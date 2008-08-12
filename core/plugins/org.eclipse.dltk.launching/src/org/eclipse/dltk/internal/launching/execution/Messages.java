package org.eclipse.dltk.internal.launching.execution;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.internal.launching.execution.messages"; //$NON-NLS-1$
	public static String EFSDeployment_failedToDeployStream;
	public static String EFSDeployment_failedToLocateEntryForPath;
	public static String LocalExecEnvironment_failedToLocateTempFolder;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
