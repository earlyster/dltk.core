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
package org.eclipse.dltk.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationMigrationDelegate;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.dltk.launching.process.ScriptRuntimeProcessFactory;

public class ScriptLaunchConfigurationMigrationDelegate implements
		ILaunchConfigurationMigrationDelegate {

	public boolean isCandidate(ILaunchConfiguration candidate)
			throws CoreException {
		return candidate.getAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID,
				(String) null) == null;
	}

	public void migrate(ILaunchConfiguration candidate) throws CoreException {
		ILaunchConfigurationWorkingCopy wc = candidate.getWorkingCopy();
		wc.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID,
				ScriptRuntimeProcessFactory.PROCESS_FACTORY_ID);
		wc.doSave();
	}
}
