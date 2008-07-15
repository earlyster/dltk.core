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
package org.eclipse.dltk.validators.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.validators.internal.core.ValidatorUtils;

public abstract class AbstractValidateJob extends Job {

	public AbstractValidateJob(String jobName) {
		super(jobName);
	}

	protected abstract void invokeValidationFor(final IValidatorOutput out,
			final IScriptProject project, final ISourceModule[] elements,
			final IResource[] resources, final IProgressMonitor monitor);

	private static class ProjectInfo {
		final List resources = new ArrayList();
		final List elements = new ArrayList();

		/**
		 * @return
		 */
		public ISourceModule[] elementsToArray() {
			return (ISourceModule[]) elements
					.toArray(new ISourceModule[elements.size()]);
		}

		/**
		 * @return
		 */
		public IResource[] resourcesToArray() {
			return (IResource[]) resources.toArray(new IResource[resources
					.size()]);
		}
	}

	private final Map byProject = new HashMap();

	public void run(Object[] selection) {
		final Set elements = new HashSet();
		final Set resources = new HashSet();
		for (int i = 0; i < selection.length; ++i) {
			Object o = selection[i];
			ValidatorUtils.processResourcesToElements(o, elements, resources);
		}
		for (Iterator i = elements.iterator(); i.hasNext();) {
			final ISourceModule module = (ISourceModule) i.next();
			final IScriptProject sproject = module.getScriptProject();
			if (sproject != null) {
				final IProject project = sproject.getProject();
				if (project != null) {
					getProjectInfo(project).elements.add(module);
				}
			}
		}
		for (Iterator i = resources.iterator(); i.hasNext();) {
			final IResource resource = (IResource) i.next();
			final IProject project = resource.getProject();
			if (project != null) {
				getProjectInfo(project).resources.add(resource);
			}
		}
		setRule(buildSchedulingRule(elements, resources));
		setUser(true);
		schedule();
	}

	private ProjectInfo getProjectInfo(final IProject project) {
		ProjectInfo info = (ProjectInfo) byProject.get(project);
		if (info == null) {
			info = new ProjectInfo();
			byProject.put(project, info);
		}
		return info;
	}

	private ISchedulingRule buildSchedulingRule(Set elements, Set resources) {
		final Set all = new HashSet(resources);
		for (Iterator i = elements.iterator(); i.hasNext();) {
			final ISourceModule module = (ISourceModule) i.next();
			final IResource resource = module.getResource();
			if (resource != null) {
				all.add(resource);
			}
		}
		final ISchedulingRule[] rules = new ISchedulingRule[all.size()];
		all.toArray(rules);
		return MultiRule.combine(rules);
	}

	protected IStatus run(IProgressMonitor monitor) {
		IValidatorOutput output = null;
		try {
			output = createOutput();
			for (Iterator i = byProject.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				final IProject project = (IProject) entry.getKey();
				final ProjectInfo info = (ProjectInfo) entry.getValue();
				invokeValidationFor(output, DLTKCore.create(project), info
						.elementsToArray(), info.resourcesToArray(), monitor);
			}
		} finally {
			try {
				if (output != null) {
					output.getStream().close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Status.OK_STATUS;
	}

	protected IValidatorOutput createOutput() {
		return new NullValidatorOutput();
	}

}
