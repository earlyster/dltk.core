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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.builder.IBuildChange;
import org.eclipse.dltk.core.builder.IBuildState;
import org.eclipse.dltk.core.builder.IProjectChange;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.eclipse.dltk.validators.core.ISourceModuleValidator;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.NullValidatorOutput;
import org.eclipse.dltk.validators.core.ValidatorRuntime;
import org.eclipse.dltk.validators.core.ValidatorRuntime.AutomaticValidatorPredicate;

public class ValidatorBuilder implements IScriptBuilder {
    private static final boolean DEBUG = false;

    private static final int WORK_EXTERNAL = 200;

    public void prepare(IBuildChange change, IBuildState state, IProgressMonitor monitor) throws CoreException {
        // NOP
    }

    public void build(IBuildChange change, IBuildState state, IProgressMonitor monitor) throws CoreException {
        final IScriptProject project = change.getScriptProject();
        final List<ISourceModule> elements = change.getSourceModules(IProjectChange.DEFAULT);
        final IValidator[] validators = ValidatorRuntime.getProjectValidators(
                project,
                ISourceModuleValidator.class,
                new AutomaticValidatorPredicate(project));
        final int validatorWork = validators.length * WORK_EXTERNAL;
        monitor.beginTask("", validatorWork); //$NON-NLS-1$
        try {
            ValidatorRuntime.executeSourceModuleValidators(
                    project,
                    elements,
                    new NullValidatorOutput(),
                    validators,
                    ValidatorUtils.subMonitorFor(monitor, validatorWork));
        } finally {
            monitor.done();
        }
        final List<IFile> resources = change.getResources(IProjectChange.DEFAULT);
        final IProgressMonitor sub = new SubProgressMonitor(monitor, resources.size());
        try {
            ValidatorRuntime.executeAutomaticResourceValidators(project, resources, new NullValidatorOutput(), sub);
        } finally {
            sub.done();
        }
    }

    public void clean(IScriptProject project, IProgressMonitor monitor) {
        ValidatorRuntime.cleanAll(project, new ISourceModule[0], new IResource[] { project.getProject() }, monitor);
    }

    public boolean initialize(IScriptProject project) {
        return true;
    }

    public void endBuild(IScriptProject project, IBuildState state, IProgressMonitor monitor) {
        // NOP
    }

}
