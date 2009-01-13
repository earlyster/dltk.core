package org.eclipse.dltk.internal.ui.environment;

import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ui.environment.IEnvironmentUI;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class LocalEnvironmentUI implements IEnvironmentUI {

	public String selectFolder(Shell shell) {
		return selectFolder(shell, null);
	}

	public String selectFolder(Shell shell, String initialFolder) {
		DirectoryDialog dialog = new DirectoryDialog(shell);
		if (initialFolder != null) {
			dialog.setFilterPath(initialFolder);
		}
		return dialog.open();
	}

	public String selectFile(Shell shell, int executable) {
		FileDialog dialog = new FileDialog(shell);
		if (executable == EXECUTABLE) {
			if (Platform.getOS().equals(Platform.OS_WIN32)) {
				dialog
						.setFilterExtensions(new String[] { "*.exe;*.bat;*.exe" }); //$NON-NLS-1$
			} else {
				dialog.setFilterExtensions(new String[] { "*" }); //$NON-NLS-1$
			}
			dialog
					.setFilterNames(new String[] { Messages.LocalEnvironmentUI_executables });
		}
		return dialog.open();
	}

}
