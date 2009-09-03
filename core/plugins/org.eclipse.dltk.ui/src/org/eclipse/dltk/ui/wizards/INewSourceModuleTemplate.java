package org.eclipse.dltk.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.swt.widgets.Composite;

/**
 * @since 2.0
 */
public interface INewSourceModuleTemplate {
	interface IValidationNotifier {
		void validate();
	}

	void createControl(Composite parent, int columns);

	boolean createSourceModule(IScriptFolder folder, String name,
			String defaultContent) throws CoreException;

	void setEnvironment(IEnvironment environment);

	void updateEnablement(boolean enabled);

	boolean isAvailable(IEnvironment environment, IScriptFolder folder);

	IStatus validate(IScriptFolder folder, String fileName);

	void setNotifier(IValidationNotifier notifier);
}
