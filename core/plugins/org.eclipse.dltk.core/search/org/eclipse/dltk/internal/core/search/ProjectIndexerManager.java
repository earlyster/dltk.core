/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;

public class ProjectIndexerManager {

	private final static String EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".projectIndexer"; //$NON-NLS-1$

	private final static String NATURE_ATTR = "nature"; //$NON-NLS-1$
	private static final String CLASS_ATTR = "class"; //$NON-NLS-1$
	private static final String DISABLE_ELEM = "disable"; //$NON-NLS-1$
	private static final String INDEXER_ELEM = "projectIndexer"; //$NON-NLS-1$

	// Contains list of indexers for selected nature.
	private static Map<String, List<ProjectIndexerDescriptor>> indexers;

	private static class ProjectIndexerDescriptor {
		private IConfigurationElement element;
		private IProjectIndexer projectIndexer;

		ProjectIndexerDescriptor(IConfigurationElement element) {
			this.element = element;
		}

		public IProjectIndexer getObject() {
			if (projectIndexer == null) {
				try {
					projectIndexer = (IProjectIndexer) element
							.createExecutableExtension(CLASS_ATTR);
				} catch (CoreException e) {
					DLTKCore.error("Error initializing project indexer", e); //$NON-NLS-1$
				}
			}
			return projectIndexer;
		}
	}

	private synchronized static void initialize() {
		if (indexers != null) {
			return;
		}

		Map<String, IConfigurationElement> enabled = new HashMap<String, IConfigurationElement>();
		for (IConfigurationElement element : Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTPOINT)) {
			if (DISABLE_ELEM.equals(element.getName())) {
				enabled.put(element.getAttribute(CLASS_ATTR), null);
			} else if (INDEXER_ELEM.equals(element.getName())) {
				String className = element.getAttribute(CLASS_ATTR);
				if (!enabled.containsKey(className)) {
					enabled.put(className, element);
				}
			}
		}

		indexers = new HashMap<String, List<ProjectIndexerDescriptor>>();

		Iterator<String> i = enabled.keySet().iterator();
		while (i.hasNext()) {
			String className = i.next();
			IConfigurationElement element = enabled.get(className);
			if (element != null) {
				String nature = element.getAttribute(NATURE_ATTR);
				List<ProjectIndexerDescriptor> elements = indexers.get(nature);
				if (elements == null) {
					elements = new LinkedList<ProjectIndexerDescriptor>();
					indexers.put(nature, elements);
				}
				elements.add(new ProjectIndexerDescriptor(element));
			}
		}
	}

	/**
	 * Return merged with all elements with nature #
	 * 
	 * @param natureId
	 * @return
	 * @throws CoreException
	 */
	public static IProjectIndexer[] getIndexers(String natureId) {
		initialize();
		IProjectIndexer[] nature = getByNature(natureId);
		IProjectIndexer[] all = getByNature("#"); //$NON-NLS-1$
		if (all == null) {
			return nature;
		}
		if (nature == null) {
			return all;
		}
		final IProjectIndexer[] result = new IProjectIndexer[nature.length
				+ all.length];
		System.arraycopy(nature, 0, result, 0, nature.length);
		System.arraycopy(all, 0, result, nature.length, all.length);
		return result;
	}

	public static IProjectIndexer[] getAllIndexers() {
		initialize();

		Set<String> natures = indexers.keySet();
		List<IProjectIndexer> result = new ArrayList<IProjectIndexer>(natures
				.size());
		for (String natureId : natures) {
			final IProjectIndexer[] byNature = getByNature(natureId);
			if (byNature != null) {
				result.addAll(Arrays.asList(byNature));
			}
		}
		if (!result.isEmpty()) {
			return result.toArray(new IProjectIndexer[result.size()]);
		}
		return null;
	}

	private static IProjectIndexer[] getByNature(String natureId) {
		List<ProjectIndexerDescriptor> elements = indexers.get(natureId);
		if (elements != null) {
			List<IProjectIndexer> result = new ArrayList<IProjectIndexer>(
					elements.size());
			for (ProjectIndexerDescriptor descriptor : elements) {
				result.add(descriptor.getObject());
			}
			if (!result.isEmpty()) {
				return (IProjectIndexer[]) result
						.toArray(new IProjectIndexer[result.size()]);
			}
		}
		return null;
	}

	private static IProjectIndexer[] getIndexers(IScriptProject project) {
		return getIndexers(project, true);
	}

	private static IProjectIndexer[] getIndexers(IScriptProject project,
			boolean checkEnable) {
		if (checkEnable && !isIndexerEnabled(project.getProject())) {
			return null;
		}
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(project);
		if (toolkit == null) {
			return null;
		}
		return getIndexers(toolkit.getNatureId());
	}

	public static boolean isIndexerEnabled(final IProject project) {
		return new ProjectScope(project).getNode(DLTKCore.PLUGIN_ID)
				.getBoolean(DLTKCore.INDEXER_ENABLED, true);
	}

	/**
	 * Removes the indexes for a given path. This is a no-op if the index did
	 * not exist.
	 * 
	 * @param project
	 * 
	 * @param path
	 */
	public static void removeLibrary(IScriptProject project, IPath path) {
		final IProjectIndexer[] indexers = getIndexers(project);
		if (indexers != null) {
			for (int i = 0; i < indexers.length; ++i) {
				indexers[i].removeLibrary(project, path);
			}
		}
	}

	/**
	 * @param projectPath
	 */
	public static void removeProject(IPath projectPath) {
		final IProjectIndexer[] indexers = getAllIndexers();
		if (indexers != null) {
			for (int i = 0; i < indexers.length; ++i) {
				indexers[i].removeProject(projectPath);
			}
		}
	}

	/**
	 * @param project
	 * @param path
	 */
	public static void indexLibrary(IScriptProject project, IPath path) {
		final IProjectIndexer[] indexers = getIndexers(project);
		if (indexers != null) {
			for (int i = 0; i < indexers.length; ++i) {
				indexers[i].indexLibrary(project, path);
			}
		}
	}

	/**
	 * @param project
	 * @param path
	 */
	public static void removeProjectFragment(IScriptProject project, IPath path) {
		final IProjectIndexer[] indexers = getIndexers(project);
		if (indexers != null) {
			for (int i = 0; i < indexers.length; ++i) {
				indexers[i].removeProjectFragment(project, path);
			}
		}
	}

	/**
	 * @param project
	 * @param path
	 */
	public static void indexProjectFragment(IScriptProject project, IPath path) {
		final IProjectIndexer[] indexers = getIndexers(project);
		if (indexers != null) {
			for (int i = 0; i < indexers.length; ++i) {
				indexers[i].indexProjectFragment(project, path);
			}
		}
	}

	/**
	 * @param res
	 */
	public static void indexProject(IProject project) {
		if (isIndexerEnabled(project)) {
			indexProject(project, DLTKCore.create(project));
		}
	}

	public static void indexProject(IScriptProject scriptProject) {
		final IProject project = scriptProject.getProject();
		if (isIndexerEnabled(project)) {
			indexProject(project, scriptProject);
		}
	}

	/**
	 * @param scriptProject
	 */
	private static void indexProject(IProject project,
			IScriptProject scriptProject) {
		final IProjectIndexer[] indexers = getIndexers(scriptProject, false);
		if (indexers != null) {
			for (int i = 0; i < indexers.length; ++i) {
				indexers[i].indexProject(scriptProject);
			}
		}
	}

	/**
	 * @param project
	 * @param path
	 */
	public static void removeSourceModule(IScriptProject project, String path) {
		final IProjectIndexer[] indexers = getIndexers(project);
		if (indexers != null) {
			for (int i = 0; i < indexers.length; ++i) {
				indexers[i].removeSourceModule(project, path);
			}
		}
	}

	/**
	 * @param module
	 * @param toolkit
	 */
	public static void indexSourceModule(ISourceModule module,
			IDLTKLanguageToolkit toolkit) {
		final IProjectIndexer[] indexers = getIndexers(toolkit.getNatureId());
		if (indexers != null) {
			for (int i = 0; i < indexers.length; ++i) {
				indexers[i].indexSourceModule(module, toolkit);
			}
		}
	}

	/**
	 * @param workingCopy
	 */
	public static void reconciled(ISourceModule workingCopy) {
		final IScriptProject project = workingCopy.getScriptProject();
		if (project == null) {
			return;
		}
		if (!isIndexerEnabled(project.getProject())) {
			return;
		}
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(project);
		if (toolkit == null) {
			return;
		}
		final IProjectIndexer[] indexers = getIndexers(toolkit.getNatureId());
		if (indexers != null) {
			for (int i = 0; i < indexers.length; ++i) {
				indexers[i].reconciled(workingCopy, toolkit);
			}
		}
	}

	public static void startIndexing() {
		final IProjectIndexer[] indexers = getAllIndexers();
		if (indexers != null) {
			for (int i = 0; i < indexers.length; ++i) {
				indexers[i].startIndexing();
			}
		}
	}

}
