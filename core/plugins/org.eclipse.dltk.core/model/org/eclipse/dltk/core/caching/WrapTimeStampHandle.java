package org.eclipse.dltk.core.caching;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;

public class WrapTimeStampHandle implements IFileHandle {
	IFileHandle handle;
	long timestamp;

	public WrapTimeStampHandle(IFileHandle handle, long timestamp) {
		this.handle = handle;
		this.timestamp = timestamp;
	}

	public boolean exists() {
		return this.handle.exists();
	}

	public String getCanonicalPath() {
		return this.handle.getCanonicalPath();
	}

	public IFileHandle getChild(String path) {
		return this.handle.getChild(path);
	}

	public IFileHandle[] getChildren() {
		return this.handle.getChildren();
	}

	public IEnvironment getEnvironment() {
		return this.handle.getEnvironment();
	}

	public String getEnvironmentId() {
		return this.handle.getEnvironmentId();
	}

	public IPath getFullPath() {
		return this.handle.getFullPath();
	}

	public String getName() {
		return this.handle.getName();
	}

	public IFileHandle getParent() {
		return this.handle.getParent();
	}

	public IPath getPath() {
		return this.handle.getPath();
	}

	public boolean isDirectory() {
		return this.handle.isDirectory();
	}

	public boolean isFile() {
		return this.handle.isFile();
	}

	public boolean isSymlink() {
		return this.handle.isSymlink();
	}

	public long lastModified() {
		return this.timestamp;
	}

	public long length() {
		return this.handle.length();
	}

	public InputStream openInputStream(IProgressMonitor monitor)
			throws IOException {
		return this.handle.openInputStream(monitor);
	}

	public OutputStream openOutputStream(IProgressMonitor monitor)
			throws IOException {
		return this.handle.openOutputStream(monitor);
	}

	public String toOSString() {
		return this.handle.toOSString();
	}

	public URI toURI() {
		return this.handle.toURI();
	}

	public void move(IFileHandle destination) throws CoreException {
		this.handle.move(destination);
	}
}
