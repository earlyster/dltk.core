package org.eclipse.dltk.internal.ui.environment;

import org.eclipse.dltk.ui.environment.IEnvironmentUI;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

public class LocalEnvironmentUI implements IEnvironmentUI {

	public String selectFolder(Shell shell) {
		DirectoryDialog dialog = new DirectoryDialog(shell);
		return dialog.open();
	}

}
