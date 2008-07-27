package org.eclipse.dltk.debug.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.preferences.AbstractScriptPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Base class for initialized preferences for a dltk debugging ui plug-in
 * implementation. This class will initialize all values against the preference
 * store returned by {@link IDLTKUILanguageToolkit#getPreferenceStore()}.
 * 
 * <p>
 * Any plug-in using a preference page based upon an
 * {@link AbstractScriptPreferencePage} must provide an implementation of this
 * class to properly initialize preference values.
 * </p>
 */
public abstract class DLTKDebugUIPluginPreferenceInitializer extends
		AbstractPreferenceInitializer {

	/*
	 * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		/*
		 * preferences must be saved to the following preference store, else
		 * functionality based upon the AbstractScriptPreferencePage class, or
		 * anything that uses the preferences it controls, will not work
		 * properly.
		 */
		IPreferenceStore store = DLTKDebugUILanguageManager.getLanguageToolkit(
				getNatureId()).getPreferenceStore();
		Assert.isNotNull(store);

		store.setDefault(
				IDLTKDebugUIPreferenceConstants.PREF_ACTIVE_FILTERS_LIST, ""); //$NON-NLS-1$
		store.setDefault(
				IDLTKDebugUIPreferenceConstants.PREF_INACTIVE_FILTERS_LIST, ""); //$NON-NLS-1$

		store.setDefault(IDLTKDebugUIPreferenceConstants.PREF_ALERT_HCR_FAILED,
				true);
		store.setDefault(
				IDLTKDebugUIPreferenceConstants.PREF_ALERT_HCR_NOT_SUPPORTED,
				true);

		// detail formatters
		store.setDefault(IDLTKDebugUIPreferenceConstants.PREF_SHOW_DETAILS,
				IDLTKDebugUIPreferenceConstants.DETAIL_PANE);

		initializeDefaultPreferences(store);
	}

	/**
	 * Initialize any plug-in specific preferences that should be saved to the
	 * preference store returned from a call to
	 * {@link IDLTKUILanguageToolkit#getPreferenceStore()}
	 * 
	 * <p>
	 * Sub-classes may also use this method to initialized preferences against
	 * another preference store of their choosing.
	 * </p>
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		// empty implementation
	}

	/**
	 * Returns the plug-in nature id.
	 */
	protected abstract String getNatureId();
}
