package com.roiding.rterm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.roiding.rterm.bean.Host;
import com.roiding.rterm.util.Constants;
import com.roiding.rterm.util.DBUtils;
import com.roiding.rterm.util.TerminalManager;

public class AddressBookActivity extends ListActivity {
	private static final String TAG = "AddressBook";
	private static List<Host> hosts;
	private DBUtils dbUtils;
	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String languageToLoad = pref.getString(Constants.SETTINGS_LANGUAGE,
				"en");

		Log.i(TAG, languageToLoad);

		String[] localeStr = new String[] { languageToLoad, "" };
		if (languageToLoad.indexOf("_") > 0)
			localeStr = languageToLoad.split("_");
		Locale locale = new Locale(localeStr[0], localeStr[1]);

		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());

		setContentView(R.layout.act_addressbook);
	}

	private void initHost() {
		Host h1 = new Host();
		h1.setName(getText(R.string.addressbook_site_lilacbbs).toString());
		h1.setProtocal("telnet");
		h1.setEncoding("GBK");
		h1.setHost("lilacbbs.com");
		h1.setPort(23);
		dbUtils.saveHost(h1);

		Host h2 = new Host();
		h2.setName(getText(R.string.addressbook_site_newsmth).toString());
		h2.setProtocal("telnet");
		h2.setEncoding("GBK");
		h2.setHost("newsmth.net");
		h2.setPort(23);
		dbUtils.saveHost(h2);

		Host h3 = new Host();
		h3.setName(getText(R.string.addressbook_site_lqqm).toString());
		h3.setProtocal("telnet");
		h3.setEncoding("GBK");
		h3.setHost("lqqm.net");
		h3.setPort(23);
		dbUtils.saveHost(h3);

		Host h4 = new Host();
		h4.setName(getText(R.string.addressbook_site_ptt2).toString());
		h4.setProtocal("telnet");
		h4.setEncoding("big5");
		h4.setHost("ptt2.twbbs.org");
		h4.setPort(23);
		dbUtils.saveHost(h4);

		Host h5 = new Host();
		h5.setName(getText(R.string.addressbook_site_ptt).toString());
		h5.setProtocal("telnet");
		h5.setEncoding("big5");
		h5.setHost("ptt.twbbs.org");
		h5.setPort(23);
		dbUtils.saveHost(h5);


	}

	@Override
	public void onResume() {
		super.onResume();

		if (dbUtils == null)
			dbUtils = new DBUtils(this);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (!prefs.getBoolean("INITIALIZED", false)) {
			initHost();
			Editor editor = prefs.edit();
			editor.putBoolean("INITIALIZED", true);
			editor.commit();
		}

		this.getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Host host = hosts.get(position);
				Log.i(TAG, host.getHost());

				Intent intent = new Intent();
				intent.setClass(AddressBookActivity.this,
						TerminalActivity.class);
				intent.putExtra("host", host);

				Toast.makeText(AddressBookActivity.this, host.getName(),
						Toast.LENGTH_SHORT).show();

				AddressBookActivity.this.startActivity(intent);
			}
		});
		this.registerForContextMenu(this.getListView());

		update();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		update();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem add = menu.add(R.string.addressbook_add_host).setIcon(
				android.R.drawable.ic_menu_add);

		add.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent();
				intent.setClass(AddressBookActivity.this,
						EditHostActivity.class);
				AddressBookActivity.this.startActivityForResult(intent, 0);
				return true;
			}
		});

		MenuItem settings = menu.add(R.string.addressbook_settings).setIcon(
				android.R.drawable.ic_menu_preferences);

		settings.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent();
				intent.setClass(AddressBookActivity.this,
						SettingsActivity.class);
				AddressBookActivity.this.startActivityForResult(intent, 0);
				return true;
			}
		});

		MenuItem help = menu.add(R.string.addressbook_help).setIcon(
				android.R.drawable.ic_menu_help);

		help.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent();
				intent.setClass(AddressBookActivity.this, HelpActivity.class);
				AddressBookActivity.this.startActivityForResult(intent, 0);
				return true;
			}
		});
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		final Host host = hosts.get(info.position);

		menu.setHeaderTitle(host.getName());

		MenuItem edit = menu.add(R.string.addressbook_edit_host);
		edit.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent();
				intent.setClass(AddressBookActivity.this,
						EditHostActivity.class);
				intent.putExtra("host", host);
				AddressBookActivity.this.startActivityForResult(intent, 0);
				return true;
			}
		});

		MenuItem delete = menu.add(R.string.addressbook_delete_host);
		delete.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				dbUtils.deleteHost(host);
				AddressBookActivity.this.update();
				return true;
			}
		});

	}

	protected void update() {
		if (dbUtils == null) {
			dbUtils = new DBUtils(this);
		}

		hosts = dbUtils.getHosts();

		SimpleAdapter adapter = new SimpleAdapter(this, getList(hosts),
				R.layout.item_addressbook_list, new String[] { "name", "uri",
						"icon" }, new int[] { android.R.id.text1,
						android.R.id.text2, android.R.id.icon });

		this.setListAdapter(adapter);
	}

	private List<Map<String, String>> getList(List<Host> list) {
		ArrayList<Map<String, String>> hostList = new ArrayList<Map<String, String>>();
		for (Host h : list) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", h.getName());
			String uri = h.getProtocal() + "://" + h.getHost();
			map.put("uri", uri);

			if (TerminalManager.getInstance().getView(h.getId()) == null)
				map.put("icon", String.valueOf(R.drawable.offline));
			else
				map.put("icon", String.valueOf(R.drawable.online));

			hostList.add(map);
		}
		return hostList;
	}

	@Override
	public void onStop() {
		super.onStop();

		if (dbUtils != null) {
			dbUtils.close();
			dbUtils = null;
		}
	}
}
