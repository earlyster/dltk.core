/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.environment;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;

@SuppressWarnings("nls")
public class FileHandles {

	public static IFileStore asFileStore(IFileHandle handle)
			throws CoreException {
		if (handle instanceof IFileStoreProvider) {
			return ((IFileStoreProvider) handle).getFileStore();
		} else {
			throw new CoreException(new Status(IStatus.ERROR,
					DLTKCore.PLUGIN_ID, "Unsupported " + handle.getFullPath()));
		}
	}

	public static File asFile(IFileHandle handle) throws CoreException {
		if (handle instanceof FileAsFileHandle) {
			return ((FileAsFileHandle) handle).getFile();
		}
		if (LocalEnvironment.ENVIRONMENT_ID.equals(handle.getEnvironmentId())) {
			return handle.getPath().toFile();
		}
		final File result = asFileStore(handle).toLocalFile(EFS.NONE, null);
		if (result == null) {
			throw new CoreException(new Status(IStatus.ERROR,
					DLTKCore.PLUGIN_ID, "Unsupported " + handle.getFullPath()));
		}
		return result;
	}

	public static IPath asPath(IFileHandle handle, IEnvironment environment)
			throws CoreException {
		if (environment.getId().equals(handle.getEnvironmentId())) {
			return handle.getPath();
		}
		throw new CoreException(new Status(IStatus.ERROR, DLTKCore.PLUGIN_ID,
				"Couldn't resolve " + handle.getFullPath() + " for "
						+ environment.getName()));
	}

}
