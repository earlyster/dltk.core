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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.ReadWriteMonitor;

class MixinSourceModulesRequest extends MixinIndexRequest {

	private final IScriptProject project;
	private final IDLTKLanguageToolkit toolkit;
	private final Set modules;

	/**
	 * @param project
	 * @param modules
	 */
	public MixinSourceModulesRequest(IScriptProject project,
			IDLTKLanguageToolkit toolkit, Set modules) {
		this.project = project;
		this.toolkit = toolkit;
		this.modules = modules;
	}

	protected String getName() {
		return project.getElementName();
	}

	protected void run() throws CoreException, IOException {
		final Index index = getProjectMixinIndex(project);
		final IPath containerPath = project.getPath();
		final List changes = checkChanges(index, modules, containerPath,
				EnvironmentManager.getEnvironment(project));
		if (DEBUG) {
			log("changes.size=" + changes.size()); //$NON-NLS-1$
		}
		if (changes.isEmpty()) {
			return;
		}
		final ReadWriteMonitor imon = index.monitor;
		imon.enterWrite();
		try {
			for (Iterator i = changes.iterator(); !isCancelled && i.hasNext();) {
				final Object change = i.next();
				if (change instanceof String) {
					index.remove((String) change);
				} else {
					indexSourceModule(index, toolkit, (ISourceModule) change,
							containerPath);
				}
			}
		} finally {
			try {
				index.save();
			} catch (IOException e) {
				DLTKCore.error("error saving index", e); //$NON-NLS-1$
			} finally {
				imon.exitWrite();
			}
		}
	}

	public boolean belongsTo(String jobFamily) {
		return jobFamily.equals(project.getProject().getName());
	}

}
