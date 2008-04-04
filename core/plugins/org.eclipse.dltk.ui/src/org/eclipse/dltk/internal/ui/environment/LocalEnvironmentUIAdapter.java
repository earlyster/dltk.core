package org.eclipse.dltk.internal.ui.environment;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;
import org.eclipse.dltk.ui.environment.IEnvironmentUI;

public class LocalEnvironmentUIAdapter implements IAdapterFactory {
	private final static Class[] ADAPTABLES = new Class[] { IEnvironmentUI.class };

	public LocalEnvironmentUIAdapter() {
	}

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof LocalEnvironment
				&& adapterType.equals(IEnvironmentUI.class)) {
			return new LocalEnvironmentUI();
		}
		return null;
	}

	public Class[] getAdapterList() {
		return ADAPTABLES;
	}
}
