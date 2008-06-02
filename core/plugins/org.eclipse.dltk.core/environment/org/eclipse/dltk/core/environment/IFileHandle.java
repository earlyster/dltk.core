/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core.environment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IFileHandle {
	String ID_SEPARATOR = "#"; //$NON-NLS-1$

	IEnvironment getEnvironment();

	String getEnvironmentId();

	IPath getPath();

	String toOSString();

	String getCanonicalPath();

	IPath getFullPath();

	String getName();

	URI toURI();

	IFileHandle getParent();

	IFileHandle[] getChildren();

	IFileHandle getChild(String bundlePath);

	boolean exists();

	InputStream openInputStream(IProgressMonitor monitor) throws IOException;

	boolean isSymlink();

	boolean isDirectory();

	boolean isFile();

	long lastModified();

	long length();

}
