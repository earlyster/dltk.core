/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.debug.core.model;

import java.net.URI;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.model.ILineBreakpoint;

public interface IScriptLineBreakpoint extends IScriptBreakpoint,
		ILineBreakpoint {

	/**
	 * Returns the {@link IResource} if this breakpoint is attached to the
	 * resource in the workspace or <code>null</code> if breakpoint is attached
	 * to the file outside of the workspace.
	 * 
	 * @return
	 */
	IResource getResource();

	IPath getResourcePath();

	URI getResourceURI();
}
