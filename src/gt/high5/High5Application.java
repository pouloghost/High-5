package gt.high5;

import gt.high5.activity.MainActivity;

import java.io.File;
import java.io.IOException;

import com.github.curioustechizen.xlog.Log;

import android.app.Application;
import android.os.Environment;

public class High5Application extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		//init log file
		android.util.Log.d(MainActivity.LOG_TAG, "create application");
		if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
				.getExternalStorageState())) {
			File root = Environment.getExternalStorageDirectory();
			android.util.Log.d(MainActivity.LOG_TAG,
					"root " + root.getAbsolutePath());
			File logFile = new File(root.getAbsolutePath(), "high5log.txt");
			if (!logFile.exists()) {
				try {
					logFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Log.init(this, true, logFile);
				android.util.Log.d(MainActivity.LOG_TAG,
						"inited " + logFile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			android.util.Log.d(MainActivity.LOG_TAG,
					"state " + Environment.getExternalStorageState());
		}

	}
}
