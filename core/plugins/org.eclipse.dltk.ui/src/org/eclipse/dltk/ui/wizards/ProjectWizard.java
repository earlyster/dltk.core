/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * @since 2.0
 */
public abstract class ProjectWizard extends NewElementWizard implements
		INewWizard, IExecutableExtension, IProjectWizard {

	private IConfigurationElement fConfigElement;

	@Override
	public void createPageControls(Composite pageContainer) {
		final ProjectCreator creator = getProjectCreator();
		if (creator != null) {
			for (IWizardPage page : getPages()) {
				if (page instanceof IProjectWizardPage) {
					((IProjectWizardPage) page).configureSteps(creator);
				}
			}
		}
		super.createPageControls(pageContainer);
	}

	@Override
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		getProjectCreator().performFinish(monitor);
	}

	@Override
	public boolean performFinish() {
		updateSteps(null);
		boolean res = super.performFinish();
		if (res) {
			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			selectAndReveal(getCreatedElement().getProject());
		}
		return res;
	}

	protected ILocationGroup getFirstPage() {
		final IWizardPage page = getPage(ProjectWizardFirstPage.PAGE_NAME);
		Assert.isNotNull(page);
		return (ILocationGroup) page;
	}

	/*
	 * Stores the configuration element for the wizard. The config element will
	 * be used in <code>performFinish</code> to set the result perspective.
	 */
	public void setInitializationData(IConfigurationElement cfig,
			String propertyName, Object data) {
		fConfigElement = cfig;
	}

	@Override
	public boolean performCancel() {
		getProjectCreator().removeProject();
		return super.performCancel();
	}

	@Override
	public IScriptProject getCreatedElement() {
		final IWizardPage page = getPage(ProjectWizardSecondPage.PAGE_NAME);
		Assert.isNotNull(page);
		return ((ProjectWizardSecondPage) page).getScriptProject();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);
		while (nextPage != null && !isEnabledPage(nextPage)) {
			nextPage = super.getNextPage(nextPage);
		}
		return nextPage;
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		IWizardPage prevPage = super.getPreviousPage(page);
		while (prevPage != null && !isEnabledPage(prevPage)) {
			prevPage = super.getPreviousPage(prevPage);
		}
		return prevPage;
	}

	@Override
	public boolean canFinish() {
		final IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; ++i) {
			final IWizardPage page = pages[i];
			if (isEnabledPage(page)) {
				if (!page.isPageComplete()) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isEnabledPage(IWizardPage page) {
		return true;
	}

	/**
	 * @since 2.0
	 */
	public IEnvironment getEnvironment() {
		return getFirstPage().getEnvironment();
	}

	/**
	 * @since 2.0
	 */
	public IProject getProject() {
		return getFirstPage().getProjectHandle();
	}

	public void createProject() {
		updateSteps(getContainer().getCurrentPage());
		getProjectCreator().changeToNewProject();
	}

	public void removeProject() {
		getProjectCreator().removeProject();
	}

	public ProjectCreator getProjectCreator() {
		final IWizardPage page = getPage(ProjectWizardSecondPage.PAGE_NAME);
		if (page != null) {
			return ((ProjectWizardSecondPage) page).getCreator();
		} else {
			return null;
		}
	}

	protected void updateSteps(final IWizardPage currentPage) {
		for (IWizardPage page : getPages()) {
			if (page == currentPage) {
				break;
			}
			if (page instanceof IProjectWizardPage) {
				((IProjectWizardPage) page).updateSteps();
			}
		}
	}

}
