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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.core.BuiltinSourceModule;

class ExternalModuleCollector implements IModelElementVisitor {
	final List<ISourceModule> elements = new ArrayList<ISourceModule>();
	private final IProgressMonitor monitor;

	public ExternalModuleCollector(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * Visit only external source modules, witch we aren't builded yet.
	 */
	public boolean visit(IModelElement element) {
		// monitor.worked(1);
		if (monitor.isCanceled()) {
			return false;
		}
		if (element.getElementType() == IModelElement.PROJECT_FRAGMENT) {
			if (!(element instanceof IProjectFragment && ((IProjectFragment) element)
					.isExternal())) {
				return false;
			}
			IProjectFragment fragment = (IProjectFragment) element;

			String localPath = EnvironmentPathUtils.getLocalPath(
					fragment.getPath()).toString();
			if (!localPath.startsWith("#")) { //$NON-NLS-1$
				this.monitor
						.subTask(Messages.ScriptBuilder_scanningExternalFolder
								+ localPath);
			}
		} else if (element.getElementType() == IModelElement.SOURCE_MODULE) {
			if (element instanceof IExternalSourceModule
					|| element instanceof BuiltinSourceModule) {
				elements.add((ISourceModule) element);
			}
			return false; // do not enter into source module content.
		} else if (element.getElementType() == IModelElement.SCRIPT_FOLDER) {
			IScriptFolder folder = (IScriptFolder) element;
			String localPath = EnvironmentPathUtils.getLocalPath(
					folder.getPath()).toString();
			if (!localPath.startsWith("#")) { //$NON-NLS-1$
				this.monitor
						.subTask(Messages.ScriptBuilder_scanningExternalFolder
								+ localPath);
			}
		}
		return true;
	}
}
