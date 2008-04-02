package org.eclipse.dltk.core.environment;

import java.io.File;
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

//	boolean mkdir();

//	boolean copy(IFileHandle destination);

//	boolean move(IFileHandle destination);

//	boolean delete();

	InputStream openInputStream() throws IOException;

//	OutputStream openOutputStream() throws IOException;

	String getId();

	boolean isHidden();

	IFileHandle getChild(String bundlePath);

	boolean isSymlink();

	boolean isFile();

	String getAbsolutePath();

	String getCanonicalPath();

	void setLastModified(long times);

	File toLocalFile() throws IOException;

	long lastModified();

	long length();
}
