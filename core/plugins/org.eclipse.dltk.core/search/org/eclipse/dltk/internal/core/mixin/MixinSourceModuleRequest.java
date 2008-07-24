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
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.ReadWriteMonitor;

public class MixinSourceModuleRequest extends MixinIndexRequest {

	private final ISourceModule module;
	private final IDLTKLanguageToolkit toolkit;

	public MixinSourceModuleRequest(ISourceModule module,
			IDLTKLanguageToolkit toolkit) {
		this.module = module;
		this.toolkit = toolkit;
	}

	protected String getName() {
		return module.getElementName();
	}

	protected void run() throws CoreException, IOException {
		final IScriptProject project = module.getScriptProject();
		final Index index = getProjectMixinIndex(project);
		final ReadWriteMonitor imon = index.monitor;
		imon.enterWrite();
		try {
			indexSourceModule(index, toolkit, module, project.getPath());
		} finally {
			imon.exitWrite();
		}
	}

}
