package com.roiding.rterm;

import java.util.Locale;

import android.app.ListActivity;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.roiding.rterm.util.Constants;

public class LocalePickerActivity extends ListActivity {
	private static final String TAG = "LocalePicker";

	Loc[] mLocales;

	private static class Loc {
		String label;
		Locale locale;

		public Loc(String label, Locale locale) {
			this.label = label;
			this.locale = locale;
		}

		@Override
		public String toString() {
			return this.label;
		}
	}

	int getContentView() {
		return R.layout.act_locale_picker;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(getContentView());

		String[] languages = getResources().getStringArray(R.array.languages);
		String[] languages_value = getResources().getStringArray(
				R.array.languages_value);

		mLocales = new Loc[languages.length];
		for (int i = 0; i < languages.length; i++) {
			String languageToLoad = languages_value[i];
			String[] localeStr = new String[] { languageToLoad, "" };
			if (languageToLoad.indexOf("_") > 0)
				localeStr = languageToLoad.split("_");
			Locale locale = new Locale(localeStr[0], localeStr[1]);

			mLocales[i] = new Loc(languages[i], locale);
		}
		int layoutId = R.layout.item_locale_picker;
		int fieldId = R.id.locale;
		ArrayAdapter<Loc> adapter = new ArrayAdapter<Loc>(this, layoutId,
				fieldId, mLocales);
		getListView().setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		getListView().requestFocus();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, "onListItemClick" + position);
		Loc loc = mLocales[position];
		Locale locale = loc.locale;

		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		LocalePickerActivity.this.getBaseContext().getResources()
				.updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());

		Editor pref = PreferenceManager.getDefaultSharedPreferences(this)
				.edit();
		pref.putString(Constants.SETTINGS_LANGUAGE, locale.toString());
		pref.commit();

		finish();
	}
}
