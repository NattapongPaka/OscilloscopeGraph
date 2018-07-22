package com.cpeoscilloscope.uno;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.cpeoscilloscope.activity.Fillter;
import com.cpeoscilloscope.activity.OscApp;
import com.cpeoscilloscope.model.UNOModel;
import com.cpeoscilloscope.model.UNOModel.OnAutoChangerListener;
import com.cpeoscilloscope.model.UNOModel.OnChannelChangerListener;
import com.cpeoscilloscope.model.UNOModel.OnInitialingChangerListener;
import com.cpeoscilloscope.model.UNOModel.OnRunningChangerListener;
import com.cpeoscilloscope.model.UNOModel.OnSignalModeChangerListener;
import com.cpeoscilloscope.model.UNOModel.OnSignalTypeChangerListener;
import com.cpeoscilloscope.model.UNOSettings;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.hoho.android.usbserial.util.SerialInputOutputManager.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UNOService2 extends Service implements OnRunningChangerListener,
		OnChannelChangerListener, OnSignalModeChangerListener,
		OnSignalTypeChangerListener,OnAutoChangerListener,OnInitialingChangerListener,
		Listener {

	public UNOSettings unoSettings = OscApp.getInstanceSetting();
	private UNOInterface mInterface;
	private UNOModel mUnoModel = OscApp.getInstanceModel();
	public Fillter signalFillter = new Fillter();

	private Handler mHandler = new Handler();
	private StringBuilder mBuilder = new StringBuilder();
	public IBinder mBinder = new LocalBinder();
	private DeviceManager deviceManager;
	
	private int serialMax = 106;
	/*************************************************************************
	 * 
	 * Method Service
	 * 
	 **************************************************************************/

	@Override
	public void onCreate() {
		deviceManager = new DeviceManager(getApplicationContext());
		mUnoModel.setOnRunningChangerListener(this);
		mUnoModel.setOnChannelChangerListener(this);
		mUnoModel.setOnSignalModeChangerListener(this);
		mUnoModel.setOnSignalTypeChangerListener(this);
		mUnoModel.setOnAutoChangerListener(this);
		mUnoModel.setOnInitialingChangerListener(this);
		
		try {
			deviceManager.updateConfig();
			if (!deviceManager.isConnected()) {
				deviceManager.connect();
				deviceManager.startListening();
				if (deviceManager.isListening()) {
					dialog("Connection");
					mUnoModel.setInitialing();
				}
			} else {
				deviceManager.stopListening();
				deviceManager.disconnect();
				if (!deviceManager.isConnected()) {
					dialog("Disconnect");
				}
			}
		} catch (Exception e) {
			dialog(e.getMessage());
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		try {
			deviceManager.stopListening();
			deviceManager.disconnect();
		} catch (Exception e) {
			dialog(e.getMessage());
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public UNOService2 getServiceInstance() {
			return UNOService2.this;
		}
	}

	public void dialog(final String msg) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/*************************************************************************
	 * 
	 * Class Device Manager UNO R3
	 * 
	 **************************************************************************/
	private class DeviceManager {
		private static final int TIMEOUT = 1500;
		private final String TAG = DeviceManager.class.getSimpleName();
		private final ExecutorService mExecutor = Executors.newCachedThreadPool();
	
		private int baudRate;
		private int dataBits;
		private int stopBits;
		private int parity;
		private boolean swapBytes;
		private boolean allowBadBytes;
		// private DataListener dataListener;

		private Context context;
		private UsbSerialPort driver;
		private UsbDeviceConnection connection;
		private SerialInputOutputManager mSerialIoManager;

		public DeviceManager(Context context) {
			this.context = context;
		}

		private boolean sampleIsValid(int value) {
			return !(value > 4096 || value < 0);
		}

		private void connect() throws IOException {
			UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);			
			
			List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
			if (availableDrivers.isEmpty()) {
				  return;
			}
			
			UsbSerialDriver usbSerialDriver = availableDrivers.get(0);
			
			UsbDeviceConnection connection = usbManager.openDevice(usbSerialDriver.getDevice());
			
			final List<UsbSerialPort> ports = usbSerialDriver.getPorts();
			
			driver = ports.get(0);
			
			//driver = UsbSerialProber.findFirstDevice(usbManager); 
			if (connection != null) {
				Log.i(TAG, "DeviceManager found. Using driver "+ driver.getClass().getSimpleName());
				try {
					initializeDevice(connection);
				} catch (IOException e) {
					Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
					try {
						driver.close();
					} catch (IOException e2) {
						// Do nothing
					}
					driver = null;
					throw new IOException("Error setting up device: "+ e.getMessage(), e);
				}
			} else {
				Log.e(TAG, "Failed to load driver: device not attached");
				throw new IOException("Failed to load driver: device not attached");
			}
		}

		private void disconnect() throws IOException {
			if (driver != null) {
				try {
					driver.close();
					Log.i(TAG, "Disconnected");
				} catch (IOException e) {
					// Do nothing
				} finally {
					driver = null;
				}
			} else {
				Log.e(TAG,"Could not close connection: device is not connected");
				throw new IOException("Could not close connection: device is not connected");
			}
		}

		private void startListening() throws IOException {
			if (driver != null) {
				Log.i(TAG, "Starting IO manager..");
				mSerialIoManager = new SerialInputOutputManager(driver,UNOService2.this);
				mExecutor.submit(mSerialIoManager);
				Log.i(TAG, "Starting device..");
			}
		}

		private void stopListening() throws IOException {
			if (mSerialIoManager != null) {
				Log.i(TAG, "Stopping IO manager..");
				mSerialIoManager.stop();
				mSerialIoManager = null;
				Log.i(TAG, "Stopping device..");
			}
		}

		private void sendCommand(byte[] command) throws IOException {
			if (driver != null) {
				try {
					driver.write(command, TIMEOUT);
				} catch (IOException e) {
					Log.e(TAG, "Error writing to device: " + e.getMessage(), e);
					throw e;
				}
			} else {
				Log.e(TAG, "Error writing to device: no device attached");
				throw new IOException("DeviceManager not found");
			}
		}

		private boolean isConnected() {
			return driver != null;
		}

		private boolean isListening() {
			return mSerialIoManager != null;
		}

		private void updateConfig() {
			baudRate = 115200;
			dataBits = UsbSerialPort.DATABITS_8;
			stopBits = UsbSerialPort.STOPBITS_1;
			parity = UsbSerialPort.PARITY_NONE;
			swapBytes = true;
			allowBadBytes = false;
		}

		private ArrayList<String[]> parseBufferString(byte[] buffer) {
			//double volt = 0;
			try {
				String inString = new String(buffer, "UTF-8");
				mBuilder.append(inString);
				if (mBuilder.charAt(mBuilder.length() - 1) == '\n') {
					
					String analogString = mBuilder.toString();
					mBuilder.delete(0, mBuilder.length());
					
					//Re Check CMD Serial Data 
					if (analogString.indexOf("S") != -1) {
						
						// Check all channel
						int start1 = analogString.indexOf("S");
						int start2 = analogString.lastIndexOf("S");
						// One channel
						if(start1 == start2){
							//Sub string One channel
							String serial1 = analogString.substring(0, analogString.indexOf("E")+1);
							//Split , to analogArray
							String[] analogArray = serial1.trim().split(",");
							//And analogArray to array list
							if (analogArray.length == serialMax) {
								ArrayList<String[]> analogList = new ArrayList<String[]>();
								analogList.add(analogArray);
								//debugRow1("One Size : " + String.valueOf(analogArray.length) + "\n" + Arrays.toString(analogArray));
								return analogList;
							}else{
								if(analogArray.length >= serialMax && analogArray.length <= serialMax+1 && serial1.startsWith(",")){
									String split1 = serial1.replaceFirst(",", "");
									String[] analogArray11 = split1.trim().split(",");	
									ArrayList<String[]> analogList = new ArrayList<String[]>();
									analogList.add(analogArray11);
									//debugRow1("One Size : " + String.valueOf(analogArray.length) + "\n" + Arrays.toString(analogArray));
									return analogList;
								}else{
									//debugRow1("One Size Bad : "+ String.valueOf(analogArray.length) + "\n" + Arrays.toString(analogArray));
									return null;
								}
							}
							// debugRow2("One"+serial1);							
							//return null;
						}
						// Two channel
						else{
							//Substring Two channel
							String serial_1 = analogString.substring(0, analogString.indexOf("E")+1);
							String serial_2 = analogString.substring(analogString.indexOf("E")+1, analogString.lastIndexOf("E")+1);
							//Split , to analogArray
							String[] analogArray1 = serial_1.trim().split(",");
							String[] analogArray2 = serial_2.trim().split(",");
							//And analogArray to array list
							if(analogArray1.length == serialMax && analogArray2.length == serialMax){
								ArrayList<String[]> analogList1 = new ArrayList<String[]>();
								analogList1.add(analogArray1);
								analogList1.add(analogArray2);
								//debugRow2("One Two Size : "+String.valueOf(analogArray1.length) + ":"+String.valueOf(analogArray2.length) );
								return analogList1;
							}else{
							//debugRow1("One Size : "+String.valueOf(analogArray1.length));
								//debugRow2("One Two Bad Size : "+String.valueOf(analogArray1.length) + ":"+String.valueOf(analogArray2.length) );
								return null;
							}
						}
						//return null;					
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				dialog("Ex:" + e.getMessage());
			}
			return null;
		}
		
		private void parseStatus(byte[] buffer){
			try {
				String inString = new String(buffer, "UTF-8");
				mBuilder.append(inString);

					if (mBuilder.charAt(mBuilder.length() - 1) == '\n') {
						String analogString = mBuilder.toString().trim();
						
						mBuilder.delete(0, mBuilder.length());
						dialog(analogString);
					}
					
				
			} catch (Exception e) {
				dialog(e.getMessage());
			}
		}
		
//		private void parseCommand(String[] stringSerial){
//			if (stringSerial != null && stringSerial.length > 1) {		
//				int channel = Integer.parseInt(stringSerial[0]);
//				int type 	= Integer.parseInt(stringSerial[1]);
//				int mode 	= Integer.parseInt(stringSerial[2]);
//				int freq 	= Integer.parseInt(stringSerial[3]); 
//				//Update UNO CMD
//				unoSettings.setChannel(channel);
//				unoSettings.setType(type);
//				unoSettings.setMode(mode);
//				unoSettings.setFreq(freq);
//			}
//		}	
//		
//		private double parseVoltData(String[] stringSerial,int type){
//			for (int i = 4; i < stringSerial.length; i++) {
//				//double volt = 0;
//				if (parseDigit(stringSerial[i])) {					
//					if(type == UNOSettings.TYPE_AC){
//						int analogData = Integer.parseInt(stringSerial[i]);
//						double vfiller = signalFillter.map(analogData, 0, 4095, 0, 20);
//						return signalFillter.iirFillter(vfiller);
//					}
//					
//					if(type == UNOSettings.TYPE_DC){
//						int analogData = Integer.parseInt(stringSerial[i]);
//						return signalFillter.map(analogData, 0, 4095, 0, 20);
//					}
//				}
//			}
//			return 0;
//		}
//				
//		private double parseAmpData(String[] stringSerial,int mode){
//			for (int i = 4; i < stringSerial.length; i++) {
//				if (parseDigit(stringSerial[i])) {	
//					if(mode == UNOSettings.MODE_AMP){
//						int analogData = Integer.parseInt(stringSerial[i]);
//						return ((analogData / 204.75) / 0.47) - 0.01;
//						/*
//						 * analogData value range 0-4095
//						 * volt scale 	4095 / VMAX = 204.75
//						 * r divider 	= 0.47 OHM
//						 * calibrate 	= 0.01
//						 */
//					}					
//				}
//			}
//			return 0;
//		}
//		
//		private double parseFreqData(int type){
//			if(type == UNOSettings.TYPE_AC){
//				return unoSettings.getFreq();
//			}
//			return 0;
//		}

		private boolean parseDigit(String msg) {
			//boolean result = true;
			if (msg.contains(UNOSettings.START) || msg.contains("\n")|| msg.contains(UNOSettings.END) || msg.isEmpty()) {
				return false;
			}			
			return true;
		}		

		private void initializeDevice(UsbDeviceConnection con) throws IOException {
			driver.open(con);
			driver.setParameters(baudRate, dataBits, stopBits, parity);
		}
	}

	/*************************************************************************
	 * 
	 * Method Receiver data form UNO
	 * 
	 **************************************************************************/
	String LABEL_AC = "AC";
	String LABEL_DC = "DC";
	String LABEL_AMP = "AMP";
	String LABEL_TDIV = "T/DIV";
	String LABEL_VDIV = "V/DIV";
	String LABEL_UNIT_V = "V";
	String LABEL_UNIT_A = "A";
	String labelSignal;
	String labelUnit;
	//String labelMode;
	@Override
	public void onNewData(byte[] data) {
		// GET Data
		// Read Voltage and Current
		if (unoSettings.getRunning() == UNOSettings.RUN) {
			// Parse buffer
			ArrayList<String[]> analogList = deviceManager.parseBufferString(data);
			// Check stringSerial not null
			if (analogList != null) {
				for (String[] stringSerial : analogList) {
					// Start Check command
					if (stringSerial.length == serialMax) {
						int Channel = Integer.parseInt(stringSerial[0]);
						int Type = Integer.parseInt(stringSerial[1]);
						int Mode = Integer.parseInt(stringSerial[2]);
						int Freq = Integer.parseInt(stringSerial[3]);
						
						double valueBuffer = 0;
						// Check volt
						if (Mode == UNOSettings.MODE_VOLT) {
							
							for (int i = 4; i < stringSerial.length; i++) {
								if (deviceManager.parseDigit(stringSerial[i])) {
									// Filter digit
									try {
										int value = Integer.parseInt(stringSerial[i]);
										
										// double iirVolt =
										// signalFillter.iirFillter(volt);
										if (Channel == UNOSettings.CH1) {
											if(Type == UNOSettings.TYPE_AC){
												valueBuffer = signalFillter.map(value, 0,unoSettings.ANALOG_MAX, 0,unoSettings.VMAX);
												readVoltCH1(valueBuffer);
											}else if(Type == UNOSettings.TYPE_DC){
												valueBuffer = signalFillter.map(value, 0,unoSettings.ANALOG_DC_MAX, 0,unoSettings.VMAX);
												readVoltCH1(valueBuffer);
											}
										}

										if (Channel == UNOSettings.CH2) {
											if(Type == UNOSettings.TYPE_AC){
												valueBuffer = signalFillter.map(value, 0,unoSettings.ANALOG_MAX, 0,unoSettings.VMAX);
												readVoltCH2(valueBuffer);
											}else if(Type == UNOSettings.TYPE_DC){
												valueBuffer = signalFillter.map(value, 0,unoSettings.ANALOG_DC_MAX, 0,unoSettings.VMAX);
												readVoltCH2(valueBuffer);
											}
										}
									} catch (Exception ex) {
										dialog(ex.getMessage());
									}
								}
							}
						}

						// Check frequency value
						if (Type == UNOSettings.TYPE_AC && Mode == UNOSettings.MODE_VOLT) {
							labelSignal = LABEL_AC;
							labelUnit = LABEL_UNIT_V;
							if (Channel == UNOSettings.CH1) {
								readFreqCH1(Freq);
							}
							if (Channel == UNOSettings.CH2) {
								readFreqCH2(Freq);
							}
						}
						
						if(Type == UNOSettings.TYPE_DC && Mode == UNOSettings.MODE_VOLT){
							labelSignal = LABEL_DC;
							labelUnit = LABEL_UNIT_V;
						}

						// Check amp
						if (Mode == UNOSettings.MODE_AMP) {
							labelSignal = LABEL_AMP;
							labelUnit = LABEL_UNIT_A;
							for (int i = 4; i < stringSerial.length; i++) {
								if (deviceManager.parseDigit(stringSerial[i])) {
									int value = Integer.parseInt(stringSerial[i]);
									// Calculate amp value
									valueBuffer = ((value / 204.75) / 0.47) - 0.01;
									if (Channel == UNOSettings.CH1) {
										//readAmpCH1(valueBuffer);
										readVoltCH1(valueBuffer);
									}
									
									if (Channel == UNOSettings.CH2) {
										//readAmpCH2(valueBuffer);
										readVoltCH2(valueBuffer);
									}
								}
							}
						}
						// Show description
						if (Channel == UNOSettings.CH1) {
							debugRow1(String.valueOf(Channel),labelSignal, valueBuffer,labelUnit);
						}
						
						if(Channel == UNOSettings.CH2){
							debugRow2(String.valueOf(Channel),labelSignal, valueBuffer,labelUnit);
						}
					}// End Check command
				} 
			}
		} else {
			// Get config data
			deviceManager.parseStatus(data);			
			// blinkLED();
		}
	}

	@Override
	public void onRunError(Exception e) {
		Log.d(DeviceManager.class.getSimpleName(), "Runner stopped.");
	}

	/*************************************************************************
	 * 
	 * Method Call back
	 * 
	 **************************************************************************/
	public void setOnReciverListener(UNOInterface i) {
		mInterface = i;
	}

	public void readFreqCH1(double freq) {
		if (mInterface != null) {
			mInterface.readFreqCH1(freq);
		}
	}

	public void readFreqCH2(double freq) {
		if (mInterface != null) {
			mInterface.readFreqCH2(freq);
		}
	}

	public void readVoltCH1(double volt) {
		if (mInterface != null) {
			mInterface.readVoltCH1(volt);
		}
	}
	
//	public void readVoltCH1Text(double volt) {
//		if (mInterface != null) {
//			mInterface.readVoltCH1Text(volt);
//		}
//	}

	public void readVoltCH2(double volt) {
		if (mInterface != null) {
			mInterface.readVoltCH2(volt);
		}
	}
	
//	public void readVoltCH2Text(double volt) {
//		if (mInterface != null) {
//			mInterface.readVoltCH2Text(volt);
//		}
//	}

	public void readAmpCH1(double amp) {
		if (mInterface != null) {
			mInterface.readAmpCH1(amp);
		}
	}

	public void readAmpCH2(double amp) {
		if (mInterface != null) {
			mInterface.readAmpCH2(amp);
		}
	}

	public void debugRow1(String ch,String labelSignal,double voltAVG,String unit) {
		if (mInterface != null) {
			mInterface.descriptionRow1(ch,labelSignal,voltAVG,unit);
		}
	}

	public void debugRow2(String ch,String labelSignal,double voltAVG,String unit) {
		if (mInterface != null) {
			mInterface.descriptionRow2(ch,labelSignal,voltAVG,unit);
		}
	}

	/*************************************************************************
	 * 
	 * Method UNOModel Call back
	 * 
	 **************************************************************************/
	@Override
	public void onRunningChangerListener(UNOModel ioioModel) {
		try {
			unoSettings.setRunning(ioioModel.getRunning());
			deviceManager.sendCommand(String.valueOf(ioioModel.getRunning()).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSignalTypeChangerListener(UNOModel ioioModel) {
		try {
			unoSettings.setType(ioioModel.getSignalType());
			deviceManager.sendCommand(String.valueOf(ioioModel.getSignalType()).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void onSignalModelChangerListener(UNOModel ioioModel) {
		try {
			unoSettings.setMode(ioioModel.getSignalMode());
			deviceManager.sendCommand(String.valueOf(ioioModel.getSignalMode()).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void onChannelChangerListener(UNOModel ioioModel) {
		try {
			//if(unoSettings.getChannel() == UNOSettings.ALL){
			//	deviceManager.sendCommand(String.valueOf(UNOSettings.ALL).getBytes());
			//}else{
			unoSettings.setChannel(ioioModel.getChannel());
			deviceManager.sendCommand(String.valueOf(ioioModel.getChannel()).getBytes());
			//}			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void onAutoChangerListener(UNOModel ioioModel) {
		try {
			deviceManager.sendCommand(String.valueOf(ioioModel.getAuto()).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onInitialingChangerListener(UNOModel ioioModel) {
		try {
			deviceManager.sendCommand(String.valueOf(UNOSettings.CMD_INIT).getBytes());
//			if(unoSettings.getChannel() == UNOSettings.ALL){
//				deviceManager.sendCommand(String.valueOf(UNOSettings.ALL).getBytes());
//			}else{
//				unoSettings.setChannel(ioioModel.getChannel());
//				deviceManager.sendCommand(String.valueOf(ioioModel.getChannel()).getBytes());
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
