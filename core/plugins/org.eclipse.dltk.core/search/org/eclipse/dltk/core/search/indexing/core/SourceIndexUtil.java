package org.eclipse.dltk.core.search.indexing.core;

import org.eclipse.core.runtime.IPath;
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
		if (module instanceof ExternalSourceModule
				|| module instanceof BuiltinSourceModule) {
			return path.removeFirstSegments(containerPath.segmentCount())
					.setDevice(null).toString();
		} else if (module instanceof SourceModule) {
			return path.removeFirstSegments(1).toString();
		} else {
			return path.toString();
		}
	}
}
