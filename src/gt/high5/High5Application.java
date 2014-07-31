package gt.high5;

import gt.high5.activity.MainActivity;
import gt.high5.core.service.PreferenceReadService;
import gt.high5.database.model.TableUtils;
import gt.high5.database.tables.Time;

import java.io.File;
import java.io.IOException;

import android.app.Application;
import android.os.Environment;

import com.github.curioustechizen.xlog.Log;

public class High5Application extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// init static preferences
		Time.setRegionLength(PreferenceReadService.getPreferenceReadService(
				getApplicationContext()).getRegionLength());
		TableUtils.setDebugging(PreferenceReadService.getPreferenceReadService(
				getApplicationContext()).shouldLog(TableUtils.class));

		// init log file
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
