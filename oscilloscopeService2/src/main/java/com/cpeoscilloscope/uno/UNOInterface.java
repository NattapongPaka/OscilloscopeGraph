package com.cpeoscilloscope.uno;


public interface UNOInterface {
	void readVoltPeakCH2(double peak);
	void readVoltPeakCH1(double peak);
	void readFreqCH1(double freq);
	void readFreqCH2(double freq);
	void readVoltCH1(double volt);
	//void readVoltCH1Text(double volt);
	void readVoltCH2(double volt);
	//void readVoltCH2Text(double volt);
	void readAmpCH1(double amp);
	void readAmpCH2(double amp);
	void descriptionRow1(String ch,String labelSignal,double voltAVG,String unit);
	void descriptionRow2(String ch,String labelSignal,double voltAVG,String unit);
}
