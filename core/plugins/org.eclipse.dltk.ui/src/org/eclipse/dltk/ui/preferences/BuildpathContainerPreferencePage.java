/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.ui.preferences;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.actions.ActionMessages;
import org.eclipse.dltk.internal.ui.scriptview.BuildPathContainer;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BuildpathContainerWizard;
import org.eclipse.dltk.ui.util.ExceptionHandler;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.WizardPropertyPage;

/**
 * Wraps a PropertyPage around a ClasspathContainerWizard. It is required, that
 * the wizard consists of exactly one page.
 * 
 * @since 2.0
 */
public class BuildpathContainerPreferencePage extends WizardPropertyPage {

	private IScriptProject fJavaProject;
	private IBuildpathEntry fEntry;

	public BuildpathContainerPreferencePage() {
		noDefaultAndApplyButton();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);

		BuildPathContainer container;
		if (element instanceof BuildPathContainer) {
			container = (BuildPathContainer) element;
		} else {
			container = (BuildPathContainer) element
					.getAdapter(BuildPathContainer.class);
		}
		fJavaProject = container.getScriptProject();
		fEntry = container.getBuildpathEntry();
	}

	@Override
	protected IWizard createWizard() {
		try {
			IScriptProject project = fJavaProject;
			IBuildpathEntry[] entries = project.getRawBuildpath();
			return new BuildpathContainerWizard(fEntry, project, entries);
		} catch (ModelException e) {
			String title = ActionMessages.ConfigureContainerAction_error_title;
			String message = ActionMessages.ConfigureContainerAction_error_creationfailed_message;
			ExceptionHandler.handle(e, getShell(), title, message);
		}

		return null;
	}

	/**
	 * Apply the changes to the classpath
	 */
	@Override
	protected void applyChanges() {
		IBuildpathEntry[] created = ((BuildpathContainerWizard) getWizard())
				.getNewEntries();
		if (created == null || created.length != 1)
			return;

		final IBuildpathEntry result = created[0];
		if (result == null || result.equals(fEntry))
			return;

		try {
			IBuildpathEntry[] entries = fJavaProject.getRawBuildpath();

			int idx = indexInClasspath(entries, fEntry);
			if (idx == -1)
				return;

			final IBuildpathEntry[] newEntries = new IBuildpathEntry[entries.length];
			System.arraycopy(entries, 0, newEntries, 0, entries.length);
			newEntries[idx] = result;

			IRunnableContext context = new ProgressMonitorDialog(getShell());
			context = PlatformUI.getWorkbench().getProgressService();
			context.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						// if (result.getEntryKind() ==
						// IBuildpathEntry.BPE_CONTAINER) {
						// IPath path = result.getPath();
						// String eeID = JavaRuntime
						// .getExecutionEnvironmentId(path);
						// if (eeID != null) {
						// BuildPathSupport.setEEComplianceOptions(
						// fJavaProject, eeID, null);
						// }
						// }
						fJavaProject.setRawBuildpath(newEntries, monitor);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			});

			fEntry = result;

		} catch (ModelException e) {
			String title = ActionMessages.ConfigureContainerAction_error_title;
			String message = ActionMessages.ConfigureContainerAction_error_creationfailed_message;
			ExceptionHandler.handle(e, getShell(), title, message);
		} catch (InvocationTargetException e) {
			String title = ActionMessages.ConfigureContainerAction_error_title;
			String message = ActionMessages.ConfigureContainerAction_error_applyingfailed_message;
			ExceptionHandler.handle(e, getShell(), title, message);
		} catch (InterruptedException e) {
			// user cancelled
		}
	}

	private static int indexInClasspath(IBuildpathEntry[] entries,
			IBuildpathEntry entry) {
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].equals(entry)) {
				return i;
			}
		}
		return -1;
	}

}
