/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.internal.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.ast.declarations.FakeModuleDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserExtension2;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.eclipse.dltk.core.builder.IScriptBuilderExtension;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.validators.core.BuildParticipantManager;
import org.eclipse.dltk.validators.core.IBuildParticipant;
import org.eclipse.dltk.validators.core.IBuildParticipantExtension;
import org.eclipse.dltk.validators.core.IBuildParticipantExtension2;
import org.eclipse.dltk.validators.core.ISourceModuleValidator;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.NullValidatorOutput;
import org.eclipse.dltk.validators.core.ValidatorRuntime;
import org.eclipse.osgi.util.NLS;

public class ValidatorBuilder implements IScriptBuilder,
		IScriptBuilderExtension {
	private static final boolean DEBUG = false;

	private static final int WORK_BUILD = 100;
	private static final int WORK_EXTERNAL = 200;

	public IStatus buildModelElements(IScriptProject project, List elements,
			IProgressMonitor monitor, int buildType) {
		final IValidator[] validators = ValidatorRuntime.getProjectValidators(
				project, ISourceModuleValidator.class,
				ValidatorRuntime.AUTOMATIC);
		final int validatorWork = validators.length * WORK_EXTERNAL;
		monitor.beginTask("", validatorWork + WORK_BUILD); //$NON-NLS-1$
		try {
			buildModules(project, elements, buildType, ValidatorUtils
					.subMonitorFor(monitor, WORK_BUILD));
			if (validators.length != 0) {
				final long startTime = DEBUG ? System.currentTimeMillis() : 0;
				final IStatus status = ValidatorRuntime
						.executeSourceModuleValidators(project, elements,
								new NullValidatorOutput(), validators,
								ValidatorUtils.subMonitorFor(monitor,
										validatorWork));
				if (DEBUG) {
					System.out
							.println("Validate " + project.getElementName() + "(" //$NON-NLS-1$ //$NON-NLS-2$
									+ elements.size()
									+ ") in " //$NON-NLS-1$
									+ (System.currentTimeMillis() - startTime)
									+ "ms"); //$NON-NLS-1$
				}
				return status;
			}
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
										ValidatorMessages.ValidatorBuilder_buildExternalModuleSubTask,
										String.valueOf(remainingWork), module
												.getElementName()));
				final ModuleDeclaration moduleDeclaration;
				if (useSourceParser) {
					moduleDeclaration = SourceParserUtil.getModuleDeclaration(
							module, null);
				} else {
					moduleDeclaration = null;
				}
				for (int i = 0; i < extensions.length; ++i) {
					if (monitor.isCanceled()) {
						return;
					}
					try {
						extensions[i].buildExternalModule(module,
								moduleDeclaration);
					} catch (CoreException e) {
						ValidatorsCore.log(e.getStatus());
					}
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
		monitor.beginTask(ValidatorMessages.ValidatorBuilder_buildingModules,
				elements.size());
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
		final List reporters = secondPass ? new ArrayList() : null;
		int counter = 0;
		for (Iterator j = modules.iterator(); j.hasNext();) {
			if (monitor.isCanceled())
				return;
			final ISourceModule module = (ISourceModule) j.next();
			monitor.subTask(NLS.bind(
					ValidatorMessages.ValidatorBuilder_buildModuleSubTask,
					String.valueOf(modules.size() - counter), module
							.getElementName()));
			final IResource resource = module.getResource();
			if (resource != null) {
				final BuildProblemReporter reporter = new BuildProblemReporter(
						resource);
				buildModule(module, reporter);
				if (reporters != null) {
					reporters.add(reporter);
				} else {
					reporter.flush();
				}
			}
			monitor.worked(1);
			++counter;
		}
		if (reporters != null) {
			monitor.subTask(ValidatorMessages.ValidatorBuilder_finalizeBuild);
			final IProgressMonitor finalizeMonitor = new SubTaskProgressMonitor(
					monitor, ValidatorMessages.ValidatorBuilder_finalizeBuild);
			if (participants != null) {
				for (int j = 0; j < participants.length; ++j) {
					final IBuildParticipant participant = participants[j];
					if (participant instanceof IBuildParticipantExtension) {
						((IBuildParticipantExtension) participant)
								.endBuild(finalizeMonitor);
					}
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
			monitor
					.subTask(ValidatorMessages.ValidatorBuilder_InitializeBuilders);
			endBuildNeeded = false;
			if (participants != null) {
				for (int j = 0; j < participants.length; ++j) {
					final IBuildParticipant participant = participants[j];
					if (participant instanceof IBuildParticipantExtension) {
						((IBuildParticipantExtension) participant)
								.beginBuild(buildType);
						endBuildNeeded = true;
					}
				}
			}
			beginBuildDone = true;
		}
		return endBuildNeeded;
	}

	private void buildModule(final ISourceModule module,
			BuildProblemReporter reporter) {
		final ModuleDeclaration moduleDeclaration;
		if (useSourceParser) {
			moduleDeclaration = SourceParserUtil.getModuleDeclaration(module,
					reporter);
			final boolean isError = moduleDeclaration == null
					|| moduleDeclaration instanceof FakeModuleDeclaration
					|| reporter.hasErrors();
			if (isError && reporter.isEmpty()) {
				reporter.reportProblem(new DefaultProblem(
						ValidatorMessages.ValidatorBuilder_unknownError, 0,
						null, ProblemSeverities.Error, 0, 0, 0));
			}
		} else {
			moduleDeclaration = null;
		}
		if (participants != null) {
			for (int k = 0; k < participants.length; ++k) {
				final IBuildParticipant participant = participants[k];
				try {
					participant.build(module, moduleDeclaration, reporter);
				} catch (CoreException e) {
					ValidatorsCore.log(e.getStatus());
				}
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
					final String template = ValidatorMessages.ValidatorBuilder_clearingResourceMarkers;
					sub.subTask(NLS.bind(template, resource.getName()));
					resource.deleteMarkers(DefaultProblem.MARKER_TYPE_PROBLEM,
							true, IResource.DEPTH_INFINITE);
					resource.deleteMarkers(DefaultProblem.MARKER_TYPE_TASK,
							true, IResource.DEPTH_INFINITE);
					sub.worked(1);
				}
			} catch (CoreException e) {
				final String msg = ValidatorMessages.ValidatorBuilder_errorDeleteResourceMarkers;
				ValidatorsCore.error(msg, e);
			}
			return ValidatorRuntime.executeAutomaticResourceValidators(project,
					resources, new NullValidatorOutput(),
					new SubProgressMonitor(sub, resources.size()));
		} finally {
			sub.done();
		}
	}

	public void clean(IScriptProject project, IProgressMonitor monitor) {
		ValidatorRuntime.cleanAll(project, new ISourceModule[0],
				new IResource[] { project.getProject() }, monitor);
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

	private boolean useSourceParser = false;

	public void initialize(IScriptProject project) {
		toolkit = project.getLanguageToolkit();
		if (toolkit != null) {
			participants = BuildParticipantManager.getBuildParticipants(
					project, toolkit.getNatureId());
			final ISourceParser sourceParser = DLTKLanguageManager
					.getSourceParser(toolkit.getNatureId());
			useSourceParser = sourceParser != null
					&& (!(sourceParser instanceof ISourceParserExtension2) || ((ISourceParserExtension2) sourceParser)
							.useInBuilder());
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
