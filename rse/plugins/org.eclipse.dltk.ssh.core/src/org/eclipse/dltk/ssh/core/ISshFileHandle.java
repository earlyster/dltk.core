package org.eclipse.dltk.ssh.core;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

public interface ISshFileHandle {

	public ISshFileHandle createFolder(String newEntryName,
			IProgressMonitor monitor) throws CoreException;

	public void mkdir();

	public void delete() throws CoreException;

	public boolean exists();

	public ISshFileHandle[] getChildren(IProgressMonitor monitor)
			throws CoreException;

	public ISshFileHandle getChild(String newEntryName);

	public InputStream getInputStream(IProgressMonitor monitor)
			throws CoreException;

	public String getName();

	public IPath getPath();

	public OutputStream getOutputStream(IProgressMonitor monitor)
			throws CoreException;

	public boolean isDirectory();

	public long lastModificationTime();

	public void setLastModified(long timestamp, IProgressMonitor monitor)
			throws CoreException;

	public long getSize();

	public boolean isSymlink();

}