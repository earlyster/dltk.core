package org.eclipse.dltk.ui.environment;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.ui.environment.messages"; //$NON-NLS-1$
	public static String EnvironmentPathBlock_environment;
	public static String EnvironmentPathBlock_path;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
