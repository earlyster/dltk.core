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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IScriptProject;

public interface IProjectWizardLastPage {

	IScriptProject getScriptProject();

	/**
	 * @param monitor
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	void performFinish(IProgressMonitor monitor) throws CoreException,
			InterruptedException;

	/**
	 * 
	 */
	void performCancel();

}
