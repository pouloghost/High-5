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

		LogService.initContext(getApplicationContext());
	}

	private void initPreferences() {
		// init static preferences
		final PreferenceService preferenceService = PreferenceService
				.getPreferenceReadService(getApplicationContext());
		TimeRecordOperation
				.setRegionLength(preferenceService.getRegionLength());
		try {
			Predictor.setPredictor(
					PreferenceService.getPreferenceReadService(
							getApplicationContext()).getString(
							"predictor_class"), getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// preference change listener
		// / TODO shared preference change listener not called
		preferenceService
				.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences preferences, String key) {
						if (!"update_interval".equals(key)) {
							return;
						}
						WidgetProvider.forceRefresh(getApplicationContext());
						android.util.Log.d(LogService.LOG_TAG, "changed");
					}
				});
		preferenceService
				.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences preferences, String key) {
						if (!"record_interval".equals(key)) {
							return;
						}
						WidgetProvider.forceRecord(getApplicationContext());
					}
				});
		preferenceService
				.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences preferences, String key) {
						if (!"region_length".equals(key)) {
							return;
						}
						TimeRecordOperation.setRegionLength(preferenceService
								.getRegionLength());
					}
				});
		preferenceService
				.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences preferences, String key) {
						if (!"predictor_class".equals(key)) {
							return;
						}
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

	}
}
