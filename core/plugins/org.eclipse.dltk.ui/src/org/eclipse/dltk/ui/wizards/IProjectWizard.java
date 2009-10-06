package org.eclipse.dltk.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * @since 2.0
 */
public interface IProjectWizard extends IWizard {

	boolean isEnabledPage(IWizardPage page);

	IProject getProject();

	IEnvironment getEnvironment();

	void createProject();

	void removeProject();

	ProjectCreator getProjectCreator();

}
