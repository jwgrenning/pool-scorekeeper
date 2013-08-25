package net.grenning.pool_scorekeeper;

import android.content.SharedPreferences;
import android.util.Log;

public class AndroidGameFieldSaver implements NameValueSaver {

	SharedPreferences prefs;
	
	
	public AndroidGameFieldSaver(SharedPreferences prefs) {
		super();
		this.prefs = prefs;
	}

	@Override
	public void save(String name, String value) {
		SharedPreferences.Editor editor = prefs.edit();
		Log.d(this.getClass().getName(), "save(" + name + ", " + value +")");
		editor.putString(name, value);
		editor.commit();
	}

	@Override
	public void save(String name, int index, String value) {
		SharedPreferences.Editor editor = prefs.edit();
		name += Integer.valueOf(index).toString();
		Log.d(this.getClass().getName(), "save(" + name + ", " + value +")");
		editor.putString(name, value);
		editor.commit();
	}

	@Override
	public void save(String name, int value) {
		SharedPreferences.Editor editor = prefs.edit();
		Log.d(this.getClass().getName(), "save(" + name + ", " + value +")");
		editor.putInt(name, value);
		editor.commit();
	}

	@Override
	public void save(String name, int index, int value) {
		SharedPreferences.Editor editor = prefs.edit();
		name += Integer.valueOf(index).toString();
		Log.d(this.getClass().getName(), "save(" + name + ", " + value +")");
		editor.putInt(name, value);
		editor.commit();
	}

	@Override
	public void save(String name, boolean value) {
		Log.d(this.getClass().getName(), "save(" + name + ", " + value +")");
		prefs.edit().putBoolean(name, value);
		prefs.edit().commit();
	}

	@Override
	public void save(String name, int index, boolean value) {
		name += Integer.valueOf(index).toString();
		Log.d(this.getClass().getName(), "save(" + name + ", " + value +")");
		prefs.edit().putBoolean(name, value);
		prefs.edit().commit();
	}

	@Override
	public String getString(String name, String defaultValue) {
		String value = prefs.getString(name, defaultValue);
		Log.d(this.getClass().getName(), "getString(" + name + ", " + defaultValue +") = " + value);
		return value;
	}

	@Override
	public String getString(String name, int index, String defaultValue) {
		name += Integer.valueOf(index).toString();
		String value = prefs.getString(name, defaultValue);
		Log.d(this.getClass().getName(), "getString(" + name + ", " + defaultValue +") = " + value);
		return value;
	}

	@Override
	public int getInt(String name, int defaultValue) {
		int value = prefs.getInt(name, defaultValue);
		Log.d(this.getClass().getName(), "getString(" + name + ", " + defaultValue +") = " + value);
		return value;
	}

	@Override
	public int getInt(String name, int index, int defaultValue) {
		name += Integer.valueOf(index).toString();
		int value = prefs.getInt(name, defaultValue);
		Log.d(this.getClass().getName(), "getString(" + name + ", " + defaultValue +") = " + value);
		return value;
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue) {
		boolean value = prefs.getBoolean(name, defaultValue);
		Log.d(this.getClass().getName(), "getString(" + name + ", " + defaultValue +") = " + value);
		return value;
	}

	@Override
	public boolean getBoolean(String name, int index, boolean defaultValue) {
		name += Integer.valueOf(index).toString();
		boolean value = prefs.getBoolean(name, defaultValue);
		Log.d(this.getClass().getName(), "getString(" + name + ", " + defaultValue +") = " + value);
		return value;
	}

}
