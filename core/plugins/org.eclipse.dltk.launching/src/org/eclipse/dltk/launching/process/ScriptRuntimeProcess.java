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
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.RuntimeProcess;

public class ScriptRuntimeProcess extends RuntimeProcess implements
		IScriptProcess {

	/**
	 * @param launch
	 * @param process
	 * @param name
	 * @param attributes
	 */
	public ScriptRuntimeProcess(ILaunch launch, Process process, String name,
			Map attributes) {
		super(new LaunchProxy(launch), process, name, attributes);
		setLaunch(launch);
	}

	public IStreamsProxy getStreamsProxy() {
		return null;
	}

	public IStreamsProxy getScriptStreamsProxy() {
		return super.getStreamsProxy();
	}

}
