package com.roiding.rterm.util;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.roiding.rterm.bean.Host;

public class DBUtils extends SQLiteOpenHelper {

	public DBUtils(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public final static String DB_NAME = "rterm";
	public final static int DB_VERSION = 3;

	public final static String TABLE_HOSTS = "hosts";
	public final static String FIELD_HOSTS_ID = "_id";
	public final static String FIELD_HOSTS_NAME = "name";
	public final static String FIELD_HOSTS_PROTOCAL = "protocal";
	public final static String FIELD_HOSTS_ENCODING = "encoding";
	public final static String FIELD_HOSTS_USER = "user";
	public final static String FIELD_HOSTS_PASS = "pass";
	public final static String FIELD_HOSTS_HOST = "host";
	public final static String FIELD_HOSTS_PORT = "port";

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE " + TABLE_HOSTS
				+ " (_id INTEGER PRIMARY KEY, " + FIELD_HOSTS_NAME + " TEXT, "
				+ FIELD_HOSTS_PROTOCAL + " TEXT, " + FIELD_HOSTS_ENCODING
				+ " TEXT DEFAULT 'GBK'," + FIELD_HOSTS_USER + " TEXT, "
				+ FIELD_HOSTS_PASS + " TEXT, " + FIELD_HOSTS_HOST + " TEXT, "
				+ FIELD_HOSTS_PORT + " INTEGER)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 2:
			db.execSQL("ALTER TABLE " + TABLE_HOSTS + " ADD COLUMN "
					+ FIELD_HOSTS_ENCODING + " TEXT DEFAULT 'GBK'");

		}
	}

	public void deleteHost(Host host) {
		if (host.getId() < 0)
			return;

		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_HOSTS, "_id = ?", new String[] { String.valueOf(host
				.getId()) });
		db.close();

	}

	public void updateHost(Host host) {
		SQLiteDatabase db = this.getReadableDatabase();

		ContentValues values = host.getValues();

		db.update(TABLE_HOSTS, values, "_id =?", new String[] { String
				.valueOf(host.getId()) });

		db.close();
	}

	public Host saveHost(Host host) {
		SQLiteDatabase db = this.getWritableDatabase();

		long id = db.insert(TABLE_HOSTS, null, host.getValues());
		db.close();

		host.setId(id);
		return host;
	}

	public List<Host> getHosts() {
		List<Host> hosts = new LinkedList<Host>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(TABLE_HOSTS, null, null, null, null, null,
				FIELD_HOSTS_NAME + " ASC");

		while (c.moveToNext()) {
			Host host = new Host();

			host.setId(c.getLong(c.getColumnIndexOrThrow(FIELD_HOSTS_ID)));
			host
					.setName(c.getString(c
							.getColumnIndexOrThrow(FIELD_HOSTS_NAME)));
			host.setProtocal(c.getString(c
					.getColumnIndexOrThrow(FIELD_HOSTS_PROTOCAL)));
			host.setEncoding(c.getString(c
					.getColumnIndexOrThrow(FIELD_HOSTS_ENCODING)));
			host
					.setUser(c.getString(c
							.getColumnIndexOrThrow(FIELD_HOSTS_USER)));
			host
					.setPass(c.getString(c
							.getColumnIndexOrThrow(FIELD_HOSTS_PASS)));
			host
					.setHost(c.getString(c
							.getColumnIndexOrThrow(FIELD_HOSTS_HOST)));
			host.setPort(c.getInt(c.getColumnIndexOrThrow(FIELD_HOSTS_PORT)));

			hosts.add(host);
		}

		c.close();
		db.close();

		return hosts;
	}
}
