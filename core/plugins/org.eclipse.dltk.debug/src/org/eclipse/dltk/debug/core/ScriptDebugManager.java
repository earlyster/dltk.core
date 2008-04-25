package org.eclipse.dltk.debug.core;

import java.util.Comparator;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.debug.core.model.IScriptTypeFactory;
import org.eclipse.dltk.internal.debug.core.model.VariableNameComparator;

public class ScriptDebugManager {
	private static final String SCRIPT_DEBUG_MODEL_EXT_POINT = DLTKDebugPlugin.PLUGIN_ID
			+ ".scriptDebugModel"; //$NON-NLS-1$
	private static final String NATURE_ID = "natureId"; //$NON-NLS-1$
	private static final String DEBUG_MODEL_ID = "debugModelId"; //$NON-NLS-1$
	private static final String TYPE_FACTORY = "typeFactory"; //$NON-NLS-1$
	private static final String VARIABLE_NAME_COMPARATOR = "variableNameComparator"; //$NON-NLS-1$

	private static ScriptDebugManager instance;

	public static ScriptDebugManager getInstance() {
		if (instance == null) {
			instance = new ScriptDebugManager();
		}

		return instance;
	}

	private final HashMap natureToInfoMap;
	private final HashMap modelToNatureMap;

	private static class Info {
		public final String debugModelId;
		public final IScriptTypeFactory typeFactory;
		public final Comparator comparator;

		public Info(String debugModelId, IScriptTypeFactory typeFactory,
				Comparator comparator) {
			this.debugModelId = debugModelId;
			this.typeFactory = typeFactory;
			this.comparator = comparator;
		}
	}

	private void loadExtenstionPoints() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtension[] extensions = registry.getExtensionPoint(
				SCRIPT_DEBUG_MODEL_EXT_POINT).getExtensions();

		for (int i = 0; i < extensions.length; i++) {
			IExtension extension = extensions[i];
			IConfigurationElement[] elements = extension
					.getConfigurationElements();

			if (elements.length > 0) {
				IConfigurationElement element = elements[0];
				final String natureId = element.getAttribute(NATURE_ID);
				final String debugModelId = element
						.getAttribute(DEBUG_MODEL_ID);

				IScriptTypeFactory typeFactory = null;

				try {
					typeFactory = (IScriptTypeFactory) element
							.createExecutableExtension(TYPE_FACTORY);
				} catch (CoreException e) {
					DLTKDebugPlugin.log(e);
				}

				Comparator comparator = null;
				String comparatorId = element
						.getAttribute(VARIABLE_NAME_COMPARATOR);
				if (comparatorId != null) {
					try {
						comparator = (Comparator) element
								.createExecutableExtension(VARIABLE_NAME_COMPARATOR);
					} catch (CoreException e) {
						DLTKDebugPlugin.log(e);
					}
				}
				if (comparator == null) {
					comparator = new VariableNameComparator();
				}

				if (natureId != null && debugModelId != null) {
					natureToInfoMap.put(natureId, new Info(debugModelId,
							typeFactory, comparator));
					modelToNatureMap.put(debugModelId, natureId);
				}
			}
		}
	}

	protected Info getInfo(String natureId) {
		return (Info) natureToInfoMap.get(natureId);
	}

	protected ScriptDebugManager() {
		natureToInfoMap = new HashMap();
		modelToNatureMap = new HashMap();

		loadExtenstionPoints();
	}

	public String getNatureByDebugModel(String debugModelId) {
		return (String) modelToNatureMap.get(debugModelId);
	}

	public String getDebugModelByNature(String natureId) {
		return getInfo(natureId).debugModelId;
	}

	public IScriptTypeFactory getTypeFactoryByNature(String natureId) {
		return getInfo(natureId).typeFactory;
	}

	public IScriptTypeFactory getTypeFactoryByDebugModel(String debugModelId) {
		return getTypeFactoryByNature(getNatureByDebugModel(debugModelId));
	}

	public Comparator getVariableNameComparatorByNature(String natureId) {
		return getInfo(natureId).comparator;
	}

	public Comparator getVariableNameComparatorByDebugModel(String debugModelId) {
		return getVariableNameComparatorByNature(getNatureByDebugModel(debugModelId));
	}
}
