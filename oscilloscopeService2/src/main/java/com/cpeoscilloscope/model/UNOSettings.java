package com.cpeoscilloscope.model;

public class UNOSettings {
	// =========================================//
	// private static IOIOSettingsSingleton instance;
	// =========================================//
	// public final int[] RL_DC = new int[]{1,0,0,0,0};
	// public final int[] RL_AC = new int[]{0,1,0,0,1};
	// public final int[] RL_AMP = new int[]{0,0,1,1,0};
	// public final int[] RL_FREQ = new int[]{0,0,0,0,0};
	// public final int[] RL_OFF = new int[]{0,0,0,0,0};
	// =========================================//
	// public static final int CH1 = 100;
	// public static final int CH2 = 101;
	// public static final int ALL = 102;
	// public static final int TYPE_DC = 1;
	// public static final int TYPE_AC = 2;
	// public static final int TYPE_FREQ = 3;
	// public static final int MODE_VOLT = 1;
	// public static final int MODE_AMP = 2;
	// public static final int RUN = 1;
	// public static final int STOP = -1;
	// public static final double SCAL_VOLT = 6.06;
	// public static final boolean isConnect = true;
	// public static final int BUFF_CAP = 512;
	// public static final int IOIO = 22;
	
	public static final int TRIGGER = 100;
	public static final int FILLTER = 101;

	public static final int CH1 = 1;
	public static final int CH2 = 2;
	public static final int ALL = 3;
	public static final int TYPE_DC = 4;
	public static final int TYPE_AC = 5;
	public static final int TYPE_FREQ = 6;
	public static final int MODE_VOLT = 7;
	public static final int MODE_AMP = 8;
	public static final int RUN = 9;
	public static final int STOP = 10;
	public static final String START = "S";
	public static final String END = "E";
	public static final int AUTO = 11;
	public static final int CMD_INIT		=12;
	public static final int CMD_GET_STATUS	=13;
	public static final int CMD_SET_STATUS	=14;
	
	//Settings default
	public int VMAX = 20;
	public int ANALOG_MAX = 4095;
	public int ANALOG_DC_MAX = 900;
	public int VDC_CALIB = 5;
	// =========================================//
	private int Channel = -1;
	private int Type = -1;
	private int Mode = -1;
	private int Running = -1;
	private double freq = -1;
	// =========================================//
	
	public double getFreq() {
		return freq;
	}		

	public int getVMAX() {
		return VMAX;
	}

	public void setVMAX(int vMAX) {
		VMAX = vMAX;
	}

	public int getANALOG_MAX() {
		return ANALOG_MAX;
	}

	public void setANALOG_MAX(int aNALOG_MAX) {
		ANALOG_MAX = aNALOG_MAX;
	}

	public int getRunning() {
		return Running;
	}

	public void setRunning(int running) {
		Running = running;
	}

	public int getChannel() {
		return Channel;
	}

	public int getType() {
		return Type;
	}

	public int getMode() {
		return Mode;
	}

	public void setChannel(int channel) {
		Channel = channel;
	}

	public void setType(int type) {
		Type = type;
	}

	public void setMode(int mode) {
		Mode = mode;
	}
	
	public void setFreq(double freq) {
		this.freq = freq;
	}

	public void setDefault() {
		Channel = CH1;
		Type = TYPE_DC;
		Mode = MODE_VOLT;
	}
}
