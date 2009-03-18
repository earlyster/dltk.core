/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension2;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.eclipse.dltk.core.builder.IScriptBuilderExtension;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.osgi.util.NLS;

public class StandardScriptBuilder implements IScriptBuilder,
		IScriptBuilderExtension {
	private static final boolean DEBUG = false;

	private static final int WORK_BUILD = 100;

	public IStatus buildModelElements(IScriptProject project, List elements,
			IProgressMonitor monitor, int buildType) {
		monitor.beginTask(Util.EMPTY_STRING, WORK_BUILD);
		try {
			buildModules(project, elements, buildType, BuildUtils
					.subMonitorFor(monitor, WORK_BUILD));
			return Status.OK_STATUS;
		} finally {
			monitor.done();
		}
	}

	public void buildExternalElements(ScriptProject project,
			List externalElements, IProgressMonitor monitor, int buildType) {
		beginBuild(buildType, monitor);
		final IBuildParticipantExtension2[] extensions = selectExtension2();
		if (extensions != null) {
			int remainingWork = externalElements.size();
			for (Iterator j = externalElements.iterator(); j.hasNext();) {
				if (monitor.isCanceled())
					return;
				final ISourceModule module = (ISourceModule) j.next();
				monitor
						.subTask(NLS
								.bind(
										Messages.ValidatorBuilder_buildExternalModuleSubTask,
										String.valueOf(remainingWork), module
												.getElementName()));
				final ExternalModuleBuildContext context = new ExternalModuleBuildContext(
						module, buildType);
				try {
					for (int i = 0; i < extensions.length; ++i) {
						if (monitor.isCanceled()) {
							return;
						}
						extensions[i].buildExternalModule(context);
					}
				} catch (CoreException e) {
					DLTKCore.error(NLS.bind(
							Messages.StandardScriptBuilder_errorBuildingExternalModule, module
									.getElementName()), e);
				}
				--remainingWork;
			}
		}
	}

	/**
	 * @return
	 */
	private IBuildParticipantExtension2[] selectExtension2() {
		if (participants != null) {
			int count = 0;
			for (int i = 0; i < participants.length; ++i) {
				final IBuildParticipant participant = participants[i];
				if (participant instanceof IBuildParticipantExtension2) {
					++count;
				}
			}
			if (count != 0) {
				final IBuildParticipantExtension2[] result = new IBuildParticipantExtension2[count];
				count = 0;
				for (int i = 0; i < participants.length; ++i) {
					final IBuildParticipant participant = participants[i];
					if (participant instanceof IBuildParticipantExtension2) {
						result[count++] = (IBuildParticipantExtension2) participant;
					}
				}
				return result;
			}
		}
		return null;
	}

	private void buildModules(IScriptProject project, List elements,
			int buildType, IProgressMonitor monitor) {
		final long startTime = DEBUG ? System.currentTimeMillis() : 0;
		monitor.beginTask(Messages.ValidatorBuilder_buildingModules, elements
				.size());
		if (toolkit != null) {
			buildNatureModules(project, buildType, elements, monitor);
		}
		monitor.done();
		if (DEBUG) {
			System.out.println("Build " + project.getElementName() + "(" //$NON-NLS-1$ //$NON-NLS-2$
					+ elements.size() + ") in " //$NON-NLS-1$
					+ (System.currentTimeMillis() - startTime) + "ms"); //$NON-NLS-1$
		}
	}

	private void buildNatureModules(IScriptProject project, int buildType,
			final List modules, IProgressMonitor monitor) {
		final boolean secondPass = beginBuild(buildType, monitor);
		if (participants.length == 0) {
			return;
		}
		final List reporters = secondPass ? new ArrayList() : null;
		int counter = 0;
		for (Iterator j = modules.iterator(); j.hasNext();) {
			if (monitor.isCanceled())
				return;
			final ISourceModule module = (ISourceModule) j.next();
			monitor.subTask(NLS.bind(
					Messages.ValidatorBuilder_buildModuleSubTask, String
							.valueOf(modules.size() - counter), module
							.getElementName()));
			final SourceModuleBuildContext context = new SourceModuleBuildContext(
					module, buildType);
			if (context.reporter != null) {
				buildModule(context);
				if (reporters != null) {
					reporters.add(context.reporter);
				} else {
					context.reporter.flush();
				}
			}
			monitor.worked(1);
			++counter;
		}
		if (reporters != null) {
			monitor.subTask(Messages.ValidatorBuilder_finalizeBuild);
			final IProgressMonitor finalizeMonitor = new SubTaskProgressMonitor(
					monitor, Messages.ValidatorBuilder_finalizeBuild);
			for (int j = 0; j < participants.length; ++j) {
				final IBuildParticipant participant = participants[j];
				if (participant instanceof IBuildParticipantExtension) {
					((IBuildParticipantExtension) participant)
							.endBuild(finalizeMonitor);
				}
			}
			for (Iterator j = reporters.iterator(); j.hasNext();) {
				final BuildProblemReporter reporter = (BuildProblemReporter) j
						.next();
				reporter.flush();
			}
		}
	}

	/**
	 * Calls {@link IBuildParticipantExtension#beginBuild(int)} for all
	 * {@link #participants}. Returns <code>true</code> if it was called for
	 * some of them so it is required to call
	 * {@link IBuildParticipantExtension#endBuild()}. If called multiple times
	 * only first time actual work are performed, so subsequent calls returns
	 * result of the first call.
	 * 
	 * @param buildType
	 * @param monitor
	 * @return
	 */
	private boolean beginBuild(int buildType, IProgressMonitor monitor) {
		if (!beginBuildDone) {
			monitor.subTask(Messages.ValidatorBuilder_InitializeBuilders);
			endBuildNeeded = false;
			int count = 0;
			for (int j = 0; j < participants.length; ++j) {
				final IBuildParticipant participant = participants[j];
				final boolean useParticipant;
				if (participant instanceof IBuildParticipantExtension) {
					useParticipant = ((IBuildParticipantExtension) participant)
							.beginBuild(buildType);
					endBuildNeeded = true;
				} else {
					useParticipant = true;
				}
				if (useParticipant) {
					if (count < j) {
						participants[count] = participants[j];
					}
					++count;
				}
			}
			participants = BuildParticipantManager.copyFirst(participants,
					count);
			beginBuildDone = true;
		}
		return endBuildNeeded;
	}

	private void buildModule(IBuildContext context) {
		for (int k = 0; k < participants.length; ++k) {
			final IBuildParticipant participant = participants[k];
			try {
				participant.build(context);
			} catch (CoreException e) {
				DLTKCore.error(Messages.StandardScriptBuilder_errorBuildingModule, e);
			}
		}
	}

	public IStatus buildResources(IScriptProject project, List resources,
			IProgressMonitor monitor, int buildType) {
		final IProgressMonitor sub = new SubProgressMonitor(monitor, resources
				.size() * 2);
		try {
			sub.beginTask(Util.EMPTY_STRING, resources.size());
			try {
				for (Iterator i = resources.iterator(); i.hasNext();) {
					final IResource resource = (IResource) i.next();
					final String template = Messages.ValidatorBuilder_clearingResourceMarkers;
					sub.subTask(NLS.bind(template, resource.getName()));
					resource.deleteMarkers(DefaultProblem.MARKER_TYPE_PROBLEM,
							true, IResource.DEPTH_INFINITE);
					resource.deleteMarkers(DefaultProblem.MARKER_TYPE_TASK,
							true, IResource.DEPTH_INFINITE);
					sub.worked(1);
				}
			} catch (CoreException e) {
				final String msg = Messages.ValidatorBuilder_errorDeleteResourceMarkers;
				DLTKCore.error(msg, e);
			}
		} finally {
			sub.done();
		}
		return Status.OK_STATUS;
	}

	public void clean(IScriptProject project, IProgressMonitor monitor) {
		final IProject p = project.getProject();
		try {
			p.deleteMarkers(DefaultProblem.MARKER_TYPE_PROBLEM, true,
					IResource.DEPTH_INFINITE);
			p.deleteMarkers(DefaultProblem.MARKER_TYPE_TASK, true,
					IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			DLTKCore.error(NLS.bind(
					Messages.StandardScriptBuilder_errorCleaning, p.getName()),
					e);
		}
	}

	public DependencyResponse getDependencies(IScriptProject project,
			int buildType, Set localElements, Set externalElements,
			Set oldExternalFolders, Set externalFolders) {
		if (participants == null) {
			return null;
		}
		Set localDependencies = null;
		Set externalDependencies = null;
		boolean fullLocal = false;
		for (int i = 0; i < participants.length; ++i) {
			final IBuildParticipant participant = participants[i];
			if (participant instanceof IBuildParticipantExtension2) {
				final DependencyResponse response = ((IBuildParticipantExtension2) participant)
						.getDependencies(buildType, localElements,
								externalElements, oldExternalFolders,
								externalFolders);
				if (response != null) {
					if (response.isFullExternalBuild()) {
						return response;
					} else {
						if (response.isFullLocalBuild()) {
							fullLocal = true;
						} else if (!response.getLocalDependencies().isEmpty()) {
							if (localDependencies == null) {
								localDependencies = new HashSet();
							}
							localDependencies.addAll(response
									.getLocalDependencies());
						}
						if (!response.getExternalDependencies().isEmpty()) {
							if (externalDependencies == null) {
								externalDependencies = new HashSet();
							}
							externalDependencies.addAll(response
									.getExternalDependencies());
						}
					}
				}
			}
		}
		if (externalDependencies == null) {
			if (fullLocal) {
				return DependencyResponse.FULL_LOCAL_BUILD;
			} else {
				return DependencyResponse.createLocal(localDependencies);
			}
		} else {
			return DependencyResponse.create(fullLocal, localDependencies,
					externalDependencies);
		}
	}

	private boolean beginBuildDone = false;
	private boolean endBuildNeeded = false;
	private IBuildParticipant[] participants = null;
	private IDLTKLanguageToolkit toolkit = null;

	public void initialize(IScriptProject project) {
		toolkit = project.getLanguageToolkit();
		if (toolkit != null) {
			participants = BuildParticipantManager.getBuildParticipants(
					project, toolkit.getNatureId());
		}
		beginBuildDone = false;
		endBuildNeeded = false;
	}

	public void reset(IScriptProject project) {
		participants = null;
		toolkit = null;
		beginBuildDone = false;
		endBuildNeeded = false;
	}

}
