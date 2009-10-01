package org.eclipse.dltk.ui.wizards;

import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.ui.DLTKUIPlugin;

public class GenericDLTKProjectWizard extends ProjectWizard {
	private ProjectWizardFirstPage fFirstPage;
	private ProjectWizardSecondPage fSecondPage;
	private String nature;

	public GenericDLTKProjectWizard() {
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle(Messages.GenericDLTKProjectWizard_newDltkProject);
	}

	public String getNature() {
		return nature;
	}

	public void addPages() {
		super.addPages();
		fFirstPage = new ProjectWizardFirstPage() {

			@Override
			public String getScriptNature() {
				return nature;
			}

			@Override
			protected boolean interpeterRequired() {
				return false;
			}
		};

		// First page
		fFirstPage.setTitle(Messages.GenericDLTKProjectWizard_newDltkProject);
		fFirstPage
				.setDescription(Messages.GenericDLTKProjectWizard_createNewDltkProject);
		addPage(fFirstPage);

		// Second page
		fSecondPage = new ProjectWizardSecondPage(fFirstPage);
		addPage(fSecondPage);
	}

	/*
	 * Stores the configuration element for the wizard. The config element will
	 * be used in <code>performFinish</code> to set the result perspective.
	 */
	public void setInitializationData(IConfigurationElement cfig,
			String propertyName, Object data) {
		setInitializationData(cfig, propertyName, data);
		if (data instanceof String) {
			this.nature = (String) data;
		} else if (data instanceof Map<?, ?>) {
			this.nature = (String) ((Map<?, ?>) data).get("nature"); //$NON-NLS-1$
		}
		if (this.nature == null || this.nature.length() == 0) {
			throw new RuntimeException(
					Messages.GenericDLTKProjectWizard_natureMustBeSpecified);
		}
	}

}
