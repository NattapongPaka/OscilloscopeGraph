//package com.example.oscilloscopegraph;
//
//import android.app.Activity;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.ServiceConnection;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//
//import com.cpeoscilloscope.activity.OscApp;
//import com.cpeoscilloscope.model.UNOModel;
//import com.cpeoscilloscope.model.UNOSettings;
//import com.cpeoscilloscope.uno.UNOInterface;
//import com.cpeoscilloscope.uno.UNOService2;
//import com.cpeoscilloscope.uno.UNOService2.LocalBinder;
//
//public class MainActivityTest extends Activity implements
//		OnCheckedChangeListener, UNOInterface, OnSeekBarChangeListener {
//
//	private boolean mBounded = false;
//	private TextView display;
//	private EditText editText;
//	private Handler mHandler = new Handler();
//	
//	/*
//	 * Notifications from UsbService will be received here.
//	 */
//
//	public UNOModel unoModel = OscApp.getInstanceModel();
//	public UNOSettings unoSettings = OscApp.getInstanceSetting();
//	public UNOService2 usbService;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//		
//		display = (TextView) findViewById(R.id.textView1);
//		editText = (EditText) findViewById(R.id.editText1);
//		Button sendButton = (Button) findViewById(R.id.buttonSend);
//		sendButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (!editText.getText().toString().equals("")) {
//					String data = editText.getText().toString();
//					
//					display.append(data);
//					unoModel.setChannel(UNOSettings.ALL);
//					unoModel.setSignalType(UNOSettings.TYPE_AC);
//					unoModel.setRunning(UNOSettings.RUN);
//					
//				}
//			}
//		});
//
//		startService(new Intent(this, UNOService2.class)); // Start
//	}
//
//	@Override
//	protected void onStart() {
//		super.onStart();
//		Intent mIntent = new Intent(this, UNOService2.class);
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
//		// unregisterReceiver(mUsbReceiver);
//		unbindService(usbConnection);
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//		if (mBounded) {
//			// unregisterReceiver(mUsbReceiver);
//			unbindService(usbConnection);
//			// mPreferences.unregisterOnSharedPreferenceChangeListener(this);
//			mBounded = false;
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		if (mBounded) {
//			mBounded = false;
//			unbindService(usbConnection);
//			stopService(new Intent(this, UNOService2.class));
//		}
//		super.onDestroy();
//	}
//
//	// *************************************************************************************************************
//	// *
//	// * Service Connection
//	// *
//	// *************************************************************************************************************/
//	private final ServiceConnection usbConnection = new ServiceConnection() {
//		@Override
//		public void onServiceConnected(ComponentName arg0, IBinder service) {
//			mBounded = true;
//			LocalBinder mLocalBinder = (LocalBinder) service;
//			usbService = mLocalBinder.getServiceInstance();
//			usbService.setOnReciverListener(MainActivityTest.this);
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
//	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onStartTrackingTouch(SeekBar arg0) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onStopTrackingTouch(SeekBar arg0) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void readVoltPeakCH2(double peak) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void readVoltPeakCH1(double peak) {
//		// TODO Auto-generated method stub
//
//	}
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
//	public void readVoltCH1(double volt) {
//		// TODO Auto-generated method stub
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
//	
//
//	@Override
//	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void descriptionRow1(String ch, String labelSignal, String voltAVG) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void descriptionRow2(String ch, String labelSignal, String voltAVG) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}