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

/**
 * This class hold handle to file or directory, most methods semantic are
 * similar to File class.
 */
public interface IFileHandle {
	/**
	 * @deprecated
	 */
	String ID_SEPARATOR = "#"; //$NON-NLS-1$

	/**
	 * Return associated environment.
	 * 
	 * @return associated environemnt
	 */
	IEnvironment getEnvironment();

	/**
	 * Return associated environment identifier
	 * 
	 * @return environment identifier
	 */
	String getEnvironmentId();

	IPath getPath();

	/**
	 * Return OS specific path representation of this handle. If system are
	 * unix, path will be in unix format.
	 * 
	 * @return
	 */
	String toOSString();

	/**
	 * Return canonical path.
	 */
	String getCanonicalPath();

	/**
	 * Return full path associated with this handle. Full path contain
	 * environment identifier.
	 * 
	 * @see EnvironmentPathUtils for more information about full path to path
	 *      conversions.
	 * 
	 * @return full path.
	 */
	IPath getFullPath();

	/**
	 * Return file name.
	 * 
	 * @return file name.
	 */
	String getName();

	/**
	 * Convert file to URI.
	 * 
	 * @return uri for file.
	 */
	URI toURI();

	/**
	 * Return this file parent file handle.
	 * 
	 * @return parent file handle
	 */
	IFileHandle getParent();

	/**
	 * Return children of this directory.
	 * 
	 * @return children
	 */
	IFileHandle[] getChildren();

	/**
	 * Return specific child by name
	 * 
	 * @param path
	 *            - path to file
	 * @return child file handle
	 */
	IFileHandle getChild(String path);

	/**
	 * Return if file or directory are exists.
	 * 
	 * @return existance
	 */
	boolean exists();

	/**
	 * Return input stream for this file, or throws exception if file is not
	 * exits.
	 * 
	 * @param monitor
	 *            - progress monitor
	 * @return input stream
	 * @throws IOException
	 */
	InputStream openInputStream(IProgressMonitor monitor) throws IOException;

	/**
	 * Return true if this file is symbolic link
	 * 
	 * @return
	 */
	boolean isSymlink();

	/**
	 * Return true if file is a directory
	 */
	boolean isDirectory();

	/**
	 * Return true if this file is a regular file
	 */
	boolean isFile();

	/**
	 * Return last modified time
	 * 
	 * @return last modifed time
	 */
	long lastModified();

	/**
	 * Return file length
	 * 
	 * @return
	 */
	long length();
}
