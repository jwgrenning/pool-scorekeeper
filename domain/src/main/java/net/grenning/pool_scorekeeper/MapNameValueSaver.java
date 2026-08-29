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

	public String encode() {
		StringBuilder encoded = new StringBuilder();
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Integer) {
				encoded.append("I\t").append(entry.getKey()).append('\t').append(value).append('\n');
			} else if (value instanceof Boolean) {
				encoded.append("B\t").append(entry.getKey()).append('\t').append(value).append('\n');
			} else if (value instanceof String) {
				encoded.append("S\t").append(entry.getKey()).append('\t').append(value).append('\n');
			}
		}
		return encoded.toString();
	}

	public static MapNameValueSaver decode(String encoded) {
		MapNameValueSaver result = new MapNameValueSaver();
		if (encoded == null || encoded.isEmpty()) {
			return result;
		}
		String[] lines = encoded.split("\n", -1);
		for (String line : lines) {
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = line.split("\t", 3);
			if (parts.length < 3) {
				continue;
			}
			if ("I".equals(parts[0])) {
				result.save(parts[1], Integer.parseInt(parts[2]));
			} else if ("B".equals(parts[0])) {
				result.save(parts[1], Boolean.parseBoolean(parts[2]));
			} else if ("S".equals(parts[0])) {
				result.save(parts[1], parts[2]);
			}
		}
		return result;
	}
}
