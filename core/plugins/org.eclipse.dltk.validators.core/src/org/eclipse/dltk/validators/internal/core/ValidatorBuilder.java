/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.internal.core;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.eclipse.dltk.validators.core.ISourceModuleValidator;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.NullValidatorOutput;
import org.eclipse.dltk.validators.core.ValidatorRuntime;
import org.eclipse.dltk.validators.core.ValidatorRuntime.AutomaticValidatorPredicate;

public class ValidatorBuilder implements IScriptBuilder {
	private static final boolean DEBUG = false;

	private static final int WORK_EXTERNAL = 200;

	public IStatus buildModelElements(IScriptProject project, List elements,
			IProgressMonitor monitor, int buildType) {
		final IValidator[] validators = ValidatorRuntime.getProjectValidators(
				project, ISourceModuleValidator.class,
				new AutomaticValidatorPredicate(project));
		final int validatorWork = validators.length * WORK_EXTERNAL;
		monitor.beginTask("", validatorWork); //$NON-NLS-1$
		try {
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

	public IStatus buildResources(IScriptProject project, List resources,
			IProgressMonitor monitor, int buildType) {
		final IProgressMonitor sub = new SubProgressMonitor(monitor, resources
				.size());
		try {
			return ValidatorRuntime.executeAutomaticResourceValidators(project,
					resources, new NullValidatorOutput(), sub);
		} finally {
			sub.done();
		}
	}

	public void clean(IScriptProject project, IProgressMonitor monitor) {
		ValidatorRuntime.cleanAll(project, new ISourceModule[0],
				new IResource[] { project.getProject() }, monitor);
	}

	public void initialize(IScriptProject project) {
		// NOP
	}

	public void reset(IScriptProject project) {
		// NOP
	}

	public DependencyResponse getDependencies(IScriptProject project,
			int buildType, Set localElements, Set externalElements,
			Set oldExternalFolders, Set externalFolders) {
		return null;
	}

}
