/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.wizards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IEnvironment;

public class LinkedProjectModel {

	public static interface IFolderProvider {
		ProjectFolder[] getFolders();
	}

	public static class ProjectFolder {
		public static final int KIND_SOURCE = 0;
		public static final int KIND_RESOURCE = 1;
		public static final int KIND_EXTERNAL = 2;

		public static String describeKind(int kind) {
			switch (kind) {
			case KIND_SOURCE:
				return Messages.ProjectFolder_kind_sourceFolder;
			case KIND_RESOURCE:
				return Messages.ProjectFolder_kind_folder;
			case KIND_EXTERNAL:
				return Messages.ProjectFolder_kind_libraryFolder;
			default:
				return Messages.ProjectFolder_kind_other
						+ Integer.toString(kind);
			}
		}

		public static int countLinked(ProjectFolder[] folders) {
			int result = 0;
			for (int i = 0; i < folders.length; ++i) {
				final ProjectFolder folder = folders[i];
				if (folder.getKind() == ProjectFolder.KIND_SOURCE
						|| folder.getKind() == ProjectFolder.KIND_RESOURCE) {
					++result;
				}
			}
			return result;
		}

		private int kind;
		private IPath path;
		private String localFolderName;

		/**
		 * @return the kind
		 */
		public int getKind() {
			return kind;
		}

		/**
		 * @param kind
		 *            the kind to set
		 */
		public void setKind(int kind) {
			this.kind = kind;
		}

		/**
		 * @return the path
		 */
		public IPath getPath() {
			return path;
		}

		/**
		 * @param path
		 *            the path to set
		 */
		public void setPath(IPath path) {
			this.path = path;
		}

		/**
		 * @return the localFolderName
		 */
		public String getLocalFolderName() {
			return localFolderName;
		}

		/**
		 * @param localFolderName
		 *            the localFolderName to set
		 */
		public void setLocalFolderName(String localFolderName) {
			this.localFolderName = localFolderName;
		}

		public boolean isEmptyLocalFolderName() {
			return localFolderName == null || localFolderName.length() == 0;
		}

		/**
		 * @param kind
		 * @return
		 */
		public static boolean isValidKind(int kind) {
			return kind == ProjectFolder.KIND_SOURCE
					|| kind == ProjectFolder.KIND_EXTERNAL
					|| kind == ProjectFolder.KIND_RESOURCE;
		}
	}

	private final IEnvironment environment;
	private final IPath location;
	private final List folders = new ArrayList();

	/**
	 * @param environment2
	 * @param location2
	 */
	public LinkedProjectModel(IEnvironment environment2, IPath location2) {
		this.environment = environment2;
		this.location = location2;
	}

	/**
	 * @return the environment
	 */
	public IEnvironment getEnvironment() {
		return environment;
	}

	/**
	 * @return the location
	 */
	public IPath getLocation() {
		return location;
	}

	/**
	 * @param oEnvironment
	 * @param oLocation
	 * @return
	 */
	public boolean matches(IEnvironment oEnvironment, IPath oLocation) {
		return equals(environment, oEnvironment) && equals(location, oLocation);
	}

	/**
	 * @param a
	 * @param b
	 * @return
	 */
	private static boolean equals(Object a, Object b) {
		if (a != null) {
			return a.equals(b);
		} else {
			return b == null;
		}
	}

	/**
	 * @return
	 */
	public ProjectFolder[] getFolders() {
		return (ProjectFolder[]) folders.toArray(new ProjectFolder[folders
				.size()]);
	}

	/**
	 * @param projectFolder
	 */
	public void addFolder(ProjectFolder projectFolder) {
		folders.add(projectFolder);
	}

	/**
	 * @param path
	 * @return
	 */
	public ProjectFolder findFolder(IPath path) {
		for (Iterator i = folders.iterator(); i.hasNext();) {
			final ProjectFolder folder = (ProjectFolder) i.next();
			if (path.equals(folder.path)) {
				return folder;
			}
		}
		return null;
	}

	/**
	 * @param next
	 */
	public void removeFolder(ProjectFolder projectFolder) {
		folders.remove(projectFolder);
	}

}
