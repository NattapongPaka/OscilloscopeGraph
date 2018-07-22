package com.example.oscilloscopegraph;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cpeoscilloscope.activity.Fillter;
import com.cpeoscilloscope.activity.OscApp;
import com.cpeoscilloscope.model.UNOModel;
import com.cpeoscilloscope.model.UNOSettings;
import com.cpeoscilloscope.uno.UNOInterface;
import com.cpeoscilloscope.uno.UNOService2;
import com.cpeoscilloscope.uno.UNOService2.LocalBinder;
import com.example.settings.SettingDialog;
import com.example.settings.SettingDialog.ISettingDialog;
import com.jjoe64.graphui.Overlay;
import com.jjoe64.graphui.OverlayChannels;
import com.jjoe64.graphui.VerticalSeekBar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.informaticscentre.utils.controls.SeekbarWithIntervals;

public class OscilloscopeActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener, UNOInterface,
		OnSeekBarChangeListener, OnSharedPreferenceChangeListener,
		ISettingDialog{

	/********************************** Handler Variable *********************************************/
	private Handler mHandler = new Handler();
	/********************************** View Variable ***********************************************/
	private TextView txt_freq;
	private TextView txt_vrms;
	private TextView txt_vpp;
	private TextView txt_vp;
	
	private TextView txt_freq2;
	private TextView txt_vrms2;
	private TextView txt_vpp2;
	private TextView txt_vp2;

	private TextView txt_row1;
	private TextView txt_row2;
	private Button btn_auto;
	private Button btn_settings;
	private ToggleButton btn_ac_dc;
	private ToggleButton btn_v_a;
	private ToggleButton btn_tdiv;
	private ToggleButton btn_vdiv;
	private Button btn_snapshort;
	private Button btn_exit;
	//private SeekBar mSeekBar1;
	private SeekBar mSeekBar2;
	private SeekbarWithIntervals SeekbarWithIntervals; 
	
	private VerticalSeekBar mVerticalSeekBar1;
	private VerticalSeekBar mVerticalSeekBar2;

	// private TextView txtNumVolt;
	// private Button btn_IncrementTime;
	// private Button btn_DecrementTime;
	// private TextView txtNumTime;

	private ToggleButton btn_stop;
//	private ToggleButton btn_trigger;

	// private ProgressDialog mProgressDialog;
	// private RelativeLayout PControlVoltDiv;
	// private RelativeLayout PControlTimeDiv;
	// private RelativeLayout layoutGridline;
	// private RelativeLayout layoutLineGraph;
	// private RelativeLayout layoutSignalCH1;

	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;

	private LinearLayout mLayout;
	private GraphView mGraphView;
	private GraphViewData[] mGraphViewDatas1;
	private GraphViewData[] mGraphViewDatas2;
	private GraphViewSeries mGraphViewSeries1;
	private GraphViewSeries mGraphViewSeries2;
	private Overlay mOverlayCursors, mOverlayChannels, mOverlayTrigger;
	private RelativeLayout mOverlayCursorsContainer, mOverlayChannelsContainer,
			mOverlayTriggerContainer, mOverlayMenuContainer;

	/********************************** Signal Variable *********************************************/
	//public Fillter signalFillter = new Fillter();
	public UNOModel unoModel = OscApp.getInstanceModel();
	public UNOSettings unoSettings = OscApp.getInstanceSetting();
	public UNOService2 usbService;
	public Fillter mFillter = new Fillter();

	private boolean mBounded = false;
	private SettingDialog mSettingDialog;

	private double graph2LastXValue;
	private int graphIndex = 0;
	private int graphMAX = 100;
	private volatile boolean isGenerator = false;

	private float amplitude = 1;
	private double w = 0;
	private float f = 0;
	private double x = 0.1;
	private double t = 0.1;
	private float num = 0;
	
	// *************************************************************************************************************
	// *
	// * Read Frequency CH 1 , 2
	// *
	// ***********************************************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oscilloscope_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		InitView();
		InitGraph();
		InitSettings();
		InitViewTrigger();
		InitDefault();

//		startService(new Intent(this, UNOService.class));
//		unoService.setOnReciverListener(this);
//		setFilters(); // Start listening notifications from UsbService
		startService(new Intent(this,UNOService2.class)); // Start
	}
	
//	private void setFilters() {
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(UNOService.ACTION_USB_PERMISSION_GRANTED);
//		filter.addAction(UNOService.ACTION_NO_USB);
//		filter.addAction(UNOService.ACTION_USB_DISCONNECTED);
//		filter.addAction(UNOService.ACTION_USB_NOT_SUPPORTED);
//		filter.addAction(UNOService.ACTION_USB_PERMISSION_NOT_GRANTED);
//		registerReceiver(mUsbReceiver, filter);
//	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("��ͧ����͡�ҡ�ͻ���पѹ�������")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								OscilloscopeActivity.super.onBackPressed();
								finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void InitDefault(){
		btn_ac_dc.setChecked(false);
		btn_v_a.setChecked(false);
	}
	
	private void InitSettings() {
		mSettingDialog = new SettingDialog(OscilloscopeActivity.this);
		mSettingDialog.setOnSettingDialogChangerListener(this);
		mPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
		mPreferences.registerOnSharedPreferenceChangeListener(this);
		
		//unoModel.setInitialing();
		
		int valueCH1 = mPreferences.getInt(SettingDialog.KEY_CH1, -1);
		int valueCH2 = mPreferences.getInt(SettingDialog.KEY_CH2, -1);
		int valueTrigger = mPreferences.getInt(SettingDialog.KEY_TRIGGER, -1);
		int vDiv = mPreferences.getInt(SettingDialog.KEY_VDIV, -1);
		
		//Set relay all channel
		if(valueCH1 == UNOSettings.CH1 && valueCH2 == UNOSettings.CH2){
			unoSettings.setChannel(UNOSettings.ALL);
			unoModel.setChannel(UNOSettings.ALL);
		}
		
//		//Set relay ch1
//		if(valueCH1 == UNOSettings.CH1 && valueCH2 == -1){
//			unoSettings.setChannel(UNOSettings.CH1);
//			unoModel.setChannel(UNOSettings.CH1);
//		}
//		
//		//Set relay ch2
//		if(valueCH2 == UNOSettings.CH2 && valueCH1 == -1){
//			unoSettings.setChannel(UNOSettings.CH2);
//			unoModel.setChannel(UNOSettings.CH2);
//		}
		
		
		if(vDiv != -1){
			mSettingDialog.setCurrentVoltDiv(vDiv);
		}
		
		viewAddOn(SettingDialog.KEY_CH1, valueCH1);
		viewAddOn(SettingDialog.KEY_CH2, valueCH2);
		viewAddOn(SettingDialog.KEY_TRIGGER, valueTrigger);		
	}

	private void InitView() {
		// Seekbar
		mSeekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		mSeekBar2.setOnSeekBarChangeListener(this);
//		SeekbarWithIntervals = (SeekbarWithIntervals) findViewById(R.id.seekbarWithIntervals);
//		List<String> mList = new ArrayList<>();
//		mList.add("0.1ms");
//		mList.add("0.2ms");
//		mList.add("0.5ms");
//		mList.add("1ms");
//		mList.add("2ms");
//		SeekbarWithIntervals.setIntervals(mList);
//		SeekbarWithIntervals.setOnSeekBarChangeListener(this);

		// Textview row 1 2
		txt_row1 = (TextView) findViewById(R.id.row1_description);
		txt_row2 = (TextView) findViewById(R.id.row2_description);

		// Textview freq vrms vpp vp channel 1
		txt_freq = (TextView) findViewById(R.id.txtFREQ);
		txt_vrms = (TextView) findViewById(R.id.txtVRMS);
		txt_vpp = (TextView) findViewById(R.id.txtVPP);
		txt_vp = (TextView) findViewById(R.id.txtVP);
		
		// Textview freq vrms vpp vp channel 2
		txt_freq2 = (TextView) findViewById(R.id.txtFREQ2);
		txt_vrms2 = (TextView) findViewById(R.id.txtVRMS2);
		txt_vpp2 = (TextView) findViewById(R.id.txtVPP2);
		txt_vp2 = (TextView) findViewById(R.id.txtVP2);

		// Init Button
		btn_auto = (Button) findViewById(R.id.btn_auto);
		btn_snapshort = (Button) findViewById(R.id.btn_snapshort);
		btn_exit = (Button) findViewById(R.id.btn_exit);

		// init ToggleButton
		btn_settings = (Button) findViewById(R.id.btn_settings);
		btn_ac_dc = (ToggleButton) findViewById(R.id.btn_ac_dc);
		btn_v_a = (ToggleButton) findViewById(R.id.btn_v_a);
		btn_tdiv = (ToggleButton) findViewById(R.id.btn_tdiv);
		btn_vdiv = (ToggleButton) findViewById(R.id.btn_vdiv);
		btn_stop = (ToggleButton) findViewById(R.id.btn_stop);
		btn_stop.setChecked(true);
//		btn_trigger = (ToggleButton) findViewById(R.id.btn_trigger);

		// Register OnClick Listener
		btn_auto.setOnClickListener(this);
		btn_snapshort.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
		btn_settings.setOnClickListener(this);

		// Register OnChecked Listener
		btn_tdiv.setOnCheckedChangeListener(this);
		btn_vdiv.setOnCheckedChangeListener(this);
		btn_ac_dc.setOnCheckedChangeListener(this);
		btn_v_a.setOnCheckedChangeListener(this);
		btn_stop.setOnCheckedChangeListener(this);
//		btn_trigger.setOnCheckedChangeListener(this);

		// Set Environment Default
		// unoSettings.setDefault();
	}
	
	private void InitViewTrigger(){
//		mOverlayCursorsContainer = (RelativeLayout) findViewById(R.id.overlay_cursors_container);
//		mOverlayCursors = (Overlay) findViewById(R.id.overlay_cursors);
//		SeekBar overlayCursorsSeekBarTop = (SeekBar) findViewById(R.id.overlay_cursors_top);
//		SeekBar overlayCursorsSeekBarBottom = (SeekBar) findViewById(R.id.overlay_cursors_bottom);
//		VerticalSeekBar overlayCursorsSeekBarLeft = (VerticalSeekBar) findViewById(R.id.overlay_cursors_left);
//		VerticalSeekBar overlayCursorsSeekBarRight = (VerticalSeekBar) findViewById(R.id.overlay_cursors_right);
//		mOverlayCursors.attachViews(overlayCursorsSeekBarTop,
//				overlayCursorsSeekBarBottom, overlayCursorsSeekBarLeft,
//				overlayCursorsSeekBarRight);

		mOverlayChannelsContainer = (RelativeLayout) findViewById(R.id.overlay_channels_container);
		mOverlayChannels = (Overlay) findViewById(R.id.overlay_channels);
//		SeekBar overlayChannelsSeekBarTop = (SeekBar) findViewById(R.id.overlay_channels_top);
//		VerticalSeekBar overlayChannelsSeekBarLeft = (VerticalSeekBar) findViewById(R.id.overlay_channels_left);
//		VerticalSeekBar overlayChannelsSeekBarRight = (VerticalSeekBar) findViewById(R.id.overlay_channels_right);
//		((VerticalSeekBarOverlay) overlayChannelsSeekBarLeft).drawGround(false).setColor(getResources().getColor(R.color.blue));
//		((VerticalSeekBarOverlay) overlayChannelsSeekBarRight).drawGround(false).setColor(getResources().getColor(R.color.red));
//		mOverlayChannels.attachViews(overlayChannelsSeekBarTop, null, overlayChannelsSeekBarLeft, overlayChannelsSeekBarRight);

//		mOverlayTriggerContainer = (RelativeLayout) findViewById(R.id.overlay_trigger_container);
//		mOverlayTrigger = (Overlay) findViewById(R.id.overlay_trigger);
//		SeekBar overlayTriggerSeekBarTop = (SeekBar) findViewById(R.id.overlay_trigger_top);
//		VerticalSeekBar overlayTriggerSeekBarLeft = (VerticalSeekBar) findViewById(R.id.overlay_trigger_left);
//		((VerticalSeekBarOverlay) overlayTriggerSeekBarLeft).drawGround(true)
//				.setColor(getResources().getColor(R.color.blue));
//		VerticalSeekBar overlayTriggerSeekBarRight = (VerticalSeekBar) findViewById(R.id.overlay_trigger_right);
//		((VerticalSeekBarOverlay) overlayTriggerSeekBarRight).drawGround(true)
//				.setColor(getResources().getColor(R.color.red));
//		mOverlayTrigger.attachViews(overlayTriggerSeekBarTop, null,overlayTriggerSeekBarLeft, overlayTriggerSeekBarRight);

		// Add Class this
//		((OverlayCursors) findViewById(R.id.overlay_cursors)).attachOsci(this);
		((OverlayChannels) findViewById(R.id.overlay_channels)).attachOsci(OscilloscopeActivity.this);
//		((OverlayChannels) findViewById(R.id.overlay_channels)).attachChannels(
//				overlayChannelsSeekBarTop, overlayChannelsSeekBarLeft,
//				overlayChannelsSeekBarRight);
//		((OverlayChannels) findViewById(R.id.overlay_channels)).attachOsci(OscilloscopeActivity.this);
//		((OverlayChannels) findViewById(R.id.overlay_channels)).attachTriggers(
//				overlayTriggerSeekBarLeft, overlayTriggerSeekBarRight,
//				overlayTriggerSeekBarTop);
	}

	private void InitGraph() {

		// Init graphview data
		mGraphViewDatas1 = new GraphViewData[graphMAX];
		mGraphViewDatas2 = new GraphViewData[graphMAX];

		// Channel 1
		GraphViewSeriesStyle mGraphViewSeriesStyle1 = new GraphViewSeriesStyle(Color.YELLOW, 2);
		mGraphViewSeries1 = new GraphViewSeries("Serial1",mGraphViewSeriesStyle1, new GraphViewData[] {});
		// Channel 2
		GraphViewSeriesStyle mGraphViewSeriesStyle2 = new GraphViewSeriesStyle(Color.GREEN, 2);
		mGraphViewSeries2 = new GraphViewSeries("Serial2",mGraphViewSeriesStyle2, new GraphViewData[] {});
		// Setup graphview option
		mGraphView = new LineGraphView(this, "CPE Oscilloscope");		

		mGraphView.setManualMaxY(true);
		mGraphView.setManualMinY(true);
		
		mGraphView.setManualYMaxBound(21);
		mGraphView.setManualYMinBound(0);
		//mGraphView.setVerticalLabels(new String[]{"4","3","2","1","0","-1","-2","-3","-4"});
		mGraphView.setVerticalLabels(new String[]{
				"10","9","8","7","6","5","4","3","2","1","0",
				"-1","-2","-3","-4","-5","-6","-7","-8","-9","-10"
		});
		
//		mGraphView.setHorizontalLabels(new String[]{
//				"1","2","3","4","5","6","7","8","9","10","11",
//				"12","13","14","15","16","17","18","19","20"
//		});
		
		mGraphView.setPadding(20, 0, 20, 0);
		
		GraphViewStyle mGraphViewStyle = new GraphViewStyle();
		mGraphViewStyle.setGridColor(Color.WHITE);
		mGraphViewStyle.setHorizontalLabelsColor(Color.WHITE);
		mGraphViewStyle.setVerticalLabelsColor(Color.WHITE);
		mGraphViewStyle.setNumVerticalLabels(21);
		mGraphViewStyle.setNumHorizontalLabels(31);
		mGraphViewStyle.setTextSize(12);
		mGraphViewStyle.setVerticalLabelsWidth(20);
		mGraphViewStyle.setVerticalLabelsAlign(Align.CENTER);
		mGraphView.setGraphViewStyle(mGraphViewStyle);
		//mGraphView.setViewPort(0, 1);
		mLayout = (LinearLayout) findViewById(R.id.graph);
		mLayout.addView(mGraphView);	
	}

//	private double graphGenerate(int min, int max) {
//		if (num < max) {
//			num += 0.3;
//		} else {
//			num = min;
//		}
//		int high = 50;
//		int low = 1;
//		// return Math.random() * (high - low) + low;
//		return Math.sin(num);
//	}

	private void graphGenerateData(int count) {
		double x = 0;
		double y = 0;
		for (int i = 0; i < count; i++) {
			y += 0.3;
			mGraphViewDatas1[i] = new GraphViewData(x, 10*Math.sin(y));
			mGraphViewDatas2[i] = new GraphViewData(x, 5 * Math.sin(y));
			x += 1;
		}
		// return Math.random() * (high - low) + low;
		// return Math.sin(num);
	}

	private String saveToInternalStorage(Bitmap bitmapImage) {

		String directory = Environment.getExternalStorageDirectory().toString();
		new File(directory + "/Oscilloscope").mkdirs();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss", Locale.getDefault());
		String dateTime = simpleDateFormat.format(new Date());
		File mypath = new File(directory, "/Oscilloscope/My_Graph" + dateTime+ ".png");

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
			// Use the compress method on the BitMap object to write image to
			// the OutputStream
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();

			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(mypath.getPath())),"image/*");
			startActivity(intent);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return directory;
	}

	// *************************************************************************************************************
	// *
	// * Read Frequency CH 1 , 2
	// *
	// **************************************************************************************************************/
	@Override
	public void readFreqCH1(final double freq) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				txt_freq.setText(String.format("1:%dHz", new BigDecimal(freq).intValue()));
			}
		}, 10);
	}

	@Override
	public void readFreqCH2(final double freq) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				txt_freq2.setText(String.format("2:%dHz", new BigDecimal(freq).intValue()));
			}
		}, 10);
	}

	// *************************************************************************************************************
	// *
	// * Read Volt CH 1 , 2
	// *
	// **************************************************************************************************************/
	@Override
	public void readVoltCH1(final double volt) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				double vpp = ((LineGraphView) mGraphView).getVoltP2P();
				readVoltPeakCH1(vpp);
				double calVoltDiv = volt / mSettingDialog.getCurrentVoltDiv();
				if (graphIndex < graphMAX) {	
					mGraphViewDatas1[graphIndex++] = new GraphViewData(graph2LastXValue, calVoltDiv);
					graph2LastXValue += 0.1;
				} else {
					mGraphViewSeries1.resetData(mGraphViewDatas1);
					graphIndex = 0;
					graph2LastXValue = 0;
				}
			}
		},100);
	}

	@Override
	public void readVoltCH2(final double volt) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				double vpp = ((LineGraphView) mGraphView).getVoltP2P();
				readVoltPeakCH2(vpp);
				double calVoltDiv = volt / mSettingDialog.getCurrentVoltDiv();
				if (graphIndex < graphMAX) {
					mGraphViewDatas2[graphIndex++] = new GraphViewData(graph2LastXValue, calVoltDiv);
					graph2LastXValue += 0.1;
				} else {
					mGraphViewSeries2.resetData(mGraphViewDatas2);
					graphIndex = 0;
					graph2LastXValue = 0;
				}
			}
		},100);
	}
	
	// *************************************************************************************************************
	// *
	// * Read Volt CH 1 , 2
	// *
	// **************************************************************************************************************/
	@Override
	public void readVoltPeakCH1(final double volt) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (unoSettings.getType() == UNOSettings.TYPE_AC) {
					String CH1 = "1:";
					double vpp = volt;
					double vp = vpp / 2;
					double vrms = vp * 0.707;
					txt_vpp.setText(CH1 + String.format("%2.2fV", vpp));
					txt_vp.setText(CH1 + String.format("%2.2fV", vp));
					txt_vrms.setText(CH1 + String.format("%2.2fV", vrms));
				}
			}
		},10);
	}

	@Override
	public void readVoltPeakCH2(final double volt) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (unoSettings.getType() == UNOSettings.TYPE_AC) {
					String CH2 = "2:";
					double vpp = volt;
					double vp = vpp / 2;
					double vrms = vp * 0.707;
					txt_vpp2.setText(CH2+String.format("%2.2fV", vpp));
					txt_vp2.setText(CH2+String.format("%2.2fV", vp));
					txt_vrms2.setText(CH2+String.format("%2.2fV",vrms));
				}				
			}
		},10);
	}

	// *************************************************************************************************************
	// *
	// * Read Amp CH 1 , 2
	// *
	// **************************************************************************************************************/
	@Override
	public void readAmpCH1(final double amp) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				
			}
		},10);
	}

	@Override
	public void readAmpCH2(final double amp) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				
			}
		},10);
	}

	// *************************************************************************************************************
	// *
	// * Display Description row 1 , 2
	// *
	// **************************************************************************************************************/
	StringBuilder mStringDescription = new StringBuilder();
	
	@Override
	public void descriptionRow1(final String ch,final String labelSignal,final double voltAVG,final String unit) {
		// CH 1 : AC : ##.##V : ## V/DIV : #.# Ms T/DIV
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mStringDescription.append("CH "+ ch);
				mStringDescription.append(" : ");
				mStringDescription.append(labelSignal);
				mStringDescription.append(" : ");
				double filter = mFillter.iirFillter(voltAVG);
				mStringDescription.append(String.format("%2.2f "+unit,new BigDecimal(filter)));
				mStringDescription.append(" : ");
				mStringDescription.append(String.format("%d V/DIV", mSettingDialog.currentVoltDiv));
				mStringDescription.append(" : ");
				mStringDescription.append(String.format("%2.2f ms T/Div", mSettingDialog.currentTimeDiv));
				txt_row1.setText(mStringDescription.toString().trim());
				mStringDescription.delete(0, mStringDescription.length());
			}
		},10);
	}

	@Override
	public void descriptionRow2(final String ch,final String labelSignal,final double voltAVG,final String unit) {
		// CH 2 : DC : XX.XX V : XX V/DIV : X.X Ms T/DIV
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mStringDescription.append("CH "+ ch);
				mStringDescription.append(" : ");
				mStringDescription.append(labelSignal);
				mStringDescription.append(" : ");
				double filter = mFillter.iirFillter(voltAVG);
				mStringDescription.append(String.format("%2.2f "+unit,new BigDecimal(filter)));
				mStringDescription.append(" : ");
				mStringDescription.append(String.format("%d V/DIV", mSettingDialog.currentVoltDiv));
				mStringDescription.append(" : ");
				mStringDescription.append(String.format("%2.2f mS T/Div", mSettingDialog.currentTimeDiv));
				txt_row2.setText(mStringDescription.toString().trim());
				mStringDescription.delete(0, mStringDescription.length());
			}
		},10);
	}
	
	@Override
	public void OnTimeDivSelected(String tDiv, final int index) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				float scal = 2.1f;
				float progress = ((index + 1)*10) * scal;
				mGraphView.setViewPort(0, x);
				mGraphViewSeries1.resetData(mGraphViewDatas1);
				mGraphViewSeries2.resetData(mGraphViewDatas2);
				// txt_row2.setText(String.valueOf(x));
				mSeekBar2.setProgress((int) progress);
				double vpp = ((LineGraphView) mGraphView).getVoltP2P();
				readVoltPeakCH1(vpp);
			}
		}, 10);
	}

	// *************************************************************************************************************
	// *
	// * Activity life Cycle
	// *
	// **************************************************************************************************************/
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mBounded = false;
		//unregisterReceiver(mUsbReceiver);
		unbindService(usbConnection);
	}

	@Override
	protected void onStart() {
		super.onStart();		
		Intent mIntent = new Intent(this,UNOService2.class);
		bindService(mIntent, usbConnection, BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(mBounded){
			//unregisterReceiver(mUsbReceiver);
			unbindService(usbConnection);
			mPreferences.unregisterOnSharedPreferenceChangeListener(this);
			mBounded = false;
		}
	}
	
	@Override
	protected void onDestroy() {
		if(mBounded){
			mBounded = false;
			//unregisterReceiver(mUsbReceiver);
			unbindService(usbConnection);
			stopService(new Intent(this,UNOService2.class));
		}
		super.onDestroy();
	}
	
	// *************************************************************************************************************
	// *
	// * Broadcast Receiver
	// *
	// *************************************************************************************************************/
//	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			switch (intent.getAction()) {
//			case UNOService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION
//															// GRANTED
//				Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
//				break;
//			case UNOService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION
//																// NOT GRANTED
//				Toast.makeText(context, "USB Permission not granted",
//						Toast.LENGTH_SHORT).show();
//				break;
//			case UNOService.ACTION_NO_USB: // NO USB CONNECTED
//				Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT)
//						.show();
//				break;
//			case UNOService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
//				Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT)
//						.show();
//				break;
//			case UNOService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
//				Toast.makeText(context, "USB device not supported",
//						Toast.LENGTH_SHORT).show();
//				break;
//			}
//		}
//	};
	

	// *************************************************************************************************************
	// *
	// * Service Connection
	// *
	// *************************************************************************************************************/
	private final ServiceConnection usbConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder service) {
			mBounded = true;
			LocalBinder mLocalBinder = (LocalBinder) service;
			usbService = mLocalBinder.getServiceInstance();
			usbService.setOnReciverListener(OscilloscopeActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBounded = false;
			usbService = null;
		}
	};

	
	// *************************************************************************************************************
	// *
	// * View event listener
	// *
	// *************************************************************************************************************/
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {

		case R.id.btn_ac_dc:
			unoModel.setSignalType(isChecked ? UNOSettings.TYPE_AC : UNOSettings.TYPE_DC);
			break;

		case R.id.btn_v_a:
			unoModel.setSignalMode(isChecked ? UNOSettings.MODE_AMP : UNOSettings.MODE_VOLT);
			break;

		case R.id.btn_stop:
			if (!isChecked) {
				unoModel.setRunning(UNOSettings.RUN);
				mGraphView.setScalable(false);
				mGraphView.setScrollable(false);
			} else {
				unoModel.setRunning(UNOSettings.STOP);
				mGraphView.setScalable(true);
				mGraphView.setScrollable(true);
			}
			break;

		case R.id.btn_vdiv:
			mSettingDialog.showVoltDivision(SettingDialog.vDiv);
			break;

		case R.id.btn_tdiv:
			mSettingDialog.showTimeDivision(SettingDialog.tDiv);
			break;
			
//		case R.id.btn_trigger:
//			mHandler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					if(btn_trigger.isChecked()){
//						Toast.makeText(OscilloscopeActivity.this, "Click", Toast.LENGTH_SHORT).show();
//						mOverlayChannelsContainer.setVisibility(View.INVISIBLE);
//						mOverlayChannels.setVisibility(View.INVISIBLE);
//					}else{
//						mOverlayChannelsContainer.setVisibility(View.VISIBLE);
//						mOverlayChannels.setVisibility(View.VISIBLE);
//					}
//					mOverlayChannelsContainer.invalidate();
//					mOverlayChannels.invalidate();					
//				}
//			}, 100);
//			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_auto:
			InitDefault();
			mGraphView.removeSeries(mGraphViewSeries2);
			mEditor = mPreferences.edit();
			mEditor.putInt(SettingDialog.KEY_CH2, 0);
			mEditor.commit();
			unoModel.setAuto(UNOSettings.AUTO);
			break;

		case R.id.btn_snapshort:
			mLayout.buildDrawingCache(true);
			saveToInternalStorage(mLayout.getDrawingCache(true));
			mLayout.destroyDrawingCache();
			break;

		case R.id.btn_exit:
			onBackPressed();
			break;

//		case R.id.btn_trigger:
//			 if (btn_trigger.isChecked()) {
//			 mOverlayCursorsContainer.setVisibility(View.INVISIBLE);
//			 mOverlayTriggerContainer.setVisibility(View.VISIBLE);
//			 } else {
//			 mOverlayCursorsContainer.setVisibility(View.VISIBLE);
//			 mOverlayTriggerContainer.setVisibility(View.INVISIBLE);
//			 }
//			 mOverlayCursorsContainer.invalidate();
//			 mOverlayTriggerContainer.invalidate();
//			break;

		case R.id.btn_settings:
			mSettingDialog.showSettings();
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		
//		if (progress == 0) {
//			x = 0.1;
//		} else {
//			x = progress * 20.22;
//			mGraphView.setViewPort(0, x);
//			mGraphViewSeries1.resetData(mGraphViewDatas1);
//			mGraphViewSeries2.resetData(mGraphViewDatas2);
//			txt_row2.setText(String.valueOf(x));
//			double vpp = ((LineGraphView) mGraphView).getVoltP2P();
//			readVoltPeakCH1(vpp);
//		}
		
		switch (seekBar.getId()) {
		case R.id.seekBar2:
			if (progress == 0) {
				x = 0.1;
			} else {
				x = progress * 0.1;
				mGraphView.setViewPort(0, x);
				mGraphViewSeries1.resetData(mGraphViewDatas1);
				mGraphViewSeries2.resetData(mGraphViewDatas2);
				//txt_row2.setText(String.valueOf(x));
				double vpp = ((LineGraphView) mGraphView).getVoltP2P();
				readVoltPeakCH1(vpp);
			}
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	// *************************************************************************************************************
	// *
	// * Generator
	// *
	// **************************************************************************************************************/

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		int value = sharedPreferences.getInt(key, -1);
		viewAddOn(key, value);
		/*
		 * value =  1 if sharedPreferences is checked
		 * value =  0 if sharedPreferences not checked
		 * value = -1 if sharedPreferences invalid value
		 */		
		Toast.makeText(OscilloscopeActivity.this,
				"KEY:" + key + "\n" + "Value:" + String.valueOf(value),
				Toast.LENGTH_SHORT).show();
	}

	private void viewAddOn(String key, int value) {
		if (key.contains(SettingDialog.KEY_CH1)) {
			if (value == 0) {
				mGraphView.removeSeries(mGraphViewSeries1);		
			} else if (value == UNOSettings.CH1) {
				graphGenerateData(graphMAX);
				//unoModel.setChannel(UNOSettings.CH1);
				mGraphView.addSeries(mGraphViewSeries1);
				mGraphViewSeries1.resetData(mGraphViewDatas1);
				graphIndex = 0;
				graph2LastXValue = 0;
			} else {
				//invalid key
			}

		} else if (key.contains(SettingDialog.KEY_CH2)) {
			if (value == 0) {
				mGraphView.removeSeries(mGraphViewSeries2);
			} else if (value == UNOSettings.CH2) {
				graphGenerateData(graphMAX);
				//unoModel.setChannel(UNOSettings.CH2);
				mGraphView.addSeries(mGraphViewSeries2);
				mGraphViewSeries2.resetData(mGraphViewDatas2);
				graphIndex = 0;
				graph2LastXValue = 0;
			} else {
				//invalid key
			}

		} else if (key.contains(SettingDialog.KEY_TRIGGER)) {
			
		} 
	}

	

	
}
