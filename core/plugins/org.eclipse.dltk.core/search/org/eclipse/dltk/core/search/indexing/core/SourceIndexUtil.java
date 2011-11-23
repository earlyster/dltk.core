package org.eclipse.dltk.core.search.indexing.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.core.BuiltinSourceModule;
import org.eclipse.dltk.internal.core.ExternalSourceModule;
import org.eclipse.dltk.internal.core.SourceModule;

public class SourceIndexUtil {
	public static String containerRelativePath(IPath containerPath,
			ISourceModule module) {
		return containerRelativePath(containerPath, module, module.getPath());
	}

	public static String containerRelativePath(IPath containerPath,
			ISourceModule module, final IPath path) {
		IProjectFragment fragment = (IProjectFragment) module
				.getAncestor(IModelElement.PROJECT_FRAGMENT);
		if (fragment.isArchive()) {
			if (module instanceof IExternalSourceModule) {
				final IExternalSourceModule ext = (IExternalSourceModule) module;
				// archive related path
				return ext.getFullPath().toString();
			}
			if (module.isBinary()) {
				return path.removeFirstSegments(containerPath.segmentCount())
						.setDevice(null).toString();
			}
		}
		if (module instanceof ExternalSourceModule
				|| module instanceof BuiltinSourceModule || module.isBinary()) {
			return path.removeFirstSegments(containerPath.segmentCount())
					.setDevice(null).toString();
		} else if (module instanceof SourceModule) {
			return path.removeFirstSegments(1).toString();
		} else {
			return path.toString();
		}
	}
}
