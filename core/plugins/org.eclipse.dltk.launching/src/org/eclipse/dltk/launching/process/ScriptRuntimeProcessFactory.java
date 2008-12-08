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
package org.eclipse.dltk.launching.process;

import java.util.Map;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IProcessFactory;
import org.eclipse.debug.core.model.IProcess;

public class ScriptRuntimeProcessFactory implements IProcessFactory {

	public static final String PROCESS_FACTORY_ID = "org.eclipse.dltk.launching.scriptProcessFactory"; //$NON-NLS-1$

	public IProcess newProcess(ILaunch launch, Process process, String label,
			Map attributes) {
		return new ScriptRuntimeProcess(launch, process, label, attributes);
	}

}
