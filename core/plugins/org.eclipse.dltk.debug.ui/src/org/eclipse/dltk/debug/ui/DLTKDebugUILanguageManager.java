package org.eclipse.dltk.debug.ui;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.dltk.core.PriorityClassDLTKExtensionManager;
import org.eclipse.dltk.debug.core.model.IScriptDebugTarget;

public class DLTKDebugUILanguageManager extends
		PriorityClassDLTKExtensionManager {

	private static DLTKDebugUILanguageManager self;

	private final static String LANGUAGE_EXTPOINT = DLTKDebugUIPlugin.PLUGIN_ID
			+ ".language"; //$NON-NLS-1$

	private DLTKDebugUILanguageManager() {
		super(LANGUAGE_EXTPOINT);
	}

	static synchronized DLTKDebugUILanguageManager getInstance() {
		if (self == null) {
			self = new DLTKDebugUILanguageManager();
		}

		return self;
	}

	public static IDLTKDebugUILanguageToolkit getLanguageToolkit(
			IDebugTarget target) {
		Assert.isTrue(target instanceof IScriptDebugTarget);

		String natureId = ((IScriptDebugTarget) target).getLanguageToolkit()
				.getNatureId();

		return getLanguageToolkit(natureId);
	}

	public static IDLTKDebugUILanguageToolkit getLanguageToolkit(String natureId) {
		return (IDLTKDebugUILanguageToolkit) getInstance().getObject(natureId);
	}

	public static IDLTKDebugUILanguageToolkit[] getLanguageToolkits() {
		List toolkits = getInstance().getObjectList();
		return (IDLTKDebugUILanguageToolkit[]) toolkits
				.toArray(new IDLTKDebugUILanguageToolkit[toolkits.size()]);
	}
}
