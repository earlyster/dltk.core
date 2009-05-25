package org.eclipse.dltk.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.dltk.core.environment.IEnvironment;

public class RuntimePerformanceMonitor {
	public static final String IOREAD = "IO Read";
	public static final String IOWRITE = "IO Write";

	public static boolean RUNTIME_PERFORMANCE = true;

	public static class DataEntry {
		long count = 0;
		long total = 0;
		long time = 0;

		public long getCount() {
			return count;
		}

		public long getTotal() {
			return total;
		}

		public long getTime() {
			return time;
		}
	}

	private static Map<String, Map<String, DataEntry>> entries = new HashMap<String, Map<String, DataEntry>>();

	public static synchronized void updateData(String language, String kind,
			long time, long value) {
		Map<String, DataEntry> attrs = internalGetEntries(language);
		DataEntry entry = attrs.get(kind);
		if (entry == null) {
			entry = new DataEntry();
			attrs.put(kind, entry);
		}
		entry.count++;
		entry.total += value;
		entry.time += time;
	}

	public static synchronized void updateData(String language, String kind,
			long time, long value, IEnvironment env) {
		if (env != null) {
			updateData(language, kind + " " + env.getName(), time, value);
		}
		updateData(language, kind, time, value);
	}

	private static synchronized Map<String, DataEntry> internalGetEntries(
			String language) {
		Map<String, DataEntry> attrs = entries.get(language);
		if (attrs == null) {
			attrs = new HashMap<String, DataEntry>();
			entries.put(language, attrs);
		}
		return attrs;
	}

	public static Map<String, DataEntry> getEntries(String language) {
		Map<String, DataEntry> copy = new HashMap<String, DataEntry>();
		Map<String, DataEntry> map = internalGetEntries(language);
		for (Map.Entry<String, DataEntry> i : map.entrySet()) {
			DataEntry value = i.getValue();
			DataEntry decopy = new DataEntry();
			decopy.count = value.count;
			decopy.total = value.total;
			decopy.time = value.time;
			copy.put(i.getKey(), decopy);
		}
		return copy;
	}

	public static Map<String, Map<String, DataEntry>> getAllEntries() {
		Set<String> keySet = null;
		synchronized (RuntimePerformanceMonitor.class) {
			keySet = new HashSet<String>(entries.keySet());
		}
		Map<String, Map<String, DataEntry>> result = new HashMap<String, Map<String, DataEntry>>();
		for (String key : keySet) {
			result.put(key, getEntries(key));
		}
		return result;
	}

	public static class PerformanceNode {
		private long start;
		private long end;

		public long done() {
			end = System.currentTimeMillis();
			return get();
		}

		public long get() {
			return end - start;
		}

		public void renew() {
			start = System.currentTimeMillis();
		}

		public void done(String natureId, String string, long value) {
			RuntimePerformanceMonitor.updateData(natureId, string, done(),
					value);
		}

		public void done(String natureId, String kind, long value,
				IEnvironment environment) {
			RuntimePerformanceMonitor.updateData(natureId, kind, done(), value,
					environment);
		}
	}

	public static PerformanceNode begin() {
		PerformanceNode node = new PerformanceNode();
		node.renew();
		return node;
	}

	public static synchronized void clear() {
		entries.clear();
	}
}
