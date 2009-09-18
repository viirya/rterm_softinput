package com.roiding.rterm;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.roiding.rterm.util.Constants;

public class SettingsActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setPreferenceScreen(createPreferenceHierarchy());
	}

	private PreferenceScreen createPreferenceHierarchy() {
		// Root
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(
				this);

		// preferences
		PreferenceCategory normalPrefCat = new PreferenceCategory(this);
		normalPrefCat.setTitle(R.string.settings_normal_settings);
		root.addPreference(normalPrefCat);

		PreferenceScreen intentPref = getPreferenceManager()
				.createPreferenceScreen(this);
		Intent intent = new Intent();
		intent.setClass(SettingsActivity.this, LocalePickerActivity.class);
		intentPref.setIntent(intent);
		intentPref.setTitle(R.string.settings_language);
		intentPref.setSummary(R.string.settings_language_summary);
		normalPrefCat.addPreference(intentPref);

		CheckBoxPreference togglePref = new CheckBoxPreference(this);
		togglePref.setKey(Constants.SETTINGS_SHOW_STATUSBAR);
		togglePref.setTitle(R.string.settings_show_statusbar);
		togglePref.setSummary(R.string.settings_show_statusbar_summary);
		normalPrefCat.addPreference(togglePref);
		
		return root;
	}
}
