/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.eclipse.dltk.core.internal.environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.utils.PlatformFileUtils;

/**
 * @since 2.0
 */
public class LazyFileHandle implements IFileHandle {
	private final String environment;
	private final IPath path;
	private IFileHandle handle = null;

	public LazyFileHandle(String environment, IPath path) {
		this.environment = Util.EMPTY_STRING.equals(environment) ? LocalEnvironment.ENVIRONMENT_ID
				: environment;
		this.path = path;
	}

	private void initialize() {
		if (handle == null) {
			IEnvironment environment = resolveEnvironment();
			if (environment != null) {
				handle = PlatformFileUtils.findAbsoluteOrEclipseRelativeFile(
						environment, this.path);
			}
		}
	}

	private IEnvironment resolveEnvironment() {
		final IEnvironment environment = EnvironmentManager
				.getEnvironmentById(this.environment);
		if (environment instanceof LazyEnvironment) {
			return null;
		}
		return environment;
	}

	/**
	 * Support containers on load are always exists.
	 */
	public boolean exists() {
		if (handle != null) {
			return this.handle.exists();
		}
		IEnvironment environment = resolveEnvironment();
		if (environment == null || !environment.connect()) {
			// Assume location exists if environment is not connected
			return true;
		}
		initialize();
		if (handle != null) {
			return this.handle.exists();
		}
		return true;
	}

	public String getCanonicalPath() {
		initialize();
		if (handle != null) {
			return this.handle.getCanonicalPath();
		}
		return null;
	}

	public IFileHandle getChild(String bundlePath) {
		initialize();
		if (handle != null) {
			return this.handle.getChild(bundlePath);
		}
		return null;
	}

	public IFileHandle[] getChildren() {
		initialize();
		if (handle != null) {
			return this.handle.getChildren();
		}
		return null;
	}

	public IEnvironment getEnvironment() {
		initialize();
		if (handle != null) {
			return this.handle.getEnvironment();
		}
		return EnvironmentManager.getEnvironmentById(environment);
	}

	public IPath getFullPath() {
		// it is always possible to reconstruct full path back.
		return EnvironmentPathUtils.getFullPath(environment, path);
	}

	public String getName() {
		initialize();
		if (handle != null) {
			return this.handle.getName();
		}
		return path.lastSegment();
	}

	public IFileHandle getParent() {
		initialize();
		if (handle != null) {
			return this.handle.getParent();
		}
		if (path.segmentCount() > 0) {
			return new LazyFileHandle(environment, path.removeLastSegments(1));
		}
		return null;
	}

	public IPath getPath() {
		return this.path;
	}

	public boolean isDirectory() {
		initialize();
		if (handle != null) {
			return this.handle.isDirectory();
		}
		return false;
	}

	public boolean isFile() {
		initialize();
		if (handle != null) {
			return this.handle.isFile();
		}
		return false;
	}

	public boolean isSymlink() {
		initialize();
		if (handle != null) {
			return this.handle.isSymlink();
		}
		return false;
	}

	public long lastModified() {
		initialize();
		if (handle != null) {
			return this.handle.lastModified();
		}
		return 0;
	}

	public long length() {
		initialize();
		if (handle != null) {
			return this.handle.length();
		}
		return 0;
	}

	public InputStream openInputStream(IProgressMonitor monitor)
			throws IOException {
		initialize();
		if (handle != null) {
			return this.handle.openInputStream(monitor);
		}
		return null;
	}

	public OutputStream openOutputStream(IProgressMonitor monitor)
			throws IOException {
		initialize();
		if (handle != null) {
			return this.handle.openOutputStream(monitor);
		}
		throw new IOException("Error opening " + getFullPath()); //$NON-NLS-1$
	}

	public String toOSString() {
		IEnvironment environment = resolveEnvironment();
		if (environment != null) {
			IPath newPath = PlatformFileUtils
					.findAbsoluteOrEclipseRelativeFile(environment, this.path)
					.getPath();
			return environment.convertPathToString(newPath);
		}
		initialize();
		if (this.handle != null) {
			return this.handle.toOSString();
		}
		return Util.EMPTY_STRING;
	}

	public URI toURI() {
		initialize();
		if (handle != null) {
			return this.handle.toURI();
		}
		return null;
	}

	public String getEnvironmentId() {
		return this.environment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((environment == null) ? 0 : environment.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof IFileHandle))
			return false;
		IFileHandle other = (IFileHandle) obj;
		if (environment == null) {
			if (other.getEnvironment() != null)
				return false;
		} else {
			final IEnvironment otherEnvironment = other.getEnvironment();
			if (otherEnvironment == null
					|| !environment.equals(otherEnvironment.getId()))
				return false;
		}
		if (path == null) {
			if (other.getPath() != null)
				return false;
		} else if (!path.equals(other.getPath()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		initialize();
		if (handle != null) {
			return this.handle.toString();
		}
		return "[UNRESOLVED]" + getFullPath(); //$NON-NLS-1$
	}

	public void move(IFileHandle destination) throws CoreException {
		initialize();
		if (handle != null) {
			handle.move(destination);
		} else {
			throw new CoreException(new Status(IStatus.ERROR,
					DLTKCore.PLUGIN_ID, "Error resolving " + getFullPath())); //$NON-NLS-1$
		}
	}
}
