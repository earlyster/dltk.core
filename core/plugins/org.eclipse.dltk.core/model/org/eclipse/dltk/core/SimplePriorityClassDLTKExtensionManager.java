package org.eclipse.dltk.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class SimplePriorityClassDLTKExtensionManager extends
		PriorityDLTKExtensionManager {
	protected static final String CLASS_ATTR = "class"; //$NON-NLS-1$

	public SimplePriorityClassDLTKExtensionManager(String extensionPoint,
			String identifier) {
		super(extensionPoint, identifier);
	}

	public Object getObject(String id) {
		return getInitObject(getElementInfo(id));
	}

	public Object getInitObject(ElementInfo ext) {
		try {
			if (ext != null) {
				if (ext.object != null) {
					return ext.object;
				}

				Object object = createObject(ext.config);
				ext.object = object;
				return object;
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

	public Object[] getObjects() {
		ElementInfo[] infos = this.getElementInfos();
		List objs = new ArrayList();
		for (int i = 0; i < infos.length; i++) {
			Object o = getInitObject(infos[i]);
			if (o != null) {
				objs.add(o);
			}
		}
		return objs.toArray(new Object[objs.size()]);
	}
}