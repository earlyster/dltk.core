package org.eclipse.dltk.core.environment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.runtime.IPath;

public interface IFileHandle {
	String ID_SEPARATOR = "#"; //$NON-NLS-1$

	IEnvironment getEnvironment();

	IPath getPath();

	String toOSString();

	String getCanonicalPath();

	IPath getFullPath();

	String getName();

	URI toURI();

	IFileHandle getParent();

	IFileHandle[] getChildren();

	IFileHandle getChild(String bundlePath);

	boolean exists();

	InputStream openInputStream() throws IOException;

	boolean isSymlink();

	boolean isDirectory();

	boolean isFile();

	long lastModified();

	long length();
}
