/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.internal.environment;

import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;

/**
 * @since 2.0
 */
public abstract class LazyEnvironment implements IEnvironment {

	private static final boolean DEBUG = false;

	private final String environmentId;
	private IEnvironment environment;

	public LazyEnvironment(String environmentId) {
		this.environmentId = environmentId;
		if (DEBUG)
			System.out.println(getClass().getName() + " - created for " //$NON-NLS-1$
					+ environmentId);
	}

	private void initialize() {
		if (environment == null) {
			environment = resolveEnvironment(environmentId);
			if (DEBUG)
				if (environment != null)
					System.out
							.println(getClass().getName()
									+ " - resolved " + environmentId + " to " + environment.getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	protected abstract IEnvironment resolveEnvironment(String envId);

	public boolean connect() {
		initialize();
		return environment != null && environment.connect();
	}

	public String convertPathToString(IPath path) {
		initialize();
		return environment != null ? environment.convertPathToString(path)
				: path.toString();
	}

	public String getCanonicalPath(IPath path) {
		initialize();
		return environment != null ? environment.getCanonicalPath(path) : path
				.toString();
	}

	public IFileHandle getFile(IPath path) {
		return new LazyFileHandle(environmentId, path);
	}

	public IFileHandle getFile(URI locationURI) {
		initialize();
		return environment != null ? environment.getFile(locationURI) : null;
	}

	public String getId() {
		return environmentId;
	}

	public String getName() {
		initialize();
		return environment != null ? environment.getName() : generateName();
	}

	private String generateName() {
		return getClass().getSimpleName() + '[' + environmentId + ']';
	}

	public String getPathsSeparator() {
		initialize();
		return environment != null ? environment.getPathsSeparator() : ":"; //$NON-NLS-1$
	}

	public char getPathsSeparatorChar() {
		initialize();
		return environment != null ? environment.getPathsSeparatorChar() : ':';
	}

	public String getSeparator() {
		initialize();
		return environment != null ? environment.getSeparator() : "/"; //$NON-NLS-1$
	}

	public char getSeparatorChar() {
		initialize();
		return environment != null ? environment.getSeparatorChar() : '/';
	}

	public URI getURI(IPath location) {
		initialize();
		return environment != null ? environment.getURI(location) : null;
	}

	public boolean isConnected() {
		initialize();
		return environment != null ? environment.isConnected() : false;
	}

	public boolean isLocal() {
		initialize();
		return environment != null ? environment.isLocal() : false;
	}

	public Object getAdapter(@SuppressWarnings("unchecked") Class adapter) {
		initialize();
		return environment != null ? environment.getAdapter(adapter) : null;
	}

	@Override
	public int hashCode() {
		return environmentId.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IEnvironment) {
			IEnvironment other = (IEnvironment) obj;
			return environmentId.equals(other.getId());
		}
		return false;
	}

}
