/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.  
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.IEnvironment;

/**
 * @since 2.0
 */
public interface ISourceModuleWizard {

	/**
	 * Constant for the mode if source module should be created in workspace
	 */
	String MODE_WORKSPACE = "org.eclipse.dltk.ui.sourceModuleWizard.workspace"; //$NON-NLS-1$

	/**
	 * Returns the environment of the project selected in the wizard
	 */
	IEnvironment getEnvironment();

	/**
	 * Returns the project selected in the wizard
	 */
	IProject getProject();

	/**
	 * Returns the folder selected in the wizard
	 */
	IScriptFolder getFolder();

	/**
	 * Returns the file name specified in the wizard
	 */
	String getFileName();

	/**
	 * Executes validation in the wizard
	 */
	void validate();

	/**
	 * Returns the currently selected mode
	 */
	String getMode();

	/**
	 * Sets the mode
	 */
	void setMode(String mode);

	void enableMode(String mode, boolean enable);

	public interface ICreateContext {

		IEnvironment getEnvironment();

		IScriptProject getScriptProject();

		IScriptFolder getScriptFolder();

		ISourceModule getSourceModule();

		String getContent();

		void setContent(String content);

		void addStep(String kind, int priority, ICreateStep step);

		ICreateStep[] getSteps(String kind);
	}

	public interface ICreateStep {
		String KIND_PREPARE = "PREPARE"; //$NON-NLS-1$
		String KIND_EXECUTE = "EXECUTE"; //$NON-NLS-1$
		String KIND_FINALIZE = "FINALIZE"; //$NON-NLS-1$

		void execute(ICreateContext context, IProgressMonitor monitor)
				throws CoreException;
	}

}
