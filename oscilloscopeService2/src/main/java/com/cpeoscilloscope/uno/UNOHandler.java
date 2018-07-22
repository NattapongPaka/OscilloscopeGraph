package com.cpeoscilloscope.uno;

import android.os.Handler;

public class UNOHandler extends Handler {
	private static Handler mHandler;
	
	public static final int MSG_WAIT = 10;
	public static final int MSG_PROCESS = 11;
	public static final int MSG_RENDER = 12;

	public static Handler getMainHandler() {
		return mHandler;
	}	


	public UNOHandler(Handler h) {
		mHandler = h;
	}
}
