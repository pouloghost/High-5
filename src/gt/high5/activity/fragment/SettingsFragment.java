package gt.high5.activity.fragment;

import gt.high5.R;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
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

	private static HashSet<String> NEED_UPDATE_SUMMARY = new HashSet<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		getPreferenceManager().setSharedPreferencesMode(
				Context.MODE_MULTI_PROCESS);

		PreferenceManager
				.getDefaultSharedPreferences(getActivity())
				.registerOnSharedPreferenceChangeListener(
						new SharedPreferences.OnSharedPreferenceChangeListener() {

							@Override
							public void onSharedPreferenceChanged(
									SharedPreferences preferences, String key) {
								if (NEED_UPDATE_SUMMARY.contains(key)) {
									Preference preference = findPreference(key);
									preference
											.setSummary(((ListPreference) preference)
													.getEntry());
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
				NEED_UPDATE_SUMMARY.add(key);
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
