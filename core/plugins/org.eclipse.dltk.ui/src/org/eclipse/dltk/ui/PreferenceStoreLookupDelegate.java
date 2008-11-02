package org.eclipse.dltk.ui;

import org.eclipse.dltk.core.IPreferencesLookupDelegate;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceStoreLookupDelegate implements
		IPreferencesLookupDelegate {

	private IPreferenceStore store;

	public PreferenceStoreLookupDelegate(IPreferenceStore store) {
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
