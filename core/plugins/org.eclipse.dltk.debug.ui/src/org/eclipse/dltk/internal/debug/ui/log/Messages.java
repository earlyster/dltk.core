package org.eclipse.dltk.internal.debug.ui.log;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.internal.debug.ui.log.messages"; //$NON-NLS-1$
	public static String ScriptDebugLogView_clear;
	public static String ScriptDebugLogView_copy;
	public static String EventKind_Change;
	public static String EventKind_Create;
	public static String EventKind_ModelSpecific;
	public static String EventKind_Resume;
	public static String EventKind_Suspend;
	public static String EventKind_Terminate;
	public static String EventKind_Unknown;
	public static String ItemType_Input;
	public static String ItemType_Output;
	public static String ItemType_Event;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
