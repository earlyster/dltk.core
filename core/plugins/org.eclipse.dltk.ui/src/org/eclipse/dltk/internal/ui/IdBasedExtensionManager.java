/**
 * 
 */
package org.eclipse.dltk.internal.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.core.PriorityDLTKExtensionManager;
import org.eclipse.dltk.core.PriorityDLTKExtensionManager.ElementInfo;
import org.eclipse.dltk.ui.actions.IActionFilterTester;

class IdBasedExtensionManager extends PriorityDLTKExtensionManager {
	private static final String CLASS_ATTR = "class"; //$NON-NLS-1$
	public IdBasedExtensionManager(String extension) {
		super(extension, "id"); //$NON-NLS-1$
	}
	public IActionFilterTester getObject(String id) throws CoreException {
		ElementInfo ext = this.getElementInfo(id);

		return (IActionFilterTester)getInitObject(ext);
	}

	public Object getInitObject(ElementInfo ext) throws CoreException {
		if (ext != null) {
			if (ext.object != null) {
				return ext.object;
			}

			IConfigurationElement cfg = (IConfigurationElement) ext.getConfig();
			Object object = createObject(cfg);
			ext.object = object;
			return object;
		}
		return null;
	}

	protected Object createObject(IConfigurationElement cfg) throws CoreException {
		return cfg.createExecutableExtension(CLASS_ATTR);
	}
}