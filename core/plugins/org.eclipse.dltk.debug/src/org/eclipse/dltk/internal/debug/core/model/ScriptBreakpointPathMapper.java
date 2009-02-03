/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jae Gangemi - initial API and Implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.debug.core.model;

import java.net.URI;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.debug.core.DLTKDebugPlugin;

public class ScriptBreakpointPathMapper implements IScriptBreakpointPathMapperExtension {
	private HashMap cache;
	private String mapTo;
	private IScriptProject scriptProject;
	private boolean stripSrcFolders;

	ScriptBreakpointPathMapper(IScriptProject project, String mapTo,
			boolean stripSrcFolders) {
		this.mapTo = mapTo;
		this.scriptProject = project;
		this.stripSrcFolders = stripSrcFolders;

		this.cache = new HashMap();
	}

	public void clearCache() {
		cache.clear();
	}

	public URI map(URI uri) {
		// no mapTo, return original uri
		if (mapTo == null || "".equals(mapTo)) { //$NON-NLS-1$
			return uri;
		}

		// check the cache
		if (cache.containsKey(uri)) {
			return (URI) cache.get(uri);
		}

		// now for the fun ;)
		final IPath projectPath = scriptProject.getProject().getLocation();
		if (projectPath == null) {
			return uri;
		}
		final IPath path = new Path(uri.getPath());
		// only map paths that start w/ the project path
		if (projectPath.isPrefixOf(path)) {
			IPath temp = path.removeFirstSegments(projectPath.segmentCount())
					.setDevice(null);
			if (stripSrcFolders) {
				temp = stripSourceFolders(temp);
			}
			final IPath outgoing = new Path(mapTo).append(temp);
			final URI result = ScriptLineBreakpoint.makeUri(outgoing);
			cache.put(uri, result);
			return result;
		}
		cache.put(uri, uri);
		return uri;
	}

	private IPath stripSourceFolders(IPath path) {
		try {
			IProjectFragment[] fragments = scriptProject.getProjectFragments();

			for (int i = 0; i < fragments.length; i++) {
				IProjectFragment frag = fragments[i];
				// skip external/archive
				if (frag.isExternal() || frag.isArchive()) {
					continue;
				}

				final String name = frag.getElementName();
				if (path.segmentCount() > 0 && path.segment(0).equals(name)) {
					return path.removeFirstSegments(1);
				}
			}
		} catch (CoreException e) {
			DLTKDebugPlugin.log(e);
		}

		return path;
	}
}
