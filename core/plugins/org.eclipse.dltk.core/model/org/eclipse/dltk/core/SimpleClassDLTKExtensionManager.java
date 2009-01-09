package org.eclipse.dltk.core;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class SimpleClassDLTKExtensionManager extends SimpleDLTKExtensionManager {
	private static final String CLASS_ATTR = "class"; //$NON-NLS-1$

	public SimpleClassDLTKExtensionManager(String extension) {
		super(extension);
	}

	public Object[] getObjects() {
		final List infos = getElementInfoList();
		Object[] objs = new Object[infos.size()];
		int index = 0;
		for (int i = 0; i < infos.size(); i++) {
			final Object o = getInitObject((ElementInfo) infos.get(i));
			if (o != null) {
				objs[index++] = o;
			}
		}
		if (index != objs.length) {
			final Object[] temp = new Object[index];
			System.arraycopy(objs, 0, temp, 0, index);
			objs = temp;
		}
		return objs;
	}

	public Object getInitObject(ElementInfo ext) {
		try {
			if (ext != null) {
				if (ext.object != null) {
					return ext.object;
				}
				ext.object = createObject(ext.config);
				return ext.object;
			}
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected Object createObject(IConfigurationElement cfg)
			throws CoreException {
		return cfg.createExecutableExtension(CLASS_ATTR);
	}
}
