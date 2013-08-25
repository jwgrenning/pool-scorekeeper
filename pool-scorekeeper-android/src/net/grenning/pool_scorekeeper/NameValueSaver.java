package net.grenning.pool_scorekeeper;

public interface NameValueSaver {
	void save(String name, String value);
	void save(String name, int index, String value);
	void save(String name, int value);
	void save(String name, int index, int value);
	void save(String name, boolean value);
	void save(String name, int index, boolean value);
	String getString(String name, String defaultValue);
	String getString(String name, int index, String defaultValue);
	int getInt(String name, int defaultValue);
	int getInt(String name, int index, int defaultValue);
	boolean getBoolean(String name, boolean defaultValue);
	boolean getBoolean(String name, int index, boolean defaultValue);
}
