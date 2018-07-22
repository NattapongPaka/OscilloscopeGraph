//package uk.co.informaticscentre.utils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import uk.co.informaticscentre.utils.controls.SeekbarWithIntervals;
//import android.os.Bundle;
//import android.app.Activity;
//
//public class MainActivity extends Activity {
//	private SeekbarWithIntervals SeekbarWithIntervals = null;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//		
//		List<String> seekbarIntervals = getIntervals();
//		getSeekbarWithIntervals().setIntervals(seekbarIntervals);
//	}
//	
//	private List<String> getIntervals() {
//		return new ArrayList<String>() {{
//			add("1");
//			add("aaa");
//			add("3");
//			add("bbb");
//			add("5");
//			add("ccc");
//			add("7");
//			add("ddd");
//			add("9");
//		}};
//	}
//
//	private SeekbarWithIntervals getSeekbarWithIntervals() {
//		if (SeekbarWithIntervals == null) {
//			SeekbarWithIntervals = (SeekbarWithIntervals) findViewById(R.id.seekbarWithIntervals);
//		}
//		
//		return SeekbarWithIntervals;
//	}
//}
