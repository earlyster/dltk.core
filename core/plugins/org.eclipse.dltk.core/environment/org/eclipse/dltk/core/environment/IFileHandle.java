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
import java.io.OutputStream;
import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class hold handle to file or directory, most methods semantic are
 * similar to File class.
 */
public interface IFileHandle {

	/**
	 * Return associated environment.
	 * 
	 * @return associated environment
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
	 * Return OS specific path representation of this handle.
	 * 
	 * @return
	 */
	String toOSString();

	/**
	 * Return canonical path.
	 */
	String getCanonicalPath();

	/**
	 * Return full path associated with this handle. Full path contains
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
	 * @return children or <code>null</code> on error
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
	 * Tests file or directory existence.
	 * 
	 * @return <code>true</code> if file or directory exists.
	 */
	boolean exists();

	/**
	 * Returns input stream for this file, or throws exception if file does not
	 * exist.
	 * 
	 * @param monitor
	 *            - progress monitor
	 * @return input stream
	 * @throws IOException
	 */
	InputStream openInputStream(IProgressMonitor monitor) throws IOException;

	/**
	 * Returns an open output stream on the contents of this file. The caller is
	 * responsible for closing the provided stream when it is no longer needed.
	 * This file need not exist in the underlying file system at the time this
	 * method is called.
	 * 
	 * @param monitor
	 * @return
	 * @throws IOException
	 */
	OutputStream openOutputStream(IProgressMonitor monitor) throws IOException;

	/**
	 * Return true if this file is symbolic link
	 * 
	 * @return
	 */
	boolean isSymlink();

	/**
	 * Return <code>true</code> if file is a directory
	 */
	boolean isDirectory();

	/**
	 * Return <code>true</code> if this file is a regular file
	 */
	boolean isFile();

	/**
	 * Return last modified time
	 * 
	 * @return last modified time
	 */
	long lastModified();

	/**
	 * Return file length
	 * 
	 * @return
	 */
	long length();
}
