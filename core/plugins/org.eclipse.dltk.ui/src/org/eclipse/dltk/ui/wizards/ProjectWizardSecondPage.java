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
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * As addition to the DLTKCapabilityConfigurationPage, the wizard does an early
 * project creation (so that linked folders can be defined) and, if an existing
 * external location was specified, offers to do a buildpath detection
 */
public class ProjectWizardSecondPage extends CapabilityConfigurationPage
		implements IProjectWizardPage {

	private final ProjectCreator fCreator;

	/**
	 * @since 2.0
	 */
	public static final String PAGE_NAME = "ProjectWizardSecondPage"; //$NON-NLS-1$

	/**
	 * Constructor for ScriptProjectWizardSecondPage.
	 */
	public ProjectWizardSecondPage(ProjectWizardFirstPage firstPage) {
		super(PAGE_NAME);
		fCreator = new ProjectCreator((IProjectWizard) firstPage.getWizard(),
				firstPage);
	}

	/**
	 * @since 2.0
	 */
	public ProjectCreator getCreator() {
		return fCreator;
	}

	@Override
	protected boolean useNewSourcePage() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			((IProjectWizard) getWizard()).createProject();
		} else if (!ProjectWizardUtils.isProjectRequredFor(getContainer()
				.getCurrentPage())) {
			((IProjectWizard) getWizard()).removeProject();
		}
		super.setVisible(visible);
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	@Deprecated
	protected final BuildpathDetector createBuildpathDetector(
			IProgressMonitor monitor, IDLTKLanguageToolkit toolkit)
			throws CoreException {
		return null;
	}

	@Override
	protected final String getScriptNature() {
		return fCreator.getScriptNature();
	}

	@Deprecated
	protected final IPreferenceStore getPreferenceStore() {
		return null;
	}

	/**
	 * @since 2.0
	 */
	public void initProjectWizardPage() {
		final IProjectWizard wizard = (IProjectWizard) getWizard();
		final ProjectCreator creator = wizard.getProjectCreator();
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
		final IProjectWizard wizard = (IProjectWizard) getWizard();
		init(DLTKCore.create(wizard.getProject()), null, false);
	}

	private final IProjectCreateStep initStep = new IProjectCreateStep() {

		public void execute(IProject project, IProgressMonitor monitor)
				throws CoreException {
			final IBuildpathEntry[] entries = fCreator.initBuildpath(monitor);
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			init(DLTKCore.create(project), entries, false);
		}

		public boolean isRecurrent() {
			return false;
		}

	};

	private final IProjectCreateStep configureStep = new IProjectCreateStep() {

		public void execute(IProject project, IProgressMonitor monitor)
				throws CoreException, InterruptedException {
			configureScriptProject(monitor);
		}

		public boolean isRecurrent() {
			return true;
		}

	};

}
