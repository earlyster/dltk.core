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

import java.net.URI;

import org.eclipse.core.resources.IProject;

public interface IEnvironmentProvider {

	public String getProviderName();

	public IEnvironment[] getEnvironments();

	public IEnvironment getEnvironment(String envId);

	/**
	 * Tests if this provider is initialized
	 * 
	 * @return
	 */
	public boolean isInitialized();

	/**
	 * Waits until this provider is initialized
	 */
	public void waitInitialized();

	IEnvironment getProjectEnvironment(IProject project);

	/**
	 * Returns the environment this <code>locationURI</code> belongs to or
	 * <code>null</code> if matching environment was not found.
	 * 
	 * @param locationURI
	 * @return
	 */
	IEnvironment getEnvironment(URI locationURI);

}
