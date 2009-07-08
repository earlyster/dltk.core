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
package org.eclipse.dltk.core.internal.environment;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IEnvironmentProvider;

public class LocalEnvironmentProvider implements IEnvironmentProvider {

	/**
	 * @since 2.0
	 */
	public static final String FILE_SCHEME = "file"; //$NON-NLS-1$

	public LocalEnvironmentProvider() {
	}

	public String getProviderName() {
		return LocalEnvironment.getInstance().getName();
	}

	public IEnvironment getEnvironment(String envId) {
		if (LocalEnvironment.ENVIRONMENT_ID.equals(envId)) {
			return LocalEnvironment.getInstance();
		}
		return null;
	}

	public IEnvironment[] getEnvironments() {
		return new IEnvironment[] { LocalEnvironment.getInstance() };
	}

	public boolean isInitialized() {
		return true;
	}

	public void waitInitialized() {
	}

	public IEnvironment getProjectEnvironment(IProject project) {
		if (project.isAccessible()) {
			IPath location = project.getLocation();
			if (location != null) {
				File file = new File(location.makeAbsolute().toOSString());
				if (file.exists()) {
					return LocalEnvironment.getInstance();
				}
			}
		}
		return null;
	}

	/*
	 * @see IEnvironmentProvider#getEnvironment(java.net.URI)
	 */
	public IEnvironment getEnvironment(URI locationURI) {
		if (FILE_SCHEME.equals(locationURI.getScheme())) {
			return LocalEnvironment.getInstance();
		} else {
			return null;
		}
	}
}
