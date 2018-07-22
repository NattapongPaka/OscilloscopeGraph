package com.cpeoscilloscope.activity;

import android.app.Application;

import com.cpeoscilloscope.model.UNOModel;
import com.cpeoscilloscope.model.UNOSettings;

public class OscApp extends Application {

	private static UNOModel ioioModelInstance;
	private static UNOSettings ioioSettingsInstance;
	//private static UNOService unoServiceInstance;	
	
	public static synchronized UNOModel getInstanceModel() {
		if (ioioModelInstance == null) {
			ioioModelInstance = new UNOModel();
		}
		return ioioModelInstance;
	}

	public synchronized static UNOSettings getInstanceSetting() {
		if (ioioSettingsInstance == null) {
			ioioSettingsInstance = new UNOSettings();
		}
		return ioioSettingsInstance;
	}
	
	

//	public static synchronized IOIOMainLooper getInstanceMainLooper() {
//		if (ioioMainLooperInstance == null) {
//			ioioMainLooperInstance = new IOIOMainLooper();
//		}
//		return ioioMainLooperInstance;
//	}
}
