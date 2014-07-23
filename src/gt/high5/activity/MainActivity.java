package gt.high5.activity;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	public static final String LOG_TAG = "GT";

	private static boolean debugging = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public static boolean isDebugging() {
		return debugging;
	}

	public static void setDebugging(boolean debugging) {
		MainActivity.debugging = debugging;
	}

}
