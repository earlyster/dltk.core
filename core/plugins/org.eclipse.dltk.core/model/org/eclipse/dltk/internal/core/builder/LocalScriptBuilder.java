/*******************************************************************************
 * Copyright (c) 2011 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.core.builder;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.builder.IBuildChange;
import org.eclipse.dltk.core.builder.IBuildState;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.osgi.util.NLS;

class LocalScriptBuilder extends ScriptBuilder {

	public void build(IProject project, List<IFile> files,
			IProgressMonitor monitor) {
		this.currentProject = project;
		this.scriptProject = (ScriptProject) DLTKCore.create(project);
		final IBuildState buildState = new BuildStateStub();
		IScriptBuilder[] builders = null;
		try {
			monitor.setTaskName(NLS.bind(
					Messages.ScriptBuilder_buildingScriptsIn,
					currentProject.getName()));
			monitor.beginTask(NONAME, 100);
			builders = getScriptBuilders();
			if (builders == null || builders.length == 0) {
				return;
			}
			IBuildChange buildChange = new BuildChange(project, null, files,
					monitor);
			for (IScriptBuilder builder : builders) {
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				builder.prepare(buildChange, buildState, monitor);
				if (buildChange.getBuildType() == IScriptBuilder.FULL_BUILD
						&& buildChange instanceof IncrementalBuildChange) {
					buildChange = new FullBuildChange(currentProject, monitor);
				}
			}
			for (IScriptBuilder builder : builders) {
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				builder.build(buildChange, buildState, monitor);
			}
		} catch (CoreException e) {
			DLTKCore.error(e);
		} finally {
			resetBuilders(builders, buildState, monitor);
			monitor.done();
		}
	}

}
