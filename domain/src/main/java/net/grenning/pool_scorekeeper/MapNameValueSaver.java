package net.grenning.pool_scorekeeper;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory {@link NameValueSaver} used for undo snapshots and tests.
 */
public class MapNameValueSaver implements NameValueSaver {

	private final Map<String, Object> values = new HashMap<String, Object>();

	@Override
	public void save(String name, String value) {
		values.put(name, value);
	}

	@Override
	public void save(String name, int index, String value) {
		save(name + index, value);
	}

	@Override
	public void save(String name, int value) {
		values.put(name, Integer.valueOf(value));
	}

	@Override
	public void save(String name, int index, int value) {
		save(name + index, value);
	}

	@Override
	public void save(String name, boolean value) {
		values.put(name, Boolean.valueOf(value));
	}

	@Override
	public void save(String name, int index, boolean value) {
		save(name + index, value);
	}

	@Override
	public String getString(String name, String defaultValue) {
		Object value = values.get(name);
		if (value instanceof String) {
			return (String) value;
		}
		return defaultValue;
	}

	@Override
	public String getString(String name, int index, String defaultValue) {
		return getString(name + index, defaultValue);
	}

	@Override
	public int getInt(String name, int defaultValue) {
		Object value = values.get(name);
		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		}
		return defaultValue;
	}

	@Override
	public int getInt(String name, int index, int defaultValue) {
		return getInt(name + index, defaultValue);
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue) {
		Object value = values.get(name);
		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		}
		return defaultValue;
	}

	@Override
	public boolean getBoolean(String name, int index, boolean defaultValue) {
		return getBoolean(name + index, defaultValue);
	}
}
