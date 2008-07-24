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
package org.eclipse.dltk.internal.core.mixin;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.ReadWriteMonitor;
import org.eclipse.dltk.internal.core.BuiltinSourceModule;
import org.eclipse.dltk.internal.core.ExternalSourceModule;

public class MixinExternalProjectFragmentRequest extends MixinIndexRequest {

	protected final IProjectFragment fragment;
	protected final IDLTKLanguageToolkit toolkit;

	public MixinExternalProjectFragmentRequest(IProjectFragment fragment,
			IDLTKLanguageToolkit toolkit) {
		this.fragment = fragment;
		this.toolkit = toolkit;
	}

	protected String getName() {
		return fragment.getElementName();
	}

	protected void run() throws CoreException, IOException {
		final Set modules = getExternalSourceModules();
		final Index index = getProjectFragmentIndex(fragment);
		final IPath containerPath = fragment.getPath();
		final List changes = checkChanges(index, modules, containerPath,
				getEnvironment());
		if (DEBUG) {
			log("changes.size=" + changes.size()); //$NON-NLS-1$
		}
		if (changes.isEmpty()) {
			return;
		}
		final ReadWriteMonitor imon = index.monitor;
		imon.enterWrite();
		try {
			for (Iterator i = changes.iterator(); !isCancelled && i.hasNext();) {
				final Object change = i.next();
				if (change instanceof String) {
					index.remove((String) change);
				} else {
					indexSourceModule(index, toolkit, (ISourceModule) change,
							containerPath);
				}
			}
		} finally {
			try {
				index.save();
			} catch (IOException e) {
				DLTKCore.error("error saving index", e); //$NON-NLS-1$
			} finally {
				imon.exitWrite();
			}
		}
	}

	protected IEnvironment getEnvironment() {
		return EnvironmentManager.getEnvironment(fragment);
	}

	static class ExternalModuleVisitor implements IModelElementVisitor {
		final Set modules = new HashSet();

		public boolean visit(IModelElement element) {
			if (element.getElementType() == IModelElement.SOURCE_MODULE) {
				if (element instanceof ExternalSourceModule
						|| element instanceof BuiltinSourceModule) {
					modules.add(element);
				}
				return false;
			}
			return true;
		}
	}

	private Set getExternalSourceModules() throws ModelException {
		final ExternalModuleVisitor visitor = new ExternalModuleVisitor();
		fragment.accept(visitor);
		return visitor.modules;
	}

	public boolean equals(Object obj) {
		if (obj instanceof MixinExternalProjectFragmentRequest) {
			final MixinExternalProjectFragmentRequest other = (MixinExternalProjectFragmentRequest) obj;
			return fragment.equals(other) && toolkit.equals(other.toolkit);
		}
		return false;
	}

}
