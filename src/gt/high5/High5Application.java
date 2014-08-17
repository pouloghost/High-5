package gt.high5;

import gt.high5.core.service.IgnoreSetService;
import gt.high5.core.service.PreferenceReadService;
import gt.high5.database.model.TableUtils;
import gt.high5.database.table.Time;

import java.io.File;
import java.io.IOException;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.github.curioustechizen.xlog.Log;

public class High5Application extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		initPreferences();
		initDefaultIgnore();
		initLogFile();

	}

	private void initLogFile() {
		// init log file
		if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
				.getExternalStorageState())) {
			File root = Environment.getExternalStorageDirectory();
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void initPreferences() {
		// init static preferences
		Time.setRegionLength(PreferenceReadService.getPreferenceReadService(
				getApplicationContext()).getRegionLength());
		TableUtils.setDebugging(PreferenceReadService.getPreferenceReadService(
				getApplicationContext()).shouldLog(TableUtils.class));
	}

	private void initDefaultIgnore() {
		// init default ignore list
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String key = "ignore_initialed";
		if (!prefs.getBoolean(key, false)) {
			prefs.edit()
					.putBoolean(
							key,
							IgnoreSetService.getIgnoreSetService(
									getApplicationContext()).initDefault(
									getApplicationContext())).commit();
		}
	}
}
