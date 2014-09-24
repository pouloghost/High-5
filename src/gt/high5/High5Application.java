package gt.high5;

import gt.high5.activity.SystemBroadcastReceiver;
import gt.high5.activity.widget.WidgetProvider;
import gt.high5.core.predictor.Predictor;
import gt.high5.core.service.IgnoreSetService;
import gt.high5.core.service.LogService;
import gt.high5.core.service.PreferenceService;
import gt.high5.database.raw.TimeRecordOperation;

import java.io.File;
import java.io.IOException;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.util.ArrayMap;

import com.github.curioustechizen.xlog.Log;

public class High5Application extends Application {

	private static ArrayMap<String, SharedPreferences.OnSharedPreferenceChangeListener> preferenceListeners = new ArrayMap<String, SharedPreferences.OnSharedPreferenceChangeListener>();

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

		LogService.initContext(getApplicationContext());
	}

	private void initPreferences() {
		// init static preferences
		final PreferenceService preferenceService = PreferenceService
				.getPreferenceReadService(getApplicationContext());
		TimeRecordOperation
				.setRegionLength(preferenceService.getRegionLength());
		// preference change listener
		// / TODO shared preference change listener not called
		preferenceListeners.put("update_interval",
				new SharedPreferences.OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences preferences, String key) {
						WidgetProvider.forceRefresh(getApplicationContext());
						android.util.Log.d(LogService.LOG_TAG, "changed");
					}
				});
		preferenceListeners.put("record_interval",
				new SharedPreferences.OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences preferences, String key) {
						WidgetProvider.forceRecord(getApplicationContext());
					}
				});
		preferenceListeners.put("region_length",
				new SharedPreferences.OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences preferences, String key) {
						TimeRecordOperation.setRegionLength(preferenceService
								.getRegionLength());
					}
				});
		preferenceListeners.put("predictor_class",
				new SharedPreferences.OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences preferences, String key) {
						try {
							Predictor.setPredictor(
									preferences.getString(key, ""),
									getApplicationContext());
							android.util.Log.d(LogService.LOG_TAG, Predictor
									.getPredictor().getClass().getName());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		try {
			Predictor.setPredictor(
					PreferenceService.getPreferenceReadService(
							getApplicationContext()).getString(
							"predictor_class"), getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		preferenceService
				.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences preferences, String key) {
						SharedPreferences.OnSharedPreferenceChangeListener listener = preferenceListeners
								.get(key);
						if (null != listener) {
							listener.onSharedPreferenceChanged(preferences, key);
						}
					}
				});
	}
}
