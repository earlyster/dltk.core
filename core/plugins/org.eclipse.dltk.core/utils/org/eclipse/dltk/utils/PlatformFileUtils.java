package org.eclipse.dltk.utils;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.osgi.service.datalocation.Location;

public class PlatformFileUtils {
	public static IFileHandle findAbsoluteOrEclipseRelativeFile(
			IEnvironment env, IPath path) {
		if (path == null || path.isEmpty()) {
			throw new IllegalArgumentException(
					Messages.PlatformFileUtils_pathMustNotBeEmpty);
		}
		IFileHandle file = env.getFile(path);
		if (env.isLocal() && !file.exists() && !path.isAbsolute()) {
			Location location = Platform.getInstallLocation();
			if (location != null) {
				try {
					String loc = FileLocator.resolve(location.getURL())
							.getPath();
					IFileHandle nfile = env.getFile(new Path(loc).append(path));
					if (nfile.exists()) {
						return nfile;
					}
				} catch (IOException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}

			location = Platform.getInstanceLocation();
			if (location != null) {
				try {
					String loc = FileLocator.resolve(location.getURL())
							.getPath();
					IFileHandle nfile = env.getFile(new Path(loc).append(path));
					if (nfile.exists()) {
						return nfile;
					}
				} catch (IOException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}

		}
		return file;
	}

	public static File findAbsoluteOrEclipseRelativeFile(File file) {
		return file;
	}
}
