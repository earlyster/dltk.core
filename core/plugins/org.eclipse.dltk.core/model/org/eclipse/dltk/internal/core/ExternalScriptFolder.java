/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.core.util.MementoTokenizer;
import org.eclipse.dltk.internal.core.util.Util;

public class ExternalScriptFolder extends ScriptFolder {
	public ExternalScriptFolder(ProjectFragment parent, IPath path) {
		super(parent, path);
	}

	void computeForeignResources(ExternalScriptFolderInfo info, List resNames) {
		if (resNames == null) {
			info.setForeignResources(ModelElementInfo.NO_NON_SCRIPT_RESOURCES);
			return;
		}
		int max = resNames.size();
		if (max == 0) {
			info.setForeignResources(ModelElementInfo.NO_NON_SCRIPT_RESOURCES);
		} else {
			Object[] res = new Object[max];
			int index = 0;
			for (int i = 0; i < max; i++) {
				IPath resPath = ((IPath) resNames.get(i));
				if (!Util.isValidSourceModule(getScriptProject(), resPath)) {
					res[index++] = new ExternalEntryFile(EnvironmentPathUtils.getFile(resPath));
				}
			}
			if (index != max) {
				System.arraycopy(res, 0, res = new Object[index], 0, index);
			}
			info.setForeignResources(res);
		}
	}

	public ISourceModule getSourceModule(String name) {
		IPath path = getPath().append(name);
		ExternalEntryFile storage = new ExternalEntryFile(EnvironmentPathUtils.getFile(path));
		return new ExternalSourceModule(this, name,
				DefaultWorkingCopyOwner.PRIMARY, storage);
	}

	protected boolean computeChildren(OpenableElementInfo info, List entryNames) {
		if (entryNames != null && entryNames.size() > 0) {
			ArrayList vChildren = new ArrayList();
			for (Iterator iter = entryNames.iterator(); iter.hasNext();) {
				String child = ((IPath) iter.next()).lastSegment();
				ISourceModule classFile = getSourceModule(child);
				vChildren.add(classFile);
			}
			IModelElement[] children = new IModelElement[vChildren.size()];
			vChildren.toArray(children);
			info.setChildren(children);
		} else {
			info.setChildren(NO_ELEMENTS);
		}
		return true;
	}

	public ISourceModule[] getSourceModules() throws ModelException {
		List<IModelElement> list = getChildrenOfType(SOURCE_MODULE);
		ISourceModule[] array = new ISourceModule[list.size()];
		list.toArray(array);
		return array;
	}

	public boolean isReadOnly() {
		return true;
	}

	// Open my archive: this creates all the pkg infos
	protected void generateInfos(Object info, HashMap newElements,
			IProgressMonitor pm) throws ModelException {
		// Open my archive: this creates all the pkg infos
		Openable openableParent = (Openable) this.parent;
		if (!openableParent.isOpen()) {
			openableParent.generateInfos(openableParent.createElementInfo(),
					newElements, pm);
		}
	}

	public IModelElement getHandleFromMemento(String token,
			MementoTokenizer memento, WorkingCopyOwner owner) {
		switch (token.charAt(0)) {
		case JEM_SOURCEMODULE:
			if (!memento.hasMoreTokens())
				return this;
			String classFileName = memento.nextToken();
			ModelElement classFile = (ModelElement) getSourceModule(classFileName);
			return classFile.getHandleFromMemento(memento, owner);
		}
		return null;
	}

	protected Object createElementInfo() {
		return null; // not used for ExternalScriptFolders: info is created
		// when directory are opened.
	}

	protected boolean resourceExists() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace == null)
			return false; // workaround for
		// http://bugs.eclipse.org/bugs/show_bug.cgi?id=34069
		IFileHandle file = EnvironmentManager.getEnvironment(this).getFile(
				getPath());
		return file != null && file.exists() && file.isDirectory();
	}

	public Object[] getForeignResources() throws ModelException {
		return ((ExternalScriptFolderInfo) getElementInfo())
				.getForeignResources();
	}
}
