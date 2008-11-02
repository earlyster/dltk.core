package org.eclipse.dltk.core;

import org.eclipse.core.runtime.Preferences;

public class PluginPreferencesLookupDelegate implements
		IPreferencesLookupDelegate {

	private Preferences store;

	public PluginPreferencesLookupDelegate(Preferences store) {
		this.store = store;
	}

	public boolean getBoolean(String qualifier, String key) {
		return store.getBoolean(key);
	}

	public int getInt(String qualifier, String key) {
		return store.getInt(key);
	}

	public String getString(String qualifier, String key) {
		return store.getString(key);
	}
}
