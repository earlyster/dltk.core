package org.eclipse.dltk.debug.ui;

import org.eclipse.jface.preference.IPreferenceStore;

public interface IDLTKDebugUILanguageToolkit {

	/**
	 * Returns the plugin debug model id.
	 */
	String getDebugModelId();

	/**
	 * Returns the <code>IPreferenceStore</code> implementation that is used to
	 * store language specific debug ui preferences.
	 */
	IPreferenceStore getPreferenceStore();
}
