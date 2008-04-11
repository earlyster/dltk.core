package org.eclipse.dltk.internal.ui.environment;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.internal.ui.environment.messages"; //$NON-NLS-1$
	public static String LocalEnvironmentUI_executables;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
