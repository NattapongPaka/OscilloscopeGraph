package com.cpeoscilloscope.model;

public class UNOModel {

	private int channel = -1;
	private int signalType = -1;
	private int signalMode = -1;
	private int running = -1;
	private boolean isConnect = false;
	private int auto = -1;

	/*******************************************************************
	 * 
	 * 1.Method Interface Listener
	 * 
	 *******************************************************************/
	public interface OnChannelChangerListener {
		void onChannelChangerListener(UNOModel ioioModel);
	}

	public interface OnSignalTypeChangerListener {
		void onSignalTypeChangerListener(UNOModel ioioModel);
	}

	public interface OnSignalModeChangerListener {
		void onSignalModelChangerListener(UNOModel ioioModel);
	}

	public interface OnRunningChangerListener{
		void onRunningChangerListener(UNOModel ioioModel);
	}
	
	public interface OnConnectedChangerListener{
		void onConnectedChangerListener(UNOModel ioioModel);
	}
	
	public interface OnAutoChangerListener{
		void onAutoChangerListener(UNOModel ioioModel);
	}
	
	public interface OnInitialingChangerListener{
		void onInitialingChangerListener(UNOModel ioioModel);
	}

	/*******************************************************************
	 * 
	 * 2.Call back register listener
	 * 
	 *******************************************************************/
	private OnChannelChangerListener onChannelChangerListener;
	private OnSignalTypeChangerListener onSignalTypeChangerListener;
	private OnSignalModeChangerListener onSignalModeChangerListener;
	private OnRunningChangerListener onRunningChangerListener;
	private OnConnectedChangerListener onConnectedChangerListener;
	private OnAutoChangerListener onAutoChangerListener;
	private OnInitialingChangerListener onInitialingChangerListener;
	/*******************************************************************
	 * 
	 * 3.Method Listener
	 * 
	 *******************************************************************/
	public void setOnChannelChangerListener(
			OnChannelChangerListener onChannelChangerListener) {
		this.onChannelChangerListener = onChannelChangerListener;
	}

	public void setOnSignalTypeChangerListener(
			OnSignalTypeChangerListener onSignalTypeChangerListener) {
		this.onSignalTypeChangerListener = onSignalTypeChangerListener;
	}

	public void setOnSignalModeChangerListener(
			OnSignalModeChangerListener onSignalModeChangerListener) {
		this.onSignalModeChangerListener = onSignalModeChangerListener;
	}

	public void setOnRunningChangerListener(OnRunningChangerListener onRunningChangerListener){
		this.onRunningChangerListener = onRunningChangerListener;
	}
	
	public void setOnConnectedChangerListener(OnConnectedChangerListener onConnectedChangerListener){
		this.onConnectedChangerListener = onConnectedChangerListener;
	}
	
	public void setOnAutoChangerListener(OnAutoChangerListener onAutoChangerListener){
		this.onAutoChangerListener = onAutoChangerListener;
	}
	
	public void setOnInitialingChangerListener(OnInitialingChangerListener onInitialingChangerListener){
		this.onInitialingChangerListener = onInitialingChangerListener;
	}
	/*******************************************************************
	 * 
	 * 4.Method Setter
	 * 
	 *******************************************************************/

	public void setChannel(int btn_channel) {
		this.channel = btn_channel;
		if (this.onChannelChangerListener != null) {
			this.onChannelChangerListener.onChannelChangerListener(this);
		}
	}

	public void setSignalType(int signalType) {
		this.signalType = signalType;
		if (this.onSignalTypeChangerListener != null) {
			this.onSignalTypeChangerListener.onSignalTypeChangerListener(this);
		}
	}

	public void setSignalMode(int signalMode) {
		this.signalMode = signalMode;
		if (this.onSignalModeChangerListener != null) {
			this.onSignalModeChangerListener.onSignalModelChangerListener(this);
		}
	}
	
	public void setRunning(int run){
		this.running = run;
		if(this.onRunningChangerListener != null){
			this.onRunningChangerListener.onRunningChangerListener(this);
		}
	}
	
	public void setConnected(boolean b){
		this.isConnect = b;
		if(this.onConnectedChangerListener != null){
			this.onConnectedChangerListener.onConnectedChangerListener(this);
		}
	}
	
	public void setAuto(int a){
		this.auto = a;
		if(this.onAutoChangerListener != null){
			this.onAutoChangerListener.onAutoChangerListener(this);
		}
	}
	
	public void setInitialing(){
		if(this.onInitialingChangerListener != null){
			this.onInitialingChangerListener.onInitialingChangerListener(this);
		}
	}

	/*******************************************************************
	 * 
	 * 5.Method Getter
	 * 
	 *******************************************************************/
	public int getChannel() {
		return channel;
	}

	public int getSignalType() {
		return signalType;
	}

	public int getSignalMode() {
		return signalMode;
	}
	
	public int getRunning(){
		return running;
	}
	
	public boolean getConnected(){
		return isConnect;
	}
	
	public int getAuto(){
		return auto;
	}

}
