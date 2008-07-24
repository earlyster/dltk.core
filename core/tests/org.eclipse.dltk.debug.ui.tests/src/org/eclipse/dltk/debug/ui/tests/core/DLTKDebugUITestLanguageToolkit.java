package org.eclipse.dltk.debug.ui.tests.core;

import org.eclipse.dltk.debug.ui.AbstractDebugUILanguageToolkit;
import org.eclipse.dltk.debug.ui.tests.DLTKDebugUITestsPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

public class DLTKDebugUITestLanguageToolkit extends
		AbstractDebugUILanguageToolkit {

	/*
	 * @see org.eclipse.dltk.debug.ui.IDLTKDebugUILanguageToolkit#getPreferenceStore()
	 */
	public IPreferenceStore getPreferenceStore() {
		return DLTKDebugUITestsPlugin.getDefault().getPreferenceStore();
	}

}
