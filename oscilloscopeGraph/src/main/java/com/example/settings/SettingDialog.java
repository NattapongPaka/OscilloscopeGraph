package com.example.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.cpeoscilloscope.activity.OscApp;
import com.cpeoscilloscope.model.UNOModel;
import com.cpeoscilloscope.model.UNOSettings;
import com.example.oscilloscopegraph.R;

public class SettingDialog implements OnClickListener, OnCheckedChangeListener {

	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;

	private Context mContext;
	private Dialog mDialogSettings;
	
	private EditText edt_analog_max;
	private EditText edt_volt_max;

	private CheckBox chk_fillter;
	private CheckBox chk_ch1;
	private CheckBox chk_ch2;
	private CheckBox chk_trigger;
	private Button btnOK_settings;
	private Button btnClear_pref;

	private ImageButton btnIncrement;
	private ImageButton btnDecrement;
	private Button btnOK_div;
	private EditText edtDiv;
	private String[] valueDivision = new String[] {};
	private int index = 0;
	private String dialogID = "";
	private String unit="";

	public static final String KEY_CH1 = "CH1";
	public static final String KEY_CH2 = "CH2";
	public static final String KEY_TRIGGER = "Trigger";
	public static final String KEY_MODE_1X = "Mode_1X";
	public static final String KEY_MODE_10X = "Mode_10X";
	public static final String KEY_UNO = "UNO";
	public static final String KEY_FILLTER = "Fillter";
	public static final String KEY_VDIV = "VDIV";
	public static final String KEY_TDIV = "TDIV";
	public static final String KEY_ANALOG_MAX = "AnalogMax";
	public static final String KEY_VOLT_MAX = "VoltMax";

	public static final String vUnit = "V";
	public static String[] vDiv = {"1","2","5","10","20"};
	public int currentVoltDiv;
	
	public void setCurrentVoltDiv(int currentVoltDiv) {
		this.currentVoltDiv = currentVoltDiv;
	}

	//			Scald V/DIV
	//			20V = 4V
	//			10V = 2V
	//			5V = 1V
	//			2V = 0.4V
	//			1V = 0.2V
	//			0.5V = 0.1V
	//			0.2V = 0.04V
	//			0.1 = 0.02V
	//			50mV = 10mV
	//			20mV = 4mV
	//			10mV = 2mV
	//			5mV = 1mV
	public static final String tUnit = "mS";
	public static String[] tDiv = {"1","2","5","10","20"};
	public float currentTimeDiv;
	//			Scald T/DIV
	//			0.5,1,2,5,10,20,50			us
	//			0.1,0.2,0.5,1,2,5,10,20,50	ms
	//			0.1,0.2						S
	
	public int getCurrentVoltDiv() {
		return currentVoltDiv;
	}

	public float getCurrentTimeDiv() {
		return currentTimeDiv;
	}

	public static final String serial1 = "Serial1";
	public static final String serial2 = "Serial2";
	
	public static final int serial1ID = 1;
	public static final int serial2ID = 2;
	public static final int serialAllID = 3;
	
	public UNOModel unoModel = OscApp.getInstanceModel();
	public UNOSettings unoSettings = OscApp.getInstanceSetting();

	public SettingDialog(Context c) {
		mPreferences = c.getSharedPreferences("Settings", Context.MODE_PRIVATE);
		mContext = c;
	}

	public void showVoltDivision(String[] value) {
		unit = vUnit;
		dialogID = KEY_VDIV;
		mDialogSettings = new Dialog(mContext);
		mDialogSettings.setTitle("Volt Division");
		mDialogSettings.setContentView(R.layout.div_dialog);
		btnIncrement = (ImageButton) mDialogSettings.findViewById(R.id.btnDivIncrement);
		btnDecrement = (ImageButton) mDialogSettings.findViewById(R.id.btnDivDecrement);
		btnOK_div = (Button) mDialogSettings.findViewById(R.id.btnOK_div);
		edtDiv = (EditText) mDialogSettings.findViewById(R.id.edtDiv);
		btnIncrement.setOnClickListener(this);
		btnDecrement.setOnClickListener(this);
		btnOK_div.setOnClickListener(this);
		valueDivision = value;
		mDialogSettings.show();
		if(getPreferences(KEY_VDIV)){
			index = mPreferences.getInt(KEY_VDIV, 0);
			edtDiv.setText(valueDivision[index]+unit);
			currentVoltDiv = Integer.parseInt(valueDivision[index]);
			Log.i("KEY_VDIV", String.valueOf(index));
		}else{
			edtDiv.setText(valueDivision[0]+unit);
			currentVoltDiv = Integer.parseInt(valueDivision[0]);
		}
	}

	public void showTimeDivision(String[] value) {
		unit = tUnit;
		dialogID = KEY_TDIV;
		mDialogSettings = new Dialog(mContext);
		mDialogSettings.setTitle("Time Division");
		mDialogSettings.setContentView(R.layout.div_dialog);
		btnIncrement = (ImageButton) mDialogSettings.findViewById(R.id.btnDivIncrement);
		btnDecrement = (ImageButton) mDialogSettings.findViewById(R.id.btnDivDecrement);
		btnOK_div = (Button) mDialogSettings.findViewById(R.id.btnOK_div);
		edtDiv = (EditText) mDialogSettings.findViewById(R.id.edtDiv);
		btnIncrement.setOnClickListener(this);
		btnDecrement.setOnClickListener(this);
		btnOK_div.setOnClickListener(this);
		valueDivision = value;
		mDialogSettings.show();
		if(getPreferences(KEY_TDIV)){
			index = mPreferences.getInt(KEY_TDIV, 0);
			edtDiv.setText(valueDivision[index]+unit);
			Log.i("KEY_TDIV", String.valueOf(index));
		}else{
			edtDiv.setText(valueDivision[0]+unit);
		}
	}

	public void showSettings() {
		mDialogSettings = new Dialog(mContext);
		// mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialogSettings.setTitle("Settings");
		mDialogSettings.setContentView(R.layout.settings_dialog);
		mDialogSettings.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);

		chk_ch1 = (CheckBox) mDialogSettings.findViewById(R.id.chk_ch1);
		chk_ch2 = (CheckBox) mDialogSettings.findViewById(R.id.chk_ch2);
		chk_trigger = (CheckBox) mDialogSettings.findViewById(R.id.chk_trigger);
		btnOK_settings = (Button) mDialogSettings.findViewById(R.id.btnOK_settings);
		btnClear_pref = (Button) mDialogSettings.findViewById(R.id.btnClear_pref);
		chk_fillter = (CheckBox) mDialogSettings.findViewById(R.id.chk_fillter);
		edt_analog_max = (EditText) mDialogSettings.findViewById(R.id.edt_analog_max);
		edt_volt_max = (EditText) mDialogSettings.findViewById(R.id.edt_volt_max);
		chk_fillter = (CheckBox) mDialogSettings.findViewById(R.id.chk_fillter);

		chk_fillter.setOnCheckedChangeListener(this);
		chk_ch1.setOnCheckedChangeListener(this);
		chk_ch2.setOnCheckedChangeListener(this);
		chk_trigger.setOnCheckedChangeListener(this);
		btnOK_settings.setOnClickListener(this);
		btnClear_pref.setOnClickListener(this);

		setCheckBox();
		mDialogSettings.show();
	}

	public void setCheckBox() {
		if (mPreferences != null) {
			chk_ch1.setChecked(getPreferences(KEY_CH1));
			chk_ch2.setChecked(getPreferences(KEY_CH2));
			chk_trigger.setChecked(getPreferences(KEY_TRIGGER));
			chk_fillter.setChecked(getPreferences(KEY_FILLTER));
			if(getPreferences(KEY_ANALOG_MAX)){
				int analog = mPreferences.getInt(KEY_ANALOG_MAX, -1);
				edt_analog_max.setText(String.valueOf(analog));
			}
			if(getPreferences(KEY_VOLT_MAX)){
				int volt = mPreferences.getInt(KEY_VOLT_MAX, -1);
				edt_volt_max.setText(String.valueOf(volt));
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOK_settings:
			if(mPreferences != null){
				int valueCH1 = mPreferences.getInt(KEY_CH1, -1);
				int valueCH2 = mPreferences.getInt(KEY_CH2, -1);
				if (valueCH1 == UNOSettings.CH1 && valueCH2 == UNOSettings.CH2) {
					unoModel.setChannel(UNOSettings.ALL);
				} else {
					if (valueCH1 == UNOSettings.CH1)
						unoModel.setChannel(UNOSettings.CH1);
					else if (valueCH2 == UNOSettings.CH2)
						unoModel.setChannel(UNOSettings.CH2);					
				}
				//Save analog & volt
				try{
					unoSettings.setANALOG_MAX(Integer.parseInt(edt_analog_max.getText().toString()));
					unoSettings.setVMAX(Integer.parseInt(edt_volt_max.getText().toString()));
					setPreferences(KEY_ANALOG_MAX, unoSettings.getANALOG_MAX());
					setPreferences(KEY_VOLT_MAX, unoSettings.getVMAX());
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			mDialogSettings.dismiss();
			break;

		case R.id.btnDivIncrement:
			if (index < valueDivision.length - 1) {
				index++;
				edtDiv.setText(valueDivision[index]+unit);
			}
			break;

		case R.id.btnDivDecrement:
			if (index > 0) {
				index--;
				edtDiv.setText(valueDivision[index]+unit);
			}
			break;
			
		case R.id.btnOK_div:
			mDialogSettings.dismiss();
			if(dialogID.equals(KEY_VDIV)) {
				setPreferences(KEY_VDIV, index);
				currentVoltDiv = Integer.parseInt(valueDivision[index]);
			}
			else if(dialogID.equals(KEY_TDIV)) {
				setPreferences(KEY_TDIV, index);	
				currentTimeDiv = Float.parseFloat(valueDivision[index]);
				iSettingDialog.OnTimeDivSelected(valueDivision[index]+unit, valueDivision.length - (index+1));
			}
			
			break;
			
		case R.id.btnClear_pref:
			clearPreferences();
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.chk_ch1:
			setPreferences(KEY_CH1, isChecked ? UNOSettings.CH1 : 0);	
			break;
		case R.id.chk_ch2:
			setPreferences(KEY_CH2, isChecked ? UNOSettings.CH2 : 0);		
			break;
		case R.id.chk_trigger:
			setPreferences(KEY_TRIGGER, isChecked ? UNOSettings.TRIGGER : 0);
			break;
		case R.id.chk_fillter:
			setPreferences(KEY_FILLTER, isChecked ? UNOSettings.FILLTER : 0);
			break;
		}
	}

	public void setPreferences(String key, int value) {
		mEditor = mPreferences.edit();
		mEditor.putInt(key, value);
		mEditor.commit();
	}

	public void clearPreferences() {
		mEditor = mPreferences.edit();
		mEditor.clear();
		mEditor.commit();
	}

	public boolean getPreferences(String key) {
		int v = mPreferences.getInt(key, -1);
		if(v == -1 || v == 0){
			return false;
		}
		return true ;
	}
	
	ISettingDialog iSettingDialog;
	
	public interface ISettingDialog{
		void OnTimeDivSelected(String tDiv,int index);
	}
	
	public void setOnSettingDialogChangerListener(ISettingDialog iSettingDialog){
		this.iSettingDialog = iSettingDialog;
	}

}
