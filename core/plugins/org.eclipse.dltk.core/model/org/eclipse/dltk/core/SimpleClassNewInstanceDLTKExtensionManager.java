package org.eclipse.dltk.core;

import org.eclipse.core.runtime.CoreException;

public class SimpleClassNewInstanceDLTKExtensionManager extends
		SimpleDLTKExtensionManager {
	private static final String CLASS_ATTR = "class"; //$NON-NLS-1$

	public SimpleClassNewInstanceDLTKExtensionManager(String extension) {
		super(extension);
	}

	public Object createObject(ElementInfo info) throws CoreException {
		return info.getConfig().createExecutableExtension(CLASS_ATTR);
	}
}
