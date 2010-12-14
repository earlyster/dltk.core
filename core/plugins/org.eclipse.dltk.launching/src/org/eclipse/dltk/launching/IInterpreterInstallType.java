/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.launching;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.environment.IFileHandle;

/**
 * Represents a particular type of interpreter for which there may be any number
 * of interpreter installations.
 * <p>
 * This interface is intended to be implemented by clients that contribute to
 * the <code>"org.eclipse.dltk.launching.interpreterType"</code> extension
 * point.
 * </p>
 * 
 * @see IInterpreterInstall
 */
public interface IInterpreterInstallType {
	/**
	 * Creates a new instance of this interpreter Install type. The newly
	 * created IinterpreterInstall is managed by this IinterpreterInstallType.
	 * 
	 * @param id
	 *            An id String that must be unique within this
	 *            IinterpreterInstallType.
	 * 
	 * @return the newly created interpreter instance
	 * 
	 * @throws IllegalArgumentException
	 *             If the id exists already.
	 */
	IInterpreterInstall createInterpreterInstall(String id);

	/**
	 * Finds the interpreter with the given id.
	 * 
	 * @param id
	 *            the interpreter id
	 * @return a interpreter instance, or <code>null</code> if not found
	 */
	IInterpreterInstall findInterpreterInstall(String id);

	/**
	 * Finds the interpreter with the given name.
	 * 
	 * @param name
	 *            the interpreter name
	 * @return a interpreter instance, or <code>null</code> if not found
	 * 
	 */
	IInterpreterInstall findInterpreterInstallByName(String name);

	/**
	 * Remove the interpreter associated with the given id from the set of
	 * interpreters managed by this interpreter type. Has no effect if a
	 * interpreter with the given id is not currently managed by this type. A
	 * interpreter install that is disposed may not be used anymore.
	 * 
	 * @param id
	 *            the id of the interpreter to be disposed.
	 */
	void disposeInterpreterInstall(String id);

	/**
	 * Returns all interpreter instances managed by this interpreter type.
	 * 
	 * @return the list of interpreter instances managed by this interpreter
	 *         type
	 */
	IInterpreterInstall[] getInterpreterInstalls();

	/**
	 * Returns the display name of this interpreter type.
	 * 
	 * @return the name of this IInterpreterInstallType
	 */
	String getName();

	/**
	 * Returns the globally unique id of this interpreter type. Clients are
	 * responsible for providing a unique id.
	 * 
	 * @return the id of this IInterpreterInstallType
	 */
	String getId();

	/**
	 * Validates the given interpreter installation. Execute interpreter to
	 * ensure what interpreter are correct.
	 * 
	 * @param file
	 *            potential installation location for this type of interpreter
	 * @param variables
	 *            current set of specified user environment variables, they
	 *            overwrite environment variables with same names
	 * @param libraryLocations
	 *            current set of library locations, they overwrite environment
	 *            library locations
	 * @param monitor
	 *            progress monitor
	 * @since 2.0
	 * @return
	 */
	IStatus validateInstallLocation(IFileHandle file,
			EnvironmentVariable[] variables,
			LibraryLocation[] libraryLocations, IProgressMonitor monitor);

	/**
	 * Tries to detect an installed interpreter that matches this interpreter
	 * install type. Implementers should return <code>null</code> if they can't
	 * assure that a given interpreter install matches this
	 * IInterpreterInstallType.
	 * 
	 * @return The location of an interpreter installation that can be used with
	 *         this interpreter install type, or <code>null</code> if unable to
	 *         locate an installed interpreter.
	 * @since 3.0
	 */
	IFileHandle[] detectInstallLocations();

	/**
	 * Make the name of a detected interpreter stand out
	 * 
	 * @since 3.0
	 */
	String generateDetectedInterpreterName(IFileHandle install);

	/**
	 * Used to search interpreters.
	 * 
	 * @param installLocation
	 * @return
	 */
	IStatus validatePossiblyName(IFileHandle installLocation);

	/**
	 * Returns a collection of <code>LibraryLocation</code>s that represent the
	 * default system libraries of this interpreter install type, if a
	 * interpreter was installed at the given <code>installLocation</code>. The
	 * returned <code>LibraryLocation</code>s may not exist if the
	 * <code>installLocation</code> is not a valid install location.
	 * 
	 * @param installLocation
	 *            home location
	 * @see LibraryLocation
	 * @see IInterpreterInstallType#validateInstallLocation(File)
	 * 
	 * @return default library locations based on the given
	 *         <code>installLocation</code>.
	 * 
	 */

	LibraryLocation[] getDefaultLibraryLocations(IFileHandle installLocation);

	LibraryLocation[] getDefaultLibraryLocations(IFileHandle installLocation,
			EnvironmentVariable[] variables);

	LibraryLocation[] getDefaultLibraryLocations(IFileHandle fileHandle,
			EnvironmentVariable[] variables, IProgressMonitor monitor);

	/**
	 * Return string id of supported language.
	 * 
	 * @returns id of supported language.
	 */
	String getNatureId();
}
