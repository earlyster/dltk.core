package org.eclipse.dltk.debug.ui;

public abstract class AbstractDebugUILanguageToolkit implements
		IDLTKDebugUILanguageToolkit {

	/*
	 * @see org.eclipse.dltk.debug.ui.IDLTKDebugUILanguageToolkit#
	 *      getVariablesViewPreferencePages()
	 */
	public String[] getVariablesViewPreferencePages() {
		return new String[] { "" }; //$NON-NLS-1$
	}
}
