//package com.example.oscilloscopeservice2;
//
//import android.app.Activity;
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ScrollView;
//import android.widget.TextView;
//
//import com.cpeoscilloscope.activity.OscApp;
//import com.cpeoscilloscope.model.UNOModel;
//import com.cpeoscilloscope.model.UNOSettings;
//import com.cpeoscilloscope.uno.UNOInterface;
//import com.cpeoscilloscope.uno.UNOService;
//import com.cpeoscilloscope.uno.UNOService.LocalBinder;
//
//public class MainActivity extends Activity implements UNOInterface {
//	
//	private boolean mBounded = false;
//	private UNOService usbService;
//	public UNOSettings unoSettings = OscApp.getInstanceSetting();
//	private UNOModel mUnoModel = OscApp.getInstanceModel();
//	private TextView display;
//	private EditText editText;
//	private Handler mHandler = new Handler();
//	private ScrollView mScrollView;
////	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
////		@Override
////		public void onReceive(Context context, Intent intent) {
////			switch (intent.getAction()) {
////			case UNOService.ACTION_USB_PERMISSION_GRANTED: 	// USB PERMISSION
////														   	// GRANTED
////				Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
////				break;
////			case UNOService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION
////															   // NOT GRANTED
////				Toast.makeText(context, "USB Permission not granted",Toast.LENGTH_SHORT).show();
////				break;
////			case UNOService.ACTION_NO_USB: // NO USB CONNECTED
////				Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT)
////						.show();
////				break;
////			case UNOService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
////				Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT)
////						.show();
////				break;
////			case UNOService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
////				Toast.makeText(context, "USB device not supported",Toast.LENGTH_SHORT).show();
////				break;
////			}
////		}
////	};
//
//	private final ServiceConnection usbConnection = new ServiceConnection() {
//		@Override
//		public void onServiceConnected(ComponentName arg0, IBinder service) {
//			mBounded = true;
//			LocalBinder mLocalBinder = (LocalBinder) service;
//			usbService = mLocalBinder.getServiceInstance();
//			usbService.setOnReciverListener(MainActivity.this);
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName arg0) {
//			mBounded = false;
//			usbService = null;
//		}
//	};
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//
//		display = (TextView) findViewById(R.id.textView1);
//		editText = (EditText) findViewById(R.id.editText1);
//		mScrollView = (ScrollView) findViewById(R.id.sctextView1);
//		Button sendButton = (Button) findViewById(R.id.buttonSend);
//		sendButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (!editText.getText().toString().equals("")) {
//					String data = editText.getText().toString();
//					if (usbService != null) { 	// if UsbService was correctly
//												// binded, Send data
//						display.append(data+"\n");
//						mUnoModel.setRunning(Integer.parseInt(data));
//					}
//				}
//			}
//		});
//		
//		startService(new Intent(this,UNOService.class)); // Start
//		unoSettings.setDefault();
//	}
//	
//	@Override
//	protected void onStart() {
//		super.onStart();
//		Intent mIntent = new Intent(this,UNOService.class);
//		bindService(mIntent, usbConnection, BIND_AUTO_CREATE);
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//	}
//
//	@Override
//	public void onPause() {	
//		super.onPause();
//		mBounded = false;
//		//unregisterReceiver(mUsbReceiver);
//		unbindService(usbConnection);
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//		if(mBounded){
//			//unregisterReceiver(mUsbReceiver);
//			unbindService(usbConnection);
//			mBounded = false;
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		if(mBounded){
//			//unregisterReceiver(mUsbReceiver);
//			unbindService(usbConnection);
//			mBounded = false;
//			stopService(new Intent(this,UNOService.class));
//		}		
//		super.onDestroy();
//	}
//
////	private void setFilters() {
////		IntentFilter filter = new IntentFilter();
////		filter.addAction(UNOService.ACTION_USB_PERMISSION_GRANTED);
////		filter.addAction(UNOService.ACTION_NO_USB);
////		filter.addAction(UNOService.ACTION_USB_DISCONNECTED);
////		filter.addAction(UNOService.ACTION_USB_NOT_SUPPORTED);
////		filter.addAction(UNOService.ACTION_USB_PERMISSION_NOT_GRANTED);
////		registerReceiver(mUsbReceiver, filter);
////	}
//
//	@Override
//	public void readFreqCH1(double freq) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void readFreqCH2(double freq) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void readVoltCH1(double analogVolt) {
//	
//	}
//
//	@Override
//	public void readVoltCH2(double volt) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void readAmpCH1(double amp) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void readAmpCH2(double amp) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void descriptionRow1(final String msg) {
//		mHandler.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//
//				mScrollView.postDelayed(new Runnable() {
//
//					@Override
//					public void run() {
//						display.append(msg + ":");
//						mScrollView.fullScroll(View.FOCUS_DOWN);
//					}
//				}, 10);
//			}
//		}, 100);
//	}
//
//	@Override
//	public void descriptionRow2(String msg) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//	
//}