package net.grenning.pool_scorekeeper;

import android.content.SharedPreferences;

public class AndroidGameFieldSaver implements NameValueSaver {

	private final SharedPreferences prefs;
	private final SharedPreferences.Editor editor;

	public AndroidGameFieldSaver(SharedPreferences prefs) {
		this.prefs = prefs;
		this.editor = prefs.edit();
	}

	@Override
	public void save(String name, String value) {
		editor.putString(name, value);
	}

	@Override
	public void save(String name, int index, String value) {
		save(name + Integer.valueOf(index).toString(), value);
	}

	@Override
	public void save(String name, int value) {
		editor.putInt(name, value);
	}

	@Override
	public void save(String name, int index, int value) {
		save(name + Integer.valueOf(index).toString(), value);
	}

	@Override
	public void save(String name, boolean value) {
		editor.putBoolean(name, value);
	}

	@Override
	public void save(String name, int index, boolean value) {
		save(name + Integer.valueOf(index).toString(), value);
	}

	public void persist() {
		editor.commit();
	}

	@Override
	public String getString(String name, String defaultValue) {
		return prefs.getString(name, defaultValue);
	}

	@Override
	public String getString(String name, int index, String defaultValue) {
		return getString(name + Integer.valueOf(index).toString(), defaultValue);
	}

	@Override
	public int getInt(String name, int defaultValue) {
		return prefs.getInt(name, defaultValue);
	}

	@Override
	public int getInt(String name, int index, int defaultValue) {
		return getInt(name + Integer.valueOf(index).toString(), defaultValue);
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue) {
		return prefs.getBoolean(name, defaultValue);
	}

	@Override
	public boolean getBoolean(String name, int index, boolean defaultValue) {
		return getBoolean(name + Integer.valueOf(index).toString(), defaultValue);
	}
}
