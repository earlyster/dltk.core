package org.eclipse.dltk.validators.internal.ui.popup.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.validators.internal.ui.popup.actions.messages"; //$NON-NLS-1$
	public static String DLTKValidatorsEditorContextMenu_text;
	public static String DLTKValidatorsEditorContextMenu_validateWith;
	public static String DLTKValidatorsEditorContextMenu_validatorCleanup;
	public static String DLTKValidatorsEditorContextMenu_cleanupAll;
	public static String DLTKValidatorsEditorContextMenu_validateAll;
	public static String RemoveValidatorAllMarkersAction_validatorCleanup;
	public static String ValidateSelectionWithConsoleAction_validation;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
