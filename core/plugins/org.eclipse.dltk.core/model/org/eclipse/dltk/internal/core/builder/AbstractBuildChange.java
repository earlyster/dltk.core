/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.core.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.builder.IProjectChange;
import org.eclipse.osgi.util.NLS;

public abstract class AbstractBuildChange implements IProjectChange {
	protected final IProgressMonitor monitor;
	protected final IProject project;

	public AbstractBuildChange(IProject project, IProgressMonitor monitor) {
		this.project = project;
		this.monitor = monitor;
	}

	public IProject getProject() {
		return project;
	}

	private IScriptProject scriptProject = null;

	public IScriptProject getScriptProject() {
		if (scriptProject == null) {
			scriptProject = DLTKCore.create(project);
		}
		return scriptProject;
	}

	private static final int ADD_OR_CHANGE = ADDED | CHANGED;

	protected static int validateFlags(int options, int allowedOptions) {
		if ((options | allowedOptions) != allowedOptions) {
			throw new IllegalArgumentException("Wrong options 0x"
					+ Integer.toHexString(options) + ", allowed options 0x"
					+ Integer.toHexString(allowedOptions));
		}
		if ((allowedOptions & ADD_OR_CHANGE) == ADD_OR_CHANGE) {
			if ((options & ADD_OR_CHANGE) == 0) {
				options |= ADD_OR_CHANGE;
			}
		}
		return options;
	}

	protected static boolean checkFlag(int options, int mask) {
		return (options & mask) != 0;
	}

	protected static boolean wantRenames(int options) {
		return !checkFlag(options, NO_RENAMES);
	}

	protected void locateSourceModules(Collection<IFile> resources,
			Collection<ISourceModule> sourceModules,
			Collection<IFile> realResources) {
		getScriptProject();
		int remainingWork = resources.size();
		for (final IFile file : resources) {
			checkCanceled();
			monitor.subTask(NLS.bind(
					Messages.ScriptBuilder_Locating_source_modules,
					String.valueOf(remainingWork), file.getName()));
			final IModelElement element = DLTKCore.create(file);
			if (element != null
					&& element.getElementType() == IModelElement.SOURCE_MODULE
					&& element.exists() && scriptProject.isOnBuildpath(element)) {
				sourceModules.add((ISourceModule) element);
			} else {
				realResources.add(file);
			}
			--remainingWork;
		}
	}

	private int checkCounter = 0;

	protected final void checkCanceled() {
		if ((checkCounter++ & 0xFF) == 0) {
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}

	protected static <T> List<T> unmodifiableList(List<T> input) {
		if (input.isEmpty()) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(input);
		}
	}

	protected static <T> Set<T> unmodifiableSet(Set<T> input) {
		if (input.isEmpty()) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(input);
		}
	}

	public boolean isOnBuildpath(IResource resource) {
		return getScriptProject().isOnBuildpath(resource);
	}

}
