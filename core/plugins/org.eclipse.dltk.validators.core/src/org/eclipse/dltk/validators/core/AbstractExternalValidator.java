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
package org.eclipse.dltk.validators.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.validators.internal.core.ValidatorsCore;

/**
 * @deprecated use ResourceValidatorWorker and/or SourceModuleValidatorWorker
 */
public abstract class AbstractExternalValidator {

	protected abstract String getMarkerType();

	public void clean(ISourceModule[] modules) {
		clean(toResources(modules));
	}

	public void clean(IResource[] resources) {
		final String markerType = getMarkerType();
		// TODO execute single operation via IWorkspaceRunnable ?
		for (int i = 0; i < resources.length; ++i) {
			final IResource resource = resources[i];
			clean(resource, markerType);
		}
	}

	protected void clean(final IResource resource, final String markerType) {
		try {
			resource.deleteMarkers(markerType, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			ValidatorsCore.log(e.getStatus());
		}
	}

	protected void clean(final IResource resource) {
		clean(resource, getMarkerType());
	}

	protected IResource[] toResources(ISourceModule[] modules) {
		final List resources = new ArrayList(modules.length);
		for (int i = 0; i < modules.length; ++i) {
			final IResource resource = modules[i].getResource();
			if (resource != null) {
				resources.add(resource);
			}
		}
		final IResource[] result = new IResource[resources.size()];
		resources.toArray(result);
		return result;
	}

	protected IMarker createMarker(IResource res, int line, int start, int end,
			String msg, int severity, int priority, Map attributes)
			throws CoreException {
		final IMarker m = res.createMarker(getMarkerType());
		m.setAttribute(IMarker.LINE_NUMBER, line);
		m.setAttribute(IMarker.MESSAGE, msg);
		m.setAttribute(IMarker.SEVERITY, severity);
		m.setAttribute(IMarker.PRIORITY, priority);
		m.setAttribute(IMarker.CHAR_START, start);
		m.setAttribute(IMarker.CHAR_END, end);
		if (attributes != null && !attributes.isEmpty()) {
			for (Iterator i = attributes.entrySet().iterator(); i.hasNext();) {
				final Map.Entry entry = (Map.Entry) i.next();
				m.setAttribute((String) entry.getKey(), entry.getValue());
			}
		}
		return m;
	}

	protected IMarker reportWarning(IResource res, int line, int start,
			int end, String msg) throws CoreException {
		return reportWarning(res, line, start, end, msg, null);
	}

	protected IMarker reportWarning(IResource res, int line, int start,
			int end, String msg, Map attributes) throws CoreException {
		return createMarker(res, line, start, end, msg,
				IMarker.SEVERITY_WARNING, IMarker.PRIORITY_NORMAL, attributes);
	}

	protected IMarker reportError(IResource res, int line, int start, int end,
			String msg) throws CoreException {
		return reportError(res, line, start, end, msg, null);
	}

	protected IMarker reportError(IResource res, int line, int start, int end,
			String msg, Map attributes) throws CoreException {
		return createMarker(res, line, start, end, msg, IMarker.SEVERITY_ERROR,
				IMarker.PRIORITY_NORMAL, attributes);
	}
}
