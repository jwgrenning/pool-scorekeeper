package net.grenning.pool_scorekeeper;

import android.content.SharedPreferences;
import android.util.Log;

public class AndroidGameFieldSaver implements NameValueSaver {

	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	
	public AndroidGameFieldSaver(SharedPreferences prefs) {
		super();
		this.prefs = prefs;
		editor = prefs.edit();
	}

	@Override
	public void save(String name, String value) {
		Log.d(this.getClass().getName(), "save(" + name + ", " + value +")");
		editor.putString(name, value);
		editor.commit();
	}

	@Override
	public void save(String name, int index, String value) {
		save(name + Integer.valueOf(index).toString(), value);
	}

	@Override
	public void save(String name, int value) {
		Log.d(this.getClass().getName(), "save(" + name + ", " + value +")");
		editor.putInt(name, value);
		editor.commit();
	}

	@Override
	public void save(String name, int index, int value) {
		save(name + Integer.valueOf(index).toString(), value);
	}

	@Override
	public void save(String name, boolean value) {
		Log.d(this.getClass().getName(), "save(" + name + ", " + value +")");
		prefs.edit().putBoolean(name, value);
		prefs.edit().commit();
	}

	@Override
	public void save(String name, int index, boolean value) {
		save(name + Integer.valueOf(index).toString(), value);
	}

	@Override
	public String getString(String name, String defaultValue) {
		String value = prefs.getString(name, defaultValue);
		Log.d(this.getClass().getName(), "getString(" + name + ", " + defaultValue +") = " + value);
		return value;
	}

	@Override
	public String getString(String name, int index, String defaultValue) {
		return getString(name + Integer.valueOf(index).toString(), defaultValue);
	}

	@Override
	public int getInt(String name, int defaultValue) {
		int value = prefs.getInt(name, defaultValue);
		Log.d(this.getClass().getName(), "getString(" + name + ", " + defaultValue +") = " + value);
		return value;
	}

	@Override
	public int getInt(String name, int index, int defaultValue) {
		return getInt(name + Integer.valueOf(index).toString(), defaultValue);
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue) {
		boolean value = prefs.getBoolean(name, defaultValue);
		Log.d(this.getClass().getName(), "getString(" + name + ", " + defaultValue +") = " + value);
		return value;
	}

	@Override
	public boolean getBoolean(String name, int index, boolean defaultValue) {
		return getBoolean(name + Integer.valueOf(index).toString(), defaultValue);
	}

}
