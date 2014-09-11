package gt.high5;

import gt.high5.activity.SystemBroadcastReceiver;
import gt.high5.core.service.IgnoreSetService;
import gt.high5.core.service.PreferenceService;
import gt.high5.database.model.TableUtils;
import gt.high5.database.raw.RawRecord;
import gt.high5.database.raw.TimeRecordOperation;

import java.io.File;
import java.io.IOException;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
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
		registerScreenReciever();
	}

	private void registerScreenReciever() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		getApplicationContext().registerReceiver(new SystemBroadcastReceiver(),
				filter);
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
									getApplicationContext()).initDefault())
					.commit();
		}
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
		TimeRecordOperation.setRegionLength(PreferenceService
				.getPreferenceReadService(getApplicationContext())
				.getRegionLength());
		TableUtils.setDebugging(PreferenceService.getPreferenceReadService(
				getApplicationContext()).shouldLog(TableUtils.class));
		RawRecord.setDebugging(PreferenceService.getPreferenceReadService(
				getApplicationContext()).shouldLog(RawRecord.class));
	}
}
