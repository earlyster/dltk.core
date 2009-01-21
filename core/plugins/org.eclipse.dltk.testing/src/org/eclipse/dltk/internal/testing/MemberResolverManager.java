package org.eclipse.dltk.internal.testing;

import org.eclipse.dltk.core.PriorityClassDLTKExtensionManager;
import org.eclipse.dltk.testing.DLTKTestingPlugin;
import org.eclipse.dltk.testing.ITestingElementResolver;

public final class MemberResolverManager {
	private static PriorityClassDLTKExtensionManager manager = new PriorityClassDLTKExtensionManager(
			DLTKTestingPlugin.PLUGIN_ID + ".memberResolver", "id"); //$NON-NLS-1$ //$NON-NLS-2$

	public static ITestingElementResolver getResolver(String id) {
		return (ITestingElementResolver) manager.getObject(id);
	}
}
