package gt.high5.core.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceReadService {

	private static PreferenceReadService instance = null;

	public static PreferenceReadService getPreferenceReadService(Context context) {
		if (null == instance) {
			instance = new PreferenceReadService(context);
		}
		return instance;
	}

	private SharedPreferences preferences = null;

	private PreferenceReadService(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public int getUpdateInterval() {
		return Integer.parseInt(preferences.getString("update_interval",
				"600000"));
	}

	public int getRecordInterval() {
		return Integer.parseInt(preferences.getString("record_interval",
				"600000"));
	}

	public int getRegionLength() {
		return Integer.parseInt(preferences.getString("region_length", "15"));
	}

	public boolean shouldLog(Class<?> clazz) {
		String key = clazz.getSimpleName();
		return preferences.getBoolean(key, false);
	}

}
