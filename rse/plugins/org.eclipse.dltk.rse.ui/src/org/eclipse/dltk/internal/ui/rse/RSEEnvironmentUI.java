package org.eclipse.dltk.internal.ui.rse;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.internal.rse.DLTKRSEPlugin;
import org.eclipse.dltk.core.internal.rse.RSEEnvironment;
import org.eclipse.dltk.ui.environment.IEnvironmentUI;
import org.eclipse.jface.window.Window;
import org.eclipse.rse.files.ui.dialogs.SystemRemoteFileDialog;
import org.eclipse.rse.files.ui.dialogs.SystemRemoteFolderDialog;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.subsystems.files.core.model.RemoteFileUtility;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFileSubSystem;
import org.eclipse.rse.subsystems.files.core.subsystems.RemoteFile;
import org.eclipse.swt.widgets.Shell;

public class RSEEnvironmentUI implements IEnvironmentUI {
	private RSEEnvironment environment;

	public RSEEnvironmentUI(RSEEnvironment environment) {
		this.environment = environment;
	}

	public String selectFolder(Shell shell) {
		return selectFolder(shell, null);
	}

	public String selectFolder(Shell shell, String initialFolder) {
		if (!environment.connect(true)) {
			return null;
		}
		SystemRemoteFolderDialog dialog = new SystemRemoteFolderDialog(shell);
		dialog.setDefaultSystemConnection(this.environment.getHost(), true);
		if (initialFolder != null) {
			final IRemoteFileSubSystem fs = RemoteFileUtility
					.getFileSubSystem(environment.getHost());
			if (fs != null) {
				try {
					final IRemoteFile remoteFile = fs.getRemoteFileObject(
							initialFolder, new NullProgressMonitor());
					if (remoteFile != null && remoteFile.exists()) {
						dialog.setPreSelection(remoteFile);
					}
				} catch (SystemMessageException e) {
					DLTKRSEPlugin.log(e);
				}
			}
		}
		if (dialog.open() == Window.OK) {
			Object selectedObject = dialog.getSelectedObject();
			if (selectedObject instanceof RemoteFile) {
				RemoteFile file = (RemoteFile) selectedObject;
				return file.getAbsolutePath();
			}
		}
		return null;
	}

	public String selectFile(Shell shell, int fileType) {
		return selectFile(shell, fileType, null);
	}

	/**
	 * @since 2.0
	 */
	public String selectFile(Shell shell, int fileType, String initialSelection) {
		if (!environment.connect(true)) {
			return null;
		}
		SystemRemoteFileDialog dialog = new SystemRemoteFileDialog(shell);
		dialog.setDefaultSystemConnection(this.environment.getHost(), true);
		if (initialSelection != null && initialSelection.length() != 0) {
			final IRemoteFileSubSystem fs = RemoteFileUtility
					.getFileSubSystem(environment.getHost());
			if (fs != null) {
				try {
					final IRemoteFile remoteFile = fs.getRemoteFileObject(
							initialSelection, new NullProgressMonitor());
					if (remoteFile != null && remoteFile.exists()) {
						dialog.setPreSelection(remoteFile);
					}
				} catch (SystemMessageException e) {
					DLTKRSEPlugin.log(e);
				}
			}
		}
		if (dialog.open() == Window.OK) {
			Object selectedObject = dialog.getSelectedObject();
			if (selectedObject instanceof RemoteFile) {
				RemoteFile file = (RemoteFile) selectedObject;
				return file.getAbsolutePath();
			}
		}
		return null;
	}
}
