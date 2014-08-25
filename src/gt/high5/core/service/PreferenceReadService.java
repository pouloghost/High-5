package gt.high5.core.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceReadService {

	private static PreferenceReadService mInstance = null;

	public static PreferenceReadService getPreferenceReadService(Context context) {
		if (null == mInstance) {
			synchronized (PreferenceReadService.class) {
				if (null == mInstance) {
					mInstance = new PreferenceReadService(context);
				}
			}
		}
		return mInstance;
	}

	private volatile SharedPreferences mPreferences = null;

	private PreferenceReadService(Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public int getUpdateInterval() {
		return Integer.parseInt(mPreferences.getString("update_interval",
				"600000"));
	}

	public int getRecordInterval() {
		return Integer.parseInt(mPreferences.getString("record_interval",
				"600000"));
	}

	public int getRegionLength() {
		return Integer.parseInt(mPreferences.getString("region_length", "15"));
	}

	public boolean shouldLog(Class<?> clazz) {
		String key = clazz.getSimpleName();
		return mPreferences.getBoolean(key, false);
	}

	public boolean shouldLogToFile() {
		return mPreferences.getBoolean("log_to_file", false);
	}
}
