package com.cpeoscilloscope.activity;

public class Fillter {

	// *****************************************************************
	private double newVal = 0.0f;
	private double simulatedVal = 0.0f;
	private double filterConstant = 0.95f; // filter constant
	private double min=0;
	private double max=0;
	private double count=1;
	// *****************************************************************

	public Fillter() {
	}

	//Ref https://www.arduino.cc/en/Reference/Map
	public double map(double x, double in_min, double in_max, double out_min,double out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public double iirFillter(double value) {
		simulatedVal = value;
		return newVal = smooth(simulatedVal, filterConstant, newVal);
	}

	private double smooth(double data, double filterVal, double smoothedVal) {
		smoothedVal = (data * (1 - filterVal)) + (smoothedVal * filterVal);
		return smoothedVal;
	}
	
	public double fillter(double value){
		if(count == 1){
			max = value;
			min = value;
			count = 0;
			
		}
		if(value > max){
			max = value;
			return max;
		}
		if(value < min){
			min = value;
			return min;
		}
		return -1;		
	}
	
	public double mean(double value){
		min = Math.min(min, value);
		max = Math.max(max, value);
		return (max + min)/2;
	}
	
}
