package org.eclipse.dltk.internal.ui.environment;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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

	public String selectFile(Shell shell, int fileType) {
		return selectFile(shell, fileType, null);
	}

	public String selectFile(Shell shell, int fileType, String initialSelection) {
		FileDialog dialog = new FileDialog(shell);
		if (initialSelection != null && initialSelection.length() != 0) {
			IPath path = new Path(initialSelection);
			if (path.segmentCount() > 0) {
				if (path.toFile().isFile()) {
					dialog.setFilterPath(path.removeLastSegments(1)
							.toOSString());
					dialog.setFileName(path.lastSegment());
				} else {
					dialog.setFilterPath(path.toOSString());
				}
			}
		}
		if (fileType == EXECUTABLE) {
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
