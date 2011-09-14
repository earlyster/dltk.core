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
package org.eclipse.dltk.core.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.internal.core.builder.BuildChange;
import org.eclipse.dltk.internal.core.builder.BuildStateStub;
import org.eclipse.dltk.internal.core.builder.FullBuildChange;
import org.eclipse.dltk.internal.core.builder.Messages;
import org.eclipse.dltk.internal.core.builder.ScriptBuilder;
import org.eclipse.osgi.util.NLS;

public class ScriptBuilderUtil {

	private static IProgressMonitor monitorFor(IProgressMonitor monitor) {
		return monitor == null ? new NullProgressMonitor() : monitor;
	}

	private static class LocalScriptBuilder extends ScriptBuilder {

		public LocalScriptBuilder() {
		}

		public void build(IProject project, List<IFile> files,
				IProgressMonitor monitor) throws CoreException {
			this.currentProject = project;
			this.scriptProject = (ScriptProject) DLTKCore.create(project);
			final IBuildState buildState = new BuildStateStub(project.getName());
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
				IBuildChange buildChange = new BuildChange(project, null,
						files, monitor);
				for (IScriptBuilder builder : builders) {
					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}
					builder.prepare(buildChange, buildState, monitor);
					if (buildChange.getBuildType() == IScriptBuilder.FULL_BUILD
							&& buildChange instanceof BuildChange) {
						buildChange = new FullBuildChange(currentProject,
								monitor);
					}
				}
				for (IScriptBuilder builder : builders) {
					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}
					builder.build(buildChange, buildState, monitor);
				}
			} finally {
				resetBuilders(builders, buildState, monitor);
				monitor.done();
			}
		}

	}

	/**
	 * Executes all the {@link IScriptBuilder}s for the specified files.
	 * 
	 * @param files
	 * @param monitor
	 * @throws CoreException
	 */
	public static void build(List<IFile> files, IProgressMonitor monitor)
			throws CoreException {
		final Map<IProject, Set<IFile>> byProject = new HashMap<IProject, Set<IFile>>();
		for (IFile file : files) {
			Set<IFile> projectFiles = byProject.get(file.getProject());
			if (projectFiles == null) {
				projectFiles = new LinkedHashSet<IFile>();
				byProject.put(file.getProject(), projectFiles);
			}
			projectFiles.add(file);
		}
		final LocalScriptBuilder builder = new LocalScriptBuilder();
		for (Map.Entry<IProject, Set<IFile>> entry : byProject.entrySet()) {
			builder.build(entry.getKey(),
					new ArrayList<IFile>(entry.getValue()), monitorFor(monitor));
		}
	}

	private static class SingleBuilderRunner extends ScriptBuilder {

		private final Class<? extends IScriptBuilder> builderClass;

		public SingleBuilderRunner(Class<? extends IScriptBuilder> builderClass) {
			this.builderClass = builderClass;
		}

		@Override
		protected IScriptBuilder[] getScriptBuilders(
				IDLTKLanguageToolkit toolkit) {
			final IScriptBuilder[] builders = super.getScriptBuilders(toolkit);
			if (builders != null) {
				for (IScriptBuilder builder : builders) {
					if (builder.getClass() == builderClass) {
						return new IScriptBuilder[] { builder };
					}
				}
			}
			throw new IllegalArgumentException("IScriptBuilder of class "
					+ builderClass + " not found");
		}

		public void build(IProject project, IProgressMonitor monitor)
				throws CoreException {
			this.currentProject = project;
			this.scriptProject = (ScriptProject) DLTKCore.create(project);
			final IBuildState buildState = new BuildStateStub(project.getName());
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
				final IBuildChange buildChange = new FullBuildChange(project,
						monitor);
				for (IScriptBuilder builder : builders) {
					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}
					builder.prepare(buildChange, buildState, monitor);
				}
				for (IScriptBuilder builder : builders) {
					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}
					builder.build(buildChange, buildState, monitor);
				}
			} finally {
				resetBuilders(builders, buildState, monitor);
				monitor.done();
			}
		}

	}

	/**
	 * Executes the specified {@link IScriptBuilder} for the specified project.
	 * 
	 * @param project
	 * @param builderClass
	 * @throws CoreException
	 */
	public static void build(IProject project,
			Class<? extends IScriptBuilder> builderClass,
			IProgressMonitor monitor) throws CoreException {
		final SingleBuilderRunner builder = new SingleBuilderRunner(
				builderClass);
		builder.build(project, monitorFor(monitor));
	}

}
