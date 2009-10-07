package org.eclipse.dltk.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;

/**
 * @since 2.0
 */
public interface IProjectWizard extends IWizard {

	IStructuredSelection getSelection();

	IWorkbench getWorkbench();

	boolean isEnabledPage(IWizardPage page);

	IProject getProject();

	IEnvironment getEnvironment();

	void createProject();

	void removeProject();

	ProjectCreator getProjectCreator();

}
