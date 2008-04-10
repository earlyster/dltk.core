package org.eclipse.dltk.core.environment;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;

public class EnvironmentPathUtils {

	private static char SEPARATOR = '/';

	public static IPath getFullPath(IEnvironment env, IPath path) {
		if (isFull(path)) {
			throw new RuntimeException("Invalid path");
		}
//		if( path.segment(0).startsWith("#special#")) {
//			return path;
//		}
		String device = path.getDevice();
		if (device == null)
			device = Character.toString(IPath.DEVICE_SEPARATOR);
		
		return path.setDevice(env.getId() + SEPARATOR + device);
	}

	public static boolean isFull(IPath path) {
		String device = path.getDevice();
		return device != null && device.indexOf(SEPARATOR) >= 0;		
	}

	public static IEnvironment getPathEnvironment(IPath path) {
		if (!isFull(path)) {
			return null;
		}
		
		String envId = path.getDevice();
		if (envId == null)
			return null;
		
		int index = envId.indexOf(SEPARATOR);
		envId = envId.substring(0, index);
		return EnvironmentManager.getEnvironmentById(envId);
	}

	public static IPath getLocalPath(IPath path) {
//		if( path.segment(0).startsWith("#special#")) {
//			return path;
//		}
		if (!isFull(path)) {
			return path;
//			throw new RuntimeException("Invalid path");
		}

		String device = path.getDevice();
		int index = device.indexOf(SEPARATOR);
		Assert.isTrue(index >= 0);		
		device = device.substring(index + 1);
		if (device.length() == 1 && device.charAt(0) == IPath.DEVICE_SEPARATOR)
			device = null;
		
		return path.setDevice(device);
	}

	public static String getLocalPathString(IPath path) {
		IEnvironment env = getPathEnvironment(path);
		IPath localPath = getLocalPath(path);
		return env.convertPathToString(localPath);
	}
	
	public static IFileHandle getFile(IPath fullPath) {
		IEnvironment env = getPathEnvironment(fullPath);
		if (env == null)
			return null;
		
		IPath path = getLocalPath(fullPath);
		return env.getFile(path);
	}
}
