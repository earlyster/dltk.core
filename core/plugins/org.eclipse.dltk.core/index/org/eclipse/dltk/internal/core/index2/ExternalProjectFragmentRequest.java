/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.internal.core.index2;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.internal.core.BuiltinSourceModule;
import org.eclipse.dltk.internal.core.ExternalSourceModule;
import org.eclipse.dltk.internal.core.search.processing.JobManager;

/**
 * Request for indexing external project
 * 
 * @author michael
 * 
 */
public class ExternalProjectFragmentRequest extends AbstractIndexRequest {

	protected final IProjectFragment fragment;

	public ExternalProjectFragmentRequest(AbstractProjectIndexer indexer,
			IProjectFragment fragment) {
		super(indexer);
		this.fragment = fragment;
	}

	protected String getName() {
		return fragment.getElementName();
	}

	protected void run() throws CoreException, IOException {
		final Set<ISourceModule> sourceModules = getExternalSourceModules();
		JobManager jobManager = projectIndexer.getJobManager();
		jobManager.request(new SourceModulesRequest(projectIndexer, fragment
				.getPath(), sourceModules));
	}

	protected IEnvironment getEnvironment() {
		return EnvironmentManager.getEnvironment(fragment);
	}

	private Set<ISourceModule> getExternalSourceModules() throws ModelException {
		final Set<ISourceModule> modules = new HashSet<ISourceModule>();
		IModelElementVisitor visitor = new IModelElementVisitor() {
			public boolean visit(IModelElement element) {
				if (element.getElementType() == IModelElement.SOURCE_MODULE) {
					if (element instanceof ExternalSourceModule
							|| element instanceof BuiltinSourceModule) {
						modules.add((ISourceModule) element);
					}
					return false;
				}
				return true;
			}
		};
		fragment.accept(visitor);
		return modules;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fragment == null) ? 0 : fragment.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalProjectFragmentRequest other = (ExternalProjectFragmentRequest) obj;
		if (fragment == null) {
			if (other.fragment != null)
				return false;
		} else if (!fragment.equals(other.fragment))
			return false;
		return true;
	}
}
