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

import java.io.File;
import java.net.URI;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

public interface IEnvironment extends IAdaptable {
	/**
	 * Tests if this environment is local
	 * 
	 * @return
	 */
	boolean isLocal();

	/**
	 * Returns {@link IFileHandle} for the specified local path. The path should
	 * be local to this environment. If you have full path (with the environment
	 * id) - you should use {@link EnvironmentPathUtils}
	 * 
	 * @param path
	 *            environment-local path
	 * @return {@link IFileHandle} for the specified local path, not
	 *         <code>null</code>.
	 */
	IFileHandle getFile(IPath path);

	/**
	 * Return environment identifier
	 * 
	 * @return
	 */
	String getId();

	/**
	 * Return environment specific name separator as string
	 * 
	 * @return name separator string
	 */
	String getSeparator();

	/**
	 * Return environment specific name separator as char
	 * 
	 * @return name separator char
	 */
	char getSeparatorChar();

	/**
	 * Return environment specific path's separator
	 * 
	 * @return path's separator
	 */
	String getPathsSeparator();

	/**
	 * Return environment specific path's separator
	 * 
	 * @return
	 */
	char getPathsSeparatorChar();

	/**
	 * Return environment name
	 * 
	 * @return environment name
	 */
	String getName();

	/**
	 * Convert any path to environment correct path string Equivalent to call
	 * IPath.toOSString() for specific environment
	 * 
	 * @param path
	 * @return environment path
	 */
	String convertPathToString(IPath path);

	/**
	 * Return URI for specific path location For local environment will contain
	 * file: schema.
	 * 
	 * @param location
	 * @return URI for location
	 */
	URI getURI(IPath location);

	/**
	 * Return file handle from URI or <code>null</code>.
	 * 
	 * @param locationURI
	 * @return
	 */
	IFileHandle getFile(URI locationURI);

	/**
	 * Return canonical file path.
	 * 
	 * @see File#getCanonicalPath()
	 * @param path
	 * @return canonical file path
	 */
	String getCanonicalPath(IPath path);

	/**
	 * For remote environments enquires if connection is available. If
	 * connection is not available then indexing/building and some other stuff
	 * for projects with this environment will not be performed.
	 * 
	 * @since 2.0
	 */
	boolean isConnected();

	/**
	 * Ensure connection is available. Ask user for first time.
	 * 
	 * @return
	 * @since 2.0
	 */
	boolean connect();
}
