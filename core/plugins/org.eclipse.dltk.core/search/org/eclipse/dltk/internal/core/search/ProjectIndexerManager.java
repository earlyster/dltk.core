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
import java.util.HashMap;
import java.util.HashSet;
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
import org.eclipse.osgi.util.NLS;

public class ProjectIndexerManager {

	private final static String EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".projectIndexer"; //$NON-NLS-1$

	private final static String NATURE_ATTR = "nature"; //$NON-NLS-1$
	private static final String CLASS_ATTR = "class"; //$NON-NLS-1$
	private static final String ID_ATTR = "id"; //$NON-NLS-1$
	private static final String INDEXER_ATTR = "indexer"; //$NON-NLS-1$
	private static final String ENABLE_ELEM = "enable"; //$NON-NLS-1$
	private static final String DISABLE_ELEM = "disable"; //$NON-NLS-1$
	private static final String INDEXER_ELEM = "projectIndexer"; //$NON-NLS-1$

	// Contains list of indexers for selected nature.
	private static Map<String, ProjectIndexerDescriptor> indexers;
	private static final Map<String, Set<String>> enabledIndexers = new HashMap<String, Set<String>>();
	private static final Map<String, Set<String>> disabledIndexers = new HashMap<String, Set<String>>();

	private static class ProjectIndexerDescriptor {
		private final String id;
		private IConfigurationElement element;
		private IProjectIndexer projectIndexer;

		ProjectIndexerDescriptor(String id, IConfigurationElement element) {
			this.id = id;
			this.element = element;
		}

		public IProjectIndexer getObject() {
			if (projectIndexer == null) {
				try {
					projectIndexer = (IProjectIndexer) element
							.createExecutableExtension(CLASS_ATTR);

					for (Map.Entry<String, Set<String>> entry : disabledIndexers
							.entrySet()) {
						if (entry.getValue().contains(id)) {
							projectIndexer.disableForNature(entry.getKey());
						}
					}
				} catch (CoreException e) {
					DLTKCore.error("Error initializing project indexer", e); //$NON-NLS-1$
					synchronized (ProjectIndexerManager.class) {
						indexers.remove(id);
					}
					return null;
				}
			}
			return projectIndexer;
		}
	}

	private synchronized static void initialize() {
		if (indexers != null) {
			return;
		}

		indexers = new HashMap<String, ProjectIndexerDescriptor>();
		for (IConfigurationElement element : Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTPOINT)) {
			if (DISABLE_ELEM.equals(element.getName())) {
				final String nature = element.getAttribute(NATURE_ATTR);
				Set<String> disabledForNature = disabledIndexers.get(nature);
				if (disabledForNature == null) {
					disabledForNature = new HashSet<String>();
					disabledIndexers.put(nature, disabledForNature);
				}
				disabledForNature.add(element.getAttribute(INDEXER_ATTR));
			} else if (ENABLE_ELEM.equals(element.getName())) {
				final String nature = element.getAttribute(NATURE_ATTR);
				Set<String> enabledForNature = enabledIndexers.get(nature);
				if (enabledForNature == null) {
					enabledForNature = new HashSet<String>();
					enabledIndexers.put(nature, enabledForNature);
				}
				enabledForNature.add(element.getAttribute(INDEXER_ATTR));
			} else if (INDEXER_ELEM.equals(element.getName())) {
				final String id = element.getAttribute(ID_ATTR);
				if (id == null) {
					DLTKCore
							.warn(NLS
									.bind(
											"{0} contributed by {1} does not have \"{2}\" attribute", //$NON-NLS-1$
											new Object[] {
													INDEXER_ELEM,
													element.getContributor()
															.getName(), ID_ATTR }));
					continue;
				}
				if (indexers.containsKey(id)) {
					DLTKCore
							.warn(NLS
									.bind(
											"Duplicate {0} contribution from {1} (previous one is from \"{2}\")", //$NON-NLS-1$
											new Object[] {
													INDEXER_ELEM,
													element.getContributor()
															.getName(),
													indexers.get(id).element
															.getContributor()
															.getName() }));
					continue;
				}
				indexers.put(id, new ProjectIndexerDescriptor(id, element));
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
		IProjectIndexer[] nature = getByNature(natureId, natureId);
		IProjectIndexer[] all = getByNature("#", natureId); //$NON-NLS-1$
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

		final Set<String> indexerIds = new HashSet<String>();
		for (Set<String> ids : enabledIndexers.values()) {
			indexerIds.addAll(ids);
		}
		return getIndexers(indexerIds, null);
	}

	private static IProjectIndexer[] getByNature(String natureId,
			String disabledNature) {
		Set<String> indexerIds = enabledIndexers.get(natureId);
		if (indexerIds != null) {
			return getIndexers(indexerIds, disabledNature);
		} else {
			return null;
		}
	}

	private static IProjectIndexer[] getIndexers(Set<String> indexerIds,
			String disabledNature) {
		final List<IProjectIndexer> result = new ArrayList<IProjectIndexer>(
				indexerIds.size());
		final Set<String> disabled = disabledIndexers.get(disabledNature);
		for (String indexerId : indexerIds) {
			if (disabled != null && disabled.contains(indexerId)) {
				continue;
			}
			final ProjectIndexerDescriptor descriptor = indexers.get(indexerId);
			if (descriptor != null) {
				final IProjectIndexer indexer = descriptor.getObject();
				if (indexer != null) {
					result.add(indexer);
				}
			}
		}
		if (!result.isEmpty()) {
			return result.toArray(new IProjectIndexer[result.size()]);
		} else {
			return null;
		}
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
