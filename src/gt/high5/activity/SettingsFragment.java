package gt.high5.activity;

import gt.high5.R;
import gt.high5.database.tables.Time;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		getPreferenceManager()
				.getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(
						new SharedPreferences.OnSharedPreferenceChangeListener() {

							@Override
							public void onSharedPreferenceChanged(
									SharedPreferences prefs, String key) {
								if ("region_length".equalsIgnoreCase(key)) {
									Time.setRegionLength(Integer.parseInt(prefs
											.getString(key, "15")));
								}

								ListPreference pref = (ListPreference) findPreference(key);
								pref.setSummary(pref.getEntry());
							}
						});
	}
}
