package org.eclipse.dltk.core.internal.environment;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;

public class LocalEnvironment extends PlatformObject implements IEnvironment {
	public static final String ENVIRONMENT_ID = DLTKCore.PLUGIN_ID
			+ ".environment.localEnvironment";

	private static IEnvironment instance = new LocalEnvironment();
	private IFileSystem fs;

	public LocalEnvironment() {
		this.fs = EFS.getLocalFileSystem();
	}

	public IFileHandle getFile(IPath path) {
		return new EFSFileHandle(this, fs.getStore(path));
	}

	public String getId() {
		return ENVIRONMENT_ID;
	}

	public static IEnvironment getInstance() {
		return instance;
	}

	public String getSeparator() {
		return File.separator;
	}

	public char getSeparatorChar() {
		return File.separatorChar;
	}

	public String getName() {
		return "Localhost";
	}

	public boolean hasProject(IProject project) {
		if (project.isAccessible()) {
			IPath location = project.getLocation();
			if (location != null) {
				File file = new File(location.makeAbsolute().toOSString());
				if (file.exists()) {
					return true;
				}
			}
		}
		return false;
	}
}
