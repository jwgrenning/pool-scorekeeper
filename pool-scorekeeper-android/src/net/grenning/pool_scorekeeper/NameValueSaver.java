package net.grenning.pool_scorekeeper;

public interface NameValueSaver {
	void save(String name, String value);
	void save(String name, int value);
	void save(String name, boolean value);
	String getString(String name, String defaultValue);
	int getInt(String name, int defaultValue);
	boolean getBoolean(String name, boolean defaultValue);
}
