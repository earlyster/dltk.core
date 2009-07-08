package org.eclipse.dltk.debug.ui;

import java.util.HashMap;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.ui.actions.IWatchExpressionFactoryAdapter;
import org.eclipse.dltk.debug.core.model.IScriptDebugTarget;
import org.eclipse.dltk.debug.core.model.IScriptVariable;
import org.eclipse.dltk.internal.debug.ui.variables.ScriptVariableLabelProvider;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.jface.preference.IPreferenceStore;

public class ScriptDebugElementAdapterFactory implements IAdapterFactory {

	private static ScriptDebugElementAdapterFactory instance;
	// assume only 1 plugin installed
	private final HashMap<String, ScriptVariableLabelProvider> variableLabelProviders = new HashMap<String, ScriptVariableLabelProvider>(
			1, 1);

	// private static final IElementLabelProvider fgLPVariable = new
	// ScriptVariableLableProvider();
	// private static final IElementContentProvider fgCPVariable = new
	// JavaVariableContentProvider();
	// private static final IElementLabelProvider fgLPExpression = new
	// ExpressionLabelProvider();
	// private static final IElementContentProvider fgCPExpression = new
	// JavaExpressionContentProvider();

	private static final IWatchExpressionFactoryAdapter watchExpressionFactory = new ScriptWatchExpressionFilter();

	public synchronized static ScriptDebugElementAdapterFactory getInstance() {
		if (instance == null) {
			instance = new ScriptDebugElementAdapterFactory();
		}

		return instance;
	}

	private ScriptDebugElementAdapterFactory() {
		// private constructor
	}

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IElementLabelProvider.class.equals(adapterType)) {
			if (adaptableObject instanceof IScriptVariable) {
				return getVariableLabelProvider((IDebugElement) adaptableObject);
			}
		}
		/*
		 * if (adaptableObject instanceof JavaInspectExpression) { return
		 * fgLPExpression; } }
		 */

		/*
		 * if (IElementContentProvider.class.equals(adapterType)) { if
		 * (adaptableObject instanceof IJavaVariable) { return fgCPVariable; }
		 * if (adaptableObject instanceof JavaInspectExpression) { return
		 * fgCPExpression; } }
		 */

		if (IWatchExpressionFactoryAdapter.class.equals(adapterType)) {
			if (adaptableObject instanceof IScriptVariable) {
				return watchExpressionFactory;
			}
			/*
			 * if (adaptableObject instanceof JavaInspectExpression) { return
			 * fgCPExpression; }
			 */
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { IElementLabelProvider.class,
		// IElementContentProvider.class,
				IWatchExpressionFactoryAdapter.class };
	}

	public void dispose() {
		disposeVariableLabelProviders();
	}

	private void disposeVariableLabelProviders() {
		synchronized (variableLabelProviders) {
			for (ScriptVariableLabelProvider provider : variableLabelProviders
					.values()) {
				provider.dispose();
			}
			variableLabelProviders.clear();
		}
	}

	private IPreferenceStore getPreferenceStore(IDebugElement element) {
		String natureId = ((IScriptDebugTarget) element.getDebugTarget())
				.getLanguageToolkit().getNatureId();
		return DLTKUILanguageManager.getLanguageToolkit(natureId)
				.getPreferenceStore();
	}

	private ScriptVariableLabelProvider getVariableLabelProvider(
			IDebugElement toAdapt) {
		final String id = toAdapt.getModelIdentifier();
		ScriptVariableLabelProvider provider;
		synchronized (variableLabelProviders) {
			provider = variableLabelProviders.get(id);
			if (provider == null) {
				provider = new ScriptVariableLabelProvider(
						getPreferenceStore(toAdapt));
				variableLabelProviders.put(id, provider);
			}
		}
		return provider;
	}
}
