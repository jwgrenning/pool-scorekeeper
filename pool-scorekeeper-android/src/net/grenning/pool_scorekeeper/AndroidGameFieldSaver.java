package net.grenning.pool_scorekeeper;

import android.content.SharedPreferences;
import android.util.Log;

public class AndroidGameFieldSaver implements GameFieldSaver {

	SharedPreferences prefs;
	
	
	public AndroidGameFieldSaver(SharedPreferences prefs) {
		super();
		this.prefs = prefs;
//		SharedPreferences.Editor editor = prefs.edit();
		prefs.edit().putInt("junk", -42).commit();
//		editor.commit();
		int value = prefs.getInt("junk", 42);
		Log.d(this.getClass().getName(), "Create, put -42, commit, getString(" + "junk" + ", " + 42 +") = " + value);
	}

	@Override
	public void save(String name, String value) {
		SharedPreferences.Editor editor = prefs.edit();
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
	public void save(String name, boolean value) {
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
	public int getInt(String name, int defaultValue) {
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

}
