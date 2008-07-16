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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.ast.declarations.FakeModuleDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.eclipse.dltk.validators.core.IBuildParticipant;
import org.eclipse.dltk.validators.core.IBuildParticipantExtension;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.NullValidatorOutput;
import org.eclipse.dltk.validators.core.ValidatorRuntime;

public class ValidatorBuilder implements IScriptBuilder {

	private static final boolean DEBUG = false;

	public IStatus buildModelElements(IScriptProject project, List elements,
			IProgressMonitor monitor, int buildType) {
		if (DEBUG) {
			System.out.println("Build " + project.getElementName()); //$NON-NLS-1$
			for (Iterator i = elements.iterator(); i.hasNext();) {
				IModelElement element = (IModelElement) i.next();
				if (element.getElementType() == IModelElement.SOURCE_MODULE) {
					final IProjectFragment fragment = (IProjectFragment) element
							.getAncestor(IModelElement.PROJECT_FRAGMENT);
					if (!fragment.isExternal()) {
						System.out.println("- " + element.getElementName()); //$NON-NLS-1$
					}
				}
			}
		}
		buildModules(project, elements, buildType);
		return ValidatorRuntime.executeAutomaticSourceModuleValidators(project,
				elements, new NullValidatorOutput(), monitor);
	}

	private void buildModules(IScriptProject project, List elements,
			int buildType) {
		final Map modulesByNature = splitByNature(elements);
		for (Iterator i = modulesByNature.entrySet().iterator(); i.hasNext();) {
			final Map.Entry entry = (Map.Entry) i.next();
			buildNatureModules(project, buildType, (String) entry.getKey(),
					(List) entry.getValue());
		}
	}

	private void buildNatureModules(IScriptProject project, int buildType,
			final String nature, final List modules) {
		final IBuildParticipant[] validators = ValidatorRuntime
				.getBuildParticipants(project, nature, ValidatorRuntime.ALL);
		boolean secondPass = false;
		for (int j = 0; j < validators.length; ++j) {
			final IBuildParticipant participant = validators[j];
			if (participant instanceof IBuildParticipantExtension) {
				((IBuildParticipantExtension) participant)
						.beginBuild(buildType);
				secondPass = true;
			}
		}
		final List reporters = secondPass ? new ArrayList() : null;
		for (Iterator j = modules.iterator(); j.hasNext();) {
			final ISourceModule module = (ISourceModule) j.next();
			final IResource resource = module.getResource();
			if (resource != null) {
				final BuildProblemReporter reporter = new BuildProblemReporter(
						resource);
				buildModule(module, validators, reporter);
				reporter.flush();
				if (reporters != null) {
					reporters.add(reporter);
				}
			}
		}
		for (int j = 0; j < validators.length; ++j) {
			final IBuildParticipant participant = validators[j];
			if (participant instanceof IBuildParticipantExtension) {
				((IBuildParticipantExtension) participant).endBuild();
			}
		}
		if (reporters != null) {
			for (Iterator j = reporters.iterator(); j.hasNext();) {
				final BuildProblemReporter reporter = (BuildProblemReporter) j
						.next();
				reporter.flush();
			}
		}
	}

	private void buildModule(final ISourceModule module,
			final IBuildParticipant[] validators, BuildProblemReporter reporter) {
		final ModuleDeclaration moduleDeclaration = SourceParserUtil
				.getModuleDeclaration(module, reporter);
		final boolean isError = moduleDeclaration == null
				|| moduleDeclaration instanceof FakeModuleDeclaration
				|| reporter.hasErrors();
		if (isError) {
			if (reporter.isEmpty()) {
				reporter.reportProblem(new DefaultProblem(module
						.getElementName(),
						Messages.ValidatorBuilder_unknownError, 0, null,
						ProblemSeverities.Error, 0, 0, 0));
			}
		} else {
			for (int k = 0; k < validators.length; ++k) {
				final IBuildParticipant participant = validators[k];
				try {
					participant.build(module, moduleDeclaration, reporter);
				} catch (CoreException e) {
					ValidatorsCore.log(e.getStatus());
				}
			}
		}
	}

	/**
	 * @param elements
	 * @return
	 */
	private Map splitByNature(List elements) {
		final Map result = new HashMap();
		for (Iterator i = elements.iterator(); i.hasNext();) {
			final IModelElement element = (IModelElement) i.next();
			if (element.getElementType() == IModelElement.SOURCE_MODULE) {
				final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
						.getLanguageToolkit(element);
				if (toolkit != null) {
					List natureModules = (List) result.get(toolkit
							.getNatureId());
					if (natureModules == null) {
						natureModules = new ArrayList();
						result.put(toolkit.getNatureId(), natureModules);
					}
					natureModules.add(element);
				}
			}
		}
		return result;
	}

	public IStatus buildResources(IScriptProject project, List resources,
			IProgressMonitor monitor, int buildType) {
		return ValidatorRuntime.executeAutomaticResourceValidators(project,
				resources, new NullValidatorOutput(), monitor);
	}

	public int estimateElementsToBuild(List elements) {
		IValidator[] validators = ValidatorRuntime.getAllValidators();
		int count = 0;
		for (int i = 0; i < validators.length; i++) {
			if (validators[i].isAutomatic()) {
				count++;
			}
		}
		if (count == 0) {
			return 0;
		}
		int estimation = 0;
		for (int i = 0; i < elements.size(); i++) {
			IModelElement element = (IModelElement) elements.get(i);
			if (element.getElementType() == IModelElement.SOURCE_MODULE) {
				IProjectFragment projectFragment = (IProjectFragment) element
						.getAncestor(IModelElement.PROJECT_FRAGMENT);
				if (!projectFragment.isExternal())
					estimation++;
			}
		}
		return estimation;
	}

	public Set getDependencies(IScriptProject project, Set resources,
			Set allResources, Set oldExternalFolders, Set externalFolders) {
		// TODO Auto-generated method stub
		return null;
	}
}
