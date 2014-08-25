package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.database.table.Time;

import java.util.Set;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
								Preference pref = null;
								if ((pref = findPreference(key)) instanceof ListPreference) {
									pref.setSummary(((ListPreference) pref)
											.getEntry());
									try {
										getListView().invalidate();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Set<String> keySet = PreferenceManager
				.getDefaultSharedPreferences(getActivity()).getAll().keySet();
		for (String key : keySet) {
			Preference preference = findPreference(key);
			if (preference instanceof ListPreference) {
				preference.setSummary(((ListPreference) preference).getEntry());
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
