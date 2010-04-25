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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelStatusConstants;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IProjectFragmentTimestamp;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.internal.environment.EFSFileHandle;
import org.eclipse.dltk.internal.core.util.MementoTokenizer;
import org.eclipse.dltk.internal.core.util.Util;

/**
 * Project fragment to external source folder.
 * 
 * @author haiodo
 * 
 */
public class ExternalProjectFragment extends ProjectFragment implements
		IProjectFragmentTimestamp {
	public final static ArrayList EMPTY_LIST = new ArrayList();
	/**
	 * The path to the zip file (a workspace relative path if the archive is
	 * internal, or an OS path if the archive is external)
	 */
	protected final IPath fPath;
	protected final boolean fReadOnly;
	protected final boolean fOnlyScriptResources;

	protected ExternalProjectFragment(IPath path, ScriptProject project,
			boolean isReadOnly, boolean onlyScriptResources) {
		super(null, project);
		this.fPath = path;
		this.fReadOnly = isReadOnly;
		this.fOnlyScriptResources = onlyScriptResources;
	}

	/**
	 * Compute the package fragment children of this package fragment root.
	 */
	protected boolean computeChildren(OpenableElementInfo info, Map newElements)
			throws ModelException {
		ArrayList vChildren = new ArrayList(5);
		ArrayList vForeign = new ArrayList(5);
		char[][] inclusionPatterns = this.fullInclusionPatternChars();
		char[][] exclusionPatterns = this.fullExclusionPatternChars();
		Set realPaths = new HashSet();
		this.computeFolderChildren(this.fPath, !Util.isExcluded(this.fPath,
				inclusionPatterns, exclusionPatterns, true), vChildren,
				vForeign, newElements, inclusionPatterns, exclusionPatterns,
				realPaths);
		IModelElement[] children = new IModelElement[vChildren.size()];
		vChildren.toArray(children);
		info.setChildren(children);
		return true;
	}

	/**
	 * Starting at this folder, create folders and add them to the collection of
	 * children.
	 * 
	 * @param newElements
	 * 
	 * @exception ModelException
	 *                The resource associated with this project fragment does
	 *                not exist
	 */
	protected void computeFolderChildren(IPath path, boolean isIncluded,
			ArrayList vChildren, ArrayList vForeign, Map newElements,
			char[][] inclusionPatterns, char[][] exclusionPatterns,
			Set realPaths) throws ModelException {
		IEnvironment environment = EnvironmentPathUtils
				.getPathEnvironment(path);
		if (environment != null) {
			IFileHandle file = environment.getFile(EnvironmentPathUtils
					.getLocalPath(path));
			String canonicalPath = file.getCanonicalPath();
			if (!realPaths.add(canonicalPath)) {
				return;
			}
		}
		IPath lpath = path.setDevice(null).removeFirstSegments(
				this.fPath.segmentCount());

		ExternalScriptFolder fldr = (ExternalScriptFolder) this
				.getScriptFolder(lpath);
		boolean valid = Util.isValidSourcePackageName(this, path);
		if ((lpath.segmentCount() == 0 || valid) && isIncluded) {
			vChildren.add(fldr);
		} else {
			if (this.fOnlyScriptResources) {
				return;
			}
			if (!valid) {
				return;
			}
		}
		List scriptElements = new ArrayList();
		List nonScriptElements = new ArrayList();
		try {
			IFileHandle file = EnvironmentPathUtils.getFile(path);
			IFileHandle[] members = file.getChildren();
			if (members != null) {
				for (int i = 0, max = members.length; i < max; i++) {
					IFileHandle memberFile = members[i];
					IPath memberPath = memberFile.getFullPath();
					if (memberFile.isDirectory()) {
						boolean isMemberIncluded = !Util.isExcluded(memberPath,
								inclusionPatterns, exclusionPatterns, true);
						computeFolderChildren(memberPath, isMemberIncluded,
								vChildren, vForeign, newElements,
								inclusionPatterns, exclusionPatterns, realPaths);
					} else {
						if (Util.isValidSourceModule(this, memberPath)) {
							scriptElements.add(memberPath);
						} else {
							if (!this.fOnlyScriptResources || valid) {
								nonScriptElements.add(memberPath);
							}
						}
					}
				}
			}
			ExternalScriptFolderInfo fragInfo = new ExternalScriptFolderInfo();
			fldr.computeChildren(fragInfo, scriptElements);
			fldr.computeForeignResources(fragInfo, nonScriptElements);
			newElements.put(fldr, fragInfo);
		} catch (IllegalArgumentException e) {
			throw new ModelException(e,
					IModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
			/*
			 * could be thrown by ElementTree when path is not found
			 */
		} catch (CoreException e) {
			throw new ModelException(e);
		}
	}

	public void getHandleMemento(StringBuffer buff) {
		((ModelElement) getParent()).getHandleMemento(buff);
		buff.append(getHandleMementoDelimiter());
		escapeMementoName(buff, getElementName());
	}

	public IScriptFolder getScriptFolder(IPath path) {
		try {
			final String pathStr = path.toPortableString();
			List childs = getChildrenOfType(SCRIPT_FOLDER);
			for (int i = 0; i < childs.size(); ++i) {
				IScriptFolder folder = (IScriptFolder) childs.get(i);
				if (folder.getElementName().equals(pathStr)) {
					return folder;
				}
			}
		} catch (ModelException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		return new ExternalScriptFolder(this, path);
	}

	public IScriptFolder getScriptFolder(String path) {
		return this.getScriptFolder(new Path(path));
	}

	public boolean isReadOnly() {
		return this.fReadOnly;
	}

	protected Object createElementInfo() {
		return new ExternalProjectFragmentInfo();
	}

	public boolean isArchive() {
		return false;
	}

	public boolean isExternal() {
		return true;
	}

	public IResource getUnderlyingResource() throws ModelException {
		return null;
	}

	public int hashCode() {
		return this.fPath.hashCode();
	}

	public IPath getPath() {
		return this.fPath;
	}

	public IResource getResource() {
		return null;
	}

	/**
	 * Returns whether the corresponding resource or associated file exists
	 */
	protected boolean resourceExists() {
		if (fPath.toString().startsWith(
				IBuildpathEntry.BUILTIN_EXTERNAL_ENTRY_STR)) {
			return true;
		}
		IFileHandle file = EnvironmentPathUtils.getFile(fPath);
		return file.exists() && file.isDirectory();
	}

	protected void toStringAncestors(StringBuffer buffer) {
	}

	public int getKind() {
		return IProjectFragment.K_SOURCE;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof ExternalProjectFragment) {
			ExternalProjectFragment other = (ExternalProjectFragment) o;
			IEnvironment environment = EnvironmentManager.getEnvironment(this);
			if (environment != null) {
				IEnvironment environmento = EnvironmentManager
						.getEnvironment((IModelElement) other);
				if (!environment.equals(environmento)) {
					return false;
				}
			}
			return this.fPath.equals(other.fPath);
		}
		return false;
	}

	public String getElementName() {
		IEnvironment env = EnvironmentManager.getEnvironment(this);
		if (env == null) {
			env = EnvironmentPathUtils.getPathEnvironment(fPath);
		}
		String pathString = EnvironmentPathUtils.getLocalPathString(fPath);
		if (env != null && pathString != null) {
			return pathString.replace(env.getSeparatorChar(),
					JEM_SKIP_DELIMETER);
		}
		return fPath.lastSegment();
	}

	public IModelElement getHandleFromMemento(String token,
			MementoTokenizer memento, WorkingCopyOwner owner) {
		switch (token.charAt(0)) {
		case JEM_SCRIPTFOLDER:
			String pkgName;
			if (memento.hasMoreTokens()) {
				pkgName = memento.nextToken();
				char firstChar = pkgName.charAt(0);
				if (firstChar == JEM_SOURCEMODULE || firstChar == JEM_COUNT) {
					token = pkgName;
					pkgName = IProjectFragment.DEFAULT_SCRIPT_FOLDER_NAME;
				} else {
					token = null;
				}
			} else {
				pkgName = IScriptFolder.DEFAULT_FOLDER_NAME;
				token = null;
			}
			ModelElement pkg = (ModelElement) this.getScriptFolder(pkgName);
			if (token == null) {
				return pkg.getHandleFromMemento(memento, owner);
			} else {
				return pkg.getHandleFromMemento(token, memento, owner);
			}
		}
		return null;
	}

	protected char getHandleMementoDelimiter() {
		return JEM_PROJECTFRAGMENT;
	}

	public IBuildpathEntry getBuildpathEntry() throws ModelException {
		IBuildpathEntry rawEntry = super.getRawBuildpathEntry();
		// try to guest map from internal element.
		if (rawEntry != null
				&& rawEntry.getEntryKind() == IBuildpathEntry.BPE_CONTAINER) {
			IBuildpathContainer container = DLTKCore.getBuildpathContainer(
					rawEntry.getPath(), this.getScriptProject());
			IBuildpathEntry entrys[] = container.getBuildpathEntries();
			for (int i = 0; i < entrys.length; ++i) {
				if (entrys[i].getPath().equals(this.getPath())) {
					return entrys[i];
				}
			}
		}

		return rawEntry;
	}

	public long getTimeStamp() {
		// All files inside timestamps hash.
		IEnvironment environment = EnvironmentManager.getEnvironment(this);
		try {
			long stamp = 0;
			IFileHandle file = environment.getFile(this.getPath());
			if (file != null && file.exists()) {
				long lmodif = 0;
				if (file instanceof EFSFileHandle) {
					lmodif = ((EFSFileHandle) file).lastModified();
				} else {
					lmodif = file.lastModified();
				}
				stamp = lmodif;
			} else {
				return 0;
			}
			IModelElement[] children = getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i].getElementType() == IModelElement.SCRIPT_FOLDER) {
					IScriptFolder folder = (IScriptFolder) children[i];
					IPath path = folder.getPath();
					file = environment.getFile(path);
					if (file != null && file.exists()) {
						stamp = stamp * 13 + file.lastModified();
					}
				}
			}
		} catch (ModelException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		return 0;
	}
}
