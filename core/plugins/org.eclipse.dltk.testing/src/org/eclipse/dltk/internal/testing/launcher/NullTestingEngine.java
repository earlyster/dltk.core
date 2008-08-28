/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.testing.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.testing.ITestingEngine;

public class NullTestingEngine implements ITestingEngine {

	private static ITestingEngine instance = null;

	public static ITestingEngine getInstance() {
		if (instance == null) {
			instance = new NullTestingEngine();
		}
		return instance;
	}

	public void configureLaunch(InterpreterConfig config,
			ILaunchConfiguration configuration, ILaunch launch)
			throws CoreException {
		// empty
	}

	public String getContainerLauncher(ILaunchConfiguration configuration,
			IEnvironment scriptEnvironment) throws CoreException {
		return null;
	}

	/*
	 * @see org.eclipse.dltk.testing.ITestingEngine#getId()
	 */
	public String getId() {
		return null;
	}

	/*
	 * @see org.eclipse.dltk.testing.ITestingEngine#getName()
	 */
	public String getName() {
		return "NULL";
	}

	public IStatus validateContainer(IModelElement element) {
		return Status.CANCEL_STATUS;
	}

	public IStatus validateSourceModule(ISourceModule module) {
		return Status.CANCEL_STATUS;
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

}
