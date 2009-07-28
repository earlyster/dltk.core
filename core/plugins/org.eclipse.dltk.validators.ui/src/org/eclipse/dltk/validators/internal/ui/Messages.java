package org.eclipse.dltk.validators.internal.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.validators.internal.ui.messages"; //$NON-NLS-1$
	public static String ValidatorsConsolePageParticipant_showCommandLine;
	public static String ValidatorConsole_terminated;
	public static String ValidatorsConsolePageParticipant_close;
	public static String ValidatorsConsolePageParticipant_closeConsole;
	public static String RemoveAllValidatorConsolesAction_text;
	public static String RemoveAllValidatorConsolesAction_toolTipText;
	public static String AbstractValidateSelectionWithConsole_dltkValidatorOutput;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
