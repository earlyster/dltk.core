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

import java.io.File;
import java.net.URI;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.debug.core.DLTKDebugPlugin;

public class ScriptBreakpointPathMapper implements IScriptBreakpointPathMapper {
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
		String path = uri.getPath();
		// no mapTo, return original uri
		if (mapTo == null || "".equals(mapTo)) { //$NON-NLS-1$
			return uri;
		}

		// check the cache
		if (cache.containsKey(uri)) {
			return (URI) cache.get(uri);
		}

		// now for the fun ;)
		String projectPath = scriptProject.getProject().getLocation()
				.toOSString();

		String outgoing = path;

		// only map paths that start w/ the project path
		if (path.startsWith(projectPath)) {
			path = path.substring(projectPath.length() + 1);

			if (stripSrcFolders) {
				path = stripSourceFolders(path);
			}
			/*
			 * use the platform file separator b/c that's what's returned from
			 * toOSString in the project path
			 */
			outgoing = mapTo + File.separator + path;
		}

		URI outgoingUri = ScriptLineBreakpoint.makeUri(new Path(outgoing));
		cache.put(uri, outgoingUri);

		return outgoingUri;
	}

	private String stripSourceFolders(String path) {
		try {
			IProjectFragment[] fragments = scriptProject.getProjectFragments();

			for (int i = 0; i < fragments.length; i++) {
				IProjectFragment frag = fragments[i];
				// skip external/archive
				if (frag.isExternal() || frag.isArchive()) {
					continue;
				}

				String name = frag.getElementName();
				if (path.startsWith(name)) {
					// strip the path separator after the name
					path = path.substring(name.length() + 1);
					continue;
				}
			}
		} catch (CoreException e) {
			DLTKDebugPlugin.log(e);
		}

		return path;
	}
}
