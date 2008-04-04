package org.eclipse.dltk.ui.environment;

import org.eclipse.swt.widgets.Shell;

public interface IEnvironmentUI {
	/**
	 * Open directory selection dialog. Dialog allow creation of new
	 * directories.
	 */
	String selectFolder(Shell shell);

}
