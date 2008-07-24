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
package org.eclipse.dltk.internal.core.mixin;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.ReadWriteMonitor;

public class MixinSourceModuleRemoveRequest extends MixinIndexRequest {

	private final IScriptProject project;
	private final String path;

	/**
	 * @param project
	 * @param path
	 * @param name
	 */
	public MixinSourceModuleRemoveRequest(IScriptProject project, String path) {
		this.project = project;
		this.path = path;
	}

	protected String getName() {
		return path;
	}

	protected void run() throws CoreException, IOException {
		final Index index = getProjectMixinIndex(project);
		final ReadWriteMonitor imon = index.monitor;
		imon.enterWrite();
		try {
			index.remove(path);
		} finally {
			imon.exitWrite();
		}
	}

}
