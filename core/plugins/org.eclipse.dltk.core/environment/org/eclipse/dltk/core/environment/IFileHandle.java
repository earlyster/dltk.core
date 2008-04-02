package org.eclipse.dltk.core.environment;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;

public interface IFileHandle {
	String ID_SEPARATOR = "#";

	IEnvironment getEnvironment();

	IPath getPath();

	String getName();

	IFileHandle getParent();

	IFileHandle[] getChildren();

	boolean isDirectory();

	boolean exists();

	InputStream openInputStream() throws IOException;

	String getId();

	boolean isHidden();

	IFileHandle getChild(String bundlePath);

	boolean isSymlink();

	boolean isFile();

	String getAbsolutePath();

	String getCanonicalPath();

	long lastModified();

	long length();
}
