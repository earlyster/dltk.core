package org.eclipse.dltk.utils;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.EnvironmentsManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.osgi.service.datalocation.Location;

public class PlatformFileUtils {
	/**
	 * Returns same file if not exist.
	 * @deprecated
	 */
	public static File findAbsoluteOrEclipseRelativeFile(File file) {
		if( file.getPath().length() == 0 ) {
			return file;
		}
		String locationName = file.getPath();
		if (!file.exists() && !file.isAbsolute()) {
			String loc;
			Location location = Platform.getInstallLocation();
			if (location != null) {
				try {
					loc = FileLocator.resolve(location.getURL()).getPath();
					// System.out.println("relavie to:" + loc);
					File nfile = new File(loc + File.separator + locationName);
					// System.out.println("relavie to:" + nfile.toString());
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
					loc = FileLocator.resolve(location.getURL()).getPath();
					File nfile = new File(loc + File.separator + locationName);
					// System.out.println("relavie to:" + nfile.toString());
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
	
	public static IFileHandle findAbsoluteOrEclipseRelativeFile(
			IEnvironment env, IPath path) {
		IFileHandle file = env.getFile(path);
		if (EnvironmentsManager.isLocal(env) && !file.exists()
				&& !path.isAbsolute()) {
			String loc;
			Location location = Platform.getInstallLocation();
			if (location != null) {
				try {
					loc = FileLocator.resolve(location.getURL()).getPath();
					IFileHandle nfile = env.getFile(new Path(loc
							+ env.getSeparator() + path.toOSString()));
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
					loc = FileLocator.resolve(location.getURL()).getPath();
					IFileHandle nfile = env.getFile(new Path(loc
							+ env.getSeparator() + path.toOSString()));
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
}
