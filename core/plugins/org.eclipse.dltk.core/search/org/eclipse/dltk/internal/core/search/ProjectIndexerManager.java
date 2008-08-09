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
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
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

	// Contains list of indexers for selected nature.
	private static Map indexers;

	private synchronized static void initialize() {
		if (indexers != null) {
			return;
		}

		indexers = new HashMap(5);
		IConfigurationElement[] cfg = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTPOINT);

		for (int i = 0; i < cfg.length; i++) {
			String nature = cfg[i].getAttribute(NATURE_ATTR);
			if (indexers.get(nature) != null) {
				List elements = (List) indexers.get(nature);
				elements.add(cfg[i]);
			} else {
				List elements = new ArrayList();
				elements.add(cfg[i]);
				indexers.put(nature, elements);
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
		final List result = new ArrayList();
		final String[] natures = (String[]) indexers.keySet().toArray(
				new String[indexers.size()]);
		for (int i = 0; i < natures.length; ++i) {
			final IProjectIndexer[] byNature = getByNature(natures[i]);
			if (byNature != null) {
				for (int j = 0; j < byNature.length; ++j) {
					result.add(byNature[j]);
				}
			}
		}
		if (!result.isEmpty()) {
			return (IProjectIndexer[]) result
					.toArray(new IProjectIndexer[result.size()]);
		} else {
			return null;
		}
	}

	private static IProjectIndexer[] getByNature(String natureId) {
		final Object ext = indexers.get(natureId);
		if (ext != null) {
			if (ext instanceof IProjectIndexer[]) {
				return (IProjectIndexer[]) ext;
			} else if (ext instanceof List) {
				final List elements = (List) ext;
				final List result = new ArrayList(elements.size());
				// new IProjectIndexer[elements
				// .size()];
				for (int i = 0; i < elements.size(); ++i) {
					Object e = elements.get(i);
					if (e instanceof IProjectIndexer) {
						result.add(e);
					} else {
						final IConfigurationElement cfg = (IConfigurationElement) e;
						try {
							final IProjectIndexer builder = (IProjectIndexer) cfg
									.createExecutableExtension(CLASS_ATTR);
							result.add(builder);
						} catch (CoreException ex) {
							DLTKCore.error("Error creating ProjectIndexer", ex); //$NON-NLS-1$
						}
					}
				}
				if (!result.isEmpty()) {
					final IProjectIndexer[] array = (IProjectIndexer[]) result
							.toArray(new IProjectIndexer[result.size()]);
					indexers.put(natureId, array);
					return array;
				} else {
					indexers.remove(natureId);
					return null;
				}
			}
		}
		return null;
	}

	private static IProjectIndexer[] getIndexers(IScriptProject project) {
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(project);
		if (toolkit == null) {
			return null;
		}
		return getIndexers(toolkit.getNatureId());
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
		indexProject(DLTKCore.create(project));
	}

	/**
	 * @param scriptProject
	 */
	public static void indexProject(IScriptProject project) {
		final IProjectIndexer[] indexers = getIndexers(project);
		if (indexers != null) {
			for (int i = 0; i < indexers.length; ++i) {
				indexers[i].indexProject(project);
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

}
