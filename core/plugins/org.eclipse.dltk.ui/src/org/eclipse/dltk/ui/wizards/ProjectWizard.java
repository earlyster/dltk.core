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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public abstract class ProjectWizard extends NewElementWizard implements
		INewWizard, IExecutableExtension {

	private IConfigurationElement fConfigElement;

	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		getLastPage().performFinish(monitor); // use the full progress monitor
	}

	public boolean performFinish() {
		boolean res = super.performFinish();
		if (res) {
			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			selectAndReveal(getLastPage().getScriptProject().getProject());
		}
		return res;
	}

	protected IProjectWizardLastPage getLastPage() {
		final IWizardPage[] pages = getPages();
		for (int i = pages.length; --i >= 0;) {
			final IWizardPage page = pages[i];
			if (page instanceof IProjectWizardLastPage) {
				return (IProjectWizardLastPage) page;
			}
		}
		throw new IllegalStateException(IProjectWizardLastPage.class
				.getSimpleName()
				+ " not found");
	}

	/*
	 * Stores the configuration element for the wizard. The config element will
	 * be used in <code>performFinish</code> to set the result perspective.
	 */
	public void setInitializationData(IConfigurationElement cfig,
			String propertyName, Object data) {
		fConfigElement = cfig;
	}

	public boolean performCancel() {
		getLastPage().performCancel();
		return super.performCancel();
	}

	public IModelElement getCreatedElement() {
		return getLastPage().getScriptProject();
	}

	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);
		while (nextPage != null && !isEnabledPage(nextPage)) {
			nextPage = super.getNextPage(nextPage);
		}
		return nextPage;
	}

	public IWizardPage getPreviousPage(IWizardPage page) {
		IWizardPage prevPage = super.getPreviousPage(page);
		while (prevPage != null && !isEnabledPage(prevPage)) {
			prevPage = super.getPreviousPage(prevPage);
		}
		return prevPage;
	}

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

	protected boolean isEnabledPage(IWizardPage page) {
		return true;
	}
}
