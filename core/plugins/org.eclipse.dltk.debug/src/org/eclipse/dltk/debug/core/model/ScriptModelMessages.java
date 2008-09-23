package org.eclipse.dltk.debug.core.model;

import org.eclipse.osgi.util.NLS;

public class ScriptModelMessages {

	private static final String BUNDLE_NAME = "org.eclipse.dltk.debug.core.model.messages"; //$NON-NLS-1$

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ScriptModelMessages.class);
	}

	private ScriptModelMessages() {
		// private constructor
	}

	public static String unknownMemoryAddress;
	public static String variableInstanceId;
}
