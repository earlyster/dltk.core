package org.eclipse.dltk.ui.environment;

import org.eclipse.swt.widgets.Shell;

public interface IEnvironmentUI {
	public static final int DEFAULT = 0;
	public static final int EXECUTABLE = 1;
	public static final int ARCHIVE = 2;

	/**
	 * Open directory selection dialog. Dialog allow creation of new
	 * directories.
	 */
	String selectFolder(Shell shell);

	/**
	 * Open directory selection dialog. Dialog allow creation of new
	 * directories.
	 */
	String selectFolder(Shell shell, String initialFolder);

	/**
	 * Open file selection dialog.
	 */
	String selectFile(Shell shell, int fileType);

	/**
	 * Open file selection dialog.
	 * 
	 * @since 2.0
	 */
	String selectFile(Shell shell, int fileType, String initialSelection);

}
