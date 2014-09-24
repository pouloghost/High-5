package gt.high5.activity.fragment;

import gt.high5.R;
import gt.high5.core.service.PreferenceService;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static HashSet<String> NEED_UPDATE_SUMMARY = new HashSet<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		getPreferenceManager().setSharedPreferencesMode(
				Context.MODE_MULTI_PROCESS);

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

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(
						PreferenceService.getPreferenceReadService(
								getActivity().getApplicationContext())
								.getOnSharedPreferenceChangeListener());
	}

	@Override
	public void onPause() {
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(
						PreferenceService.getPreferenceReadService(
								getActivity().getApplicationContext())
								.getOnSharedPreferenceChangeListener());
		super.onPause();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences,
			String key) {
		// update summary
		if (NEED_UPDATE_SUMMARY.contains(key)) {
			Preference preference = findPreference(key);
			preference.setSummary(((ListPreference) preference).getEntry());
		}
	}
}
