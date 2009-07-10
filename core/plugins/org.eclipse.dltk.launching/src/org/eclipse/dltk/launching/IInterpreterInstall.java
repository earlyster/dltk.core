/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.launching;

import java.util.List;

import org.eclipse.dltk.core.IBuiltinModuleProvider;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

public interface IInterpreterInstall extends IBuiltinModuleProvider {
	// Runner
	IInterpreterRunner getInterpreterRunner(String mode);

	String getNatureId();

	// Id
	String getId();

	// Name
	String getName();

	void setName(String name);

	// Also search for Platform location relative locations.
	IFileHandle getInstallLocation();

	//
	IFileHandle getRawInstallLocation();

	void setInstallLocation(IFileHandle installLocation);

	IEnvironment getEnvironment();

	String getEnvironmentId();

	IExecutionEnvironment getExecEnvironment();

	// Type
	IInterpreterInstallType getInterpreterInstallType();

	// Library locations
	LibraryLocation[] getLibraryLocations();

	EnvironmentVariable[] getEnvironmentVariables();

	void setEnvironmentVariables(EnvironmentVariable[] variables);

	void setLibraryLocations(LibraryLocation[] locations);

	// Arguments
	public String[] getInterpreterArguments();

	void setInterpreterArguments(String[] args);

	// Arguments
	String getInterpreterArgs();

	void setInterpreterArgs(String args);

	/**
	 * Returns additional information objects belonging to this interpreter.
	 * 
	 * @return
	 * @since 2.0
	 */
	List<EObject> getExtensions();

	/**
	 * Returns deep copy of additional information objects belonging to this
	 * interpreter.
	 * 
	 * @return
	 * @since 2.0
	 */
	List<EObject> copyExtensions();

	/**
	 * Replaces additional information objects belonging to this interpreter
	 * with the specified ones.
	 * 
	 * @param value
	 * @since 2.0
	 */
	void setExtensions(List<EObject> value);

	/**
	 * Finds the first additional information object of the specified
	 * {@link EClass}.
	 * 
	 * @param clazz
	 * @return the object found or <code>null</code>
	 * @since 2.0
	 */
	EObject findExtension(EClass clazz);

	/**
	 * Replaces the first additional information object of the specified type
	 * with specified one.
	 * 
	 * @param clazz
	 * @param value
	 *            new value or <code>null</code>
	 * @return previous value or <code>null</code>
	 * @since 2.0
	 */
	EObject replaceExtension(EClass clazz, EObject value);
}
