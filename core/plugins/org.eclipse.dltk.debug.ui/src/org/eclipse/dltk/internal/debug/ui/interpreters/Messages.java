package org.eclipse.dltk.internal.debug.ui.interpreters;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.internal.debug.ui.interpreters.messages"; //$NON-NLS-1$
	/**
	 * @since 2.0
	 */
	public static String EnvironmentVariableContentProvider_overwriteVariableMessage;
	/**
	 * @since 2.0
	 */
	public static String EnvironmentVariableContentProvider_overwriteVariableTitle;
	public static String EnvironmentVariablesFileUtils_incorrectFormat;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
