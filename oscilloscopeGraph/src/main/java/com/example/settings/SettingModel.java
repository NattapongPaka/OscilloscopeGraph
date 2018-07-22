//package com.example.settings;
//
//public class SettingModel {
//	private String graphViewID = null;
//	private String mesureViewID = null;
//	private String sourceID = null;
//	
//	private static SettingModel settingModelInstance;
//	public static String[] vDiv = {"1v","2v","3v","4v","5v","6v"};
//	public static String[] tDiv = {"1ms","2ms","3ms","4ms","5ms","6ms"};
//	/*******************************************************************
//	 * 
//	 * 1.Method Interface Listener
//	 * 
//	 *******************************************************************/
//	public interface OnGraphViewChangerListener {
//		void onGraphViewChangerListener(SettingModel settingModel);
//	}
//
//	public interface OnMesureViewChangerListener {
//		void onMesureViewChangerListener(SettingModel settingModel);
//	}
//
//	public interface OnSourceChangerListener {
//		void onSourceChangerListener(SettingModel settingModel);
//	}
//
//	/*******************************************************************
//	 * 
//	 * 2.Call back register listener
//	 * 
//	 *******************************************************************/
//	public OnGraphViewChangerListener onGraphViewChangerListener;
//	public OnMesureViewChangerListener onMesureViewChangerListener;
//	public OnSourceChangerListener onSourceChangerListener;
//
//	/*******************************************************************
//	 * 
//	 * 3.Method Listener
//	 * 
//	 *******************************************************************/
//	public void setOnGraphViewChangeListener(
//			OnGraphViewChangerListener onGraphViewChangeListener) {
//		this.onGraphViewChangerListener = onGraphViewChangeListener;
//	}
//
//	public void setOnMesureViewChangerListener(
//			OnMesureViewChangerListener onMesureViewChangerListener) {
//		this.onMesureViewChangerListener = onMesureViewChangerListener;
//	}
//
//	public void setOnSourceChangerListener(
//			OnSourceChangerListener onSourceChangerListener) {
//		this.onSourceChangerListener = onSourceChangerListener;
//	}
//	/*******************************************************************
//	 * 
//	 * 4.Method Setter
//	 * 
//	 *******************************************************************/
//	public void setGraphView(String id){
//		this.graphViewID = id;
//		if(this.onGraphViewChangerListener != null){
//			this.onGraphViewChangerListener.onGraphViewChangerListener(this);
//		}
//	}
//	
//	public void setMesureView(String id){
//		this.mesureViewID = id;
//		if(this.onMesureViewChangerListener != null){
//			this.onMesureViewChangerListener.onMesureViewChangerListener(this);
//		}
//	}
//	
//	public void setSource(String id){
//		this.sourceID = id;
//		if(this.onSourceChangerListener != null){
//			this.onSourceChangerListener.onSourceChangerListener(this);
//		}
//	}
//	/*******************************************************************
//	 * 
//	 * 5.Method Getter
//	 * 
//	 *******************************************************************/
//
//	public String getGraphViewID() {
//		return graphViewID;
//	}
//
//	public String getMesureViewID() {
//		return mesureViewID;
//	}
//
//	public String getSourceID() {
//		return sourceID;
//	}
//	
//	public static SettingModel getInstance(){
//		if(settingModelInstance == null){
//			settingModelInstance = new SettingModel();
//		}
//		return settingModelInstance;
//	}
//	
//}
