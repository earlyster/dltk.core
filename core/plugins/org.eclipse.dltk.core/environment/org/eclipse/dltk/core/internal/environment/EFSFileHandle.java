package org.eclipse.dltk.core.internal.environment;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;

public class EFSFileHandle implements IFileHandle {
	private IFileStore file;
	private IEnvironment environment;

	public EFSFileHandle(IEnvironment env, IFileStore file) {
		this.environment = env;
		this.file = file;
	}

	public boolean exists() {
		try {
			return file.fetchInfo().exists();
		} catch (RuntimeException e) {
			return false;
		}
	}

	public String getAbsolutePath() {
		return getPath().toString();
	}

	public String getCanonicalPath() {
		return getAbsolutePath();
	}

	public IFileHandle getChild(final String childname) {
		return new EFSFileHandle(environment, file
				.getChild(new Path(childname)));
	}

	public IFileHandle[] getChildren() {
		try {
			IFileStore[] files = file.childStores(EFS.NONE, null);
			IFileHandle[] children = new IFileHandle[files.length];
			for (int i = 0; i < files.length; i++)
				children[i] = new EFSFileHandle(environment, files[i]);
			return children;
		} catch (CoreException e) {
			if (DLTKCore.DEBUG)
				e.printStackTrace();
			return null;
		}
	}

	public IEnvironment getEnvironment() {
		return environment;
	}

	public String getId() {
		return file.toURI().toString();
	}

	public String getName() {
		return file.getName();
	}

	public IFileHandle getParent() {
		IFileStore parent = file.getParent();
		if (parent == null)
			return null;
		return new EFSFileHandle(environment, parent);
	}

	public IPath getPath() {
		return new Path(file.toURI().getPath());
	}

	public boolean isDirectory() {
		return file.fetchInfo().isDirectory();
	}

	public boolean isFile() {
		return !isDirectory();
	}

	public boolean isHidden() {
		return file.fetchInfo().getAttribute(EFS.ATTRIBUTE_HIDDEN);
	}

	public boolean isSymlink() {
		return file.fetchInfo().getAttribute(EFS.ATTRIBUTE_SYMLINK);
	}

	public InputStream openInputStream() throws IOException {
		try {
			return file.openInputStream(EFS.NONE, null);
		} catch (CoreException e) {
			if (DLTKCore.DEBUG)
				e.printStackTrace();
			throw new IOException(e.getLocalizedMessage());
		}
	}

	public boolean equals(Object obj) {
		if (obj instanceof EFSFileHandle) {
			EFSFileHandle anotherFile = (EFSFileHandle) obj;
			return this.file.fetchInfo().equals(anotherFile.file.fetchInfo());
		}
		return false;
	}

	public int hashCode() {
		return file.hashCode();
	}

	public String toString() {
		return environment.convertPathToString(getPath());
	}

	public long lastModified() {
		return file.fetchInfo().getLastModified();
	}

	public long length() {
		return file.fetchInfo().getLength();
	}

	public IPath getFullPath() {
		return EnvironmentPathUtils.getFullPath(environment, getPath());
	}
}
