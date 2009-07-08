package org.eclipse.dltk.ui.dialogs;

import org.eclipse.osgi.util.NLS;

/**
 * @since 2.0
 */
public class DialogMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.ui.dialogs.dialogMessages"; //$NON-NLS-1$
	public static String MultipleInputDialog_ignore;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, DialogMessages.class);
	}

	private DialogMessages() {
	}
}
