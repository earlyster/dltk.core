/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.ui.wizards.BuildpathDetector;
import org.eclipse.dltk.ui.wizards.ProjectCreator.IProjectCreateStep;
import org.eclipse.dltk.ui.wizards.ProjectCreator.ProjectCreateStep;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * As addition to the DLTKCapabilityConfigurationPage, the wizard does an early
 * project creation (so that linked folders can be defined) and, if an existing
 * external location was specified, offers to do a buildpath detection
 */
public class ProjectWizardSecondPage extends CapabilityConfigurationPage
		implements IProjectWizardPage {

	/**
	 * @since 2.0
	 */
	public static final String PAGE_NAME = "ProjectWizardSecondPage"; //$NON-NLS-1$

	private final ProjectWizardFirstPage firstPage;

	/**
	 * Constructor for ScriptProjectWizardSecondPage.
	 */
	public ProjectWizardSecondPage(ProjectWizardFirstPage firstPage) {
		super(PAGE_NAME);
		this.firstPage = firstPage;
	}

	/**
	 * @since 2.0
	 */
	protected final ProjectCreator getCreator() {
		return getProjectWizard().getProjectCreator();
	}

	@Override
	protected boolean useNewSourcePage() {
		return true;
	}

	protected final IProjectWizard getProjectWizard() {
		return (IProjectWizard) getWizard();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			try {
				getProjectWizard().createProject();
			} catch (OperationCanceledException e) {
				getShell().close();
				// TODO getContainer().showPage(getPreviousPage());
				return;
			}
		} else if (!ProjectWizardUtils.isProjectRequredFor(getContainer()
				.getCurrentPage())) {
			getProjectWizard().removeProject();
		}
		super.setVisible(visible);
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	@Deprecated
	protected final BuildpathDetector createBuildpathDetector(
			IProgressMonitor monitor, IDLTKLanguageToolkit toolkit) {
		return null;
	}

	@Override
	protected final String getScriptNature() {
		return firstPage.getScriptNature();
	}

	@Deprecated
	protected final IPreferenceStore getPreferenceStore() {
		return null;
	}

	/**
	 * @since 2.0
	 */
	public void initProjectWizardPage() {
		final ProjectCreator creator = getCreator();
		creator.addStep(IProjectCreateStep.KIND_INIT_UI,
				IProjectCreateStep.BEFORE, initStep, this);
		creator.addStep(IProjectCreateStep.KIND_FINISH,
				IProjectCreateStep.BEFORE, configureStep, this);
	}

	/**
	 * @since 2.0
	 */
	public void updateProjectWizardPage() {
		// empty
	}

	/**
	 * @since 2.0
	 */
	public void resetProjectWizardPage() {
		final IProjectWizard wizard = getProjectWizard();
		init(DLTKCore.create(wizard.getProject()), null, false);
	}

	private final IProjectCreateStep initStep = new ProjectCreateStep() {

		public void execute(IProject project, IProgressMonitor monitor)
				throws CoreException {
			final IBuildpathEntry[] entries = getCreator().initBuildpath(
					monitor);
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			init(DLTKCore.create(project), entries, false);
		}

	};

	private final IProjectCreateStep configureStep = new ProjectCreateStep() {

		public void execute(IProject project, IProgressMonitor monitor)
				throws CoreException, InterruptedException {
			configureScriptProject(monitor);
		}

		@Override
		public boolean isRecurrent() {
			return true;
		}

	};

	protected void configureNatures(IProject project, IProgressMonitor monitor)
			throws CoreException {
		((ProjectWizard) getWizard()).configureNatures(project, monitor);
	}

	@Override
	protected void configureProject(IProject project, IProgressMonitor monitor)
			throws CoreException {
		super.configureProject(project, monitor);
		((ProjectWizard) getWizard()).configureProject(project, monitor);
	}

}
