package org.eclipse.dltk.debug.ui;

import org.eclipse.dltk.debug.ui.handlers.VariablesViewOptionsHandler;
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

	/**
	 * Returns an array of preference page ids whose pages will be displayed
	 * when the {@link VariablesViewOptionsHandler} is invoked.
	 */
	String[] getVariablesViewPreferencePages();
}
