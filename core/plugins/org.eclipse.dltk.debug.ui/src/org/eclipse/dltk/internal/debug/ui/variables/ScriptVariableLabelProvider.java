package org.eclipse.dltk.internal.debug.ui.variables;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.model.elements.VariableLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class ScriptVariableLabelProvider extends VariableLabelProvider
		implements IPropertyChangeListener {

	private IPreferenceStore store;

	public ScriptVariableLabelProvider(IPreferenceStore store) {
		this.store = store;
		this.store.addPropertyChangeListener(this);
	}

	public void dispose() {
		store.removePropertyChangeListener(this);
		store = null;
	}

	protected IDebugModelPresentation getModelPresentation(
			IPresentationContext context, String modelId) {
		/*
		 * no longer here for 3.3 compatibility...
		 * 
		 * we need to use the langugage specific implementation so we can get
		 * access to addtional presentation methods by downcasting to the
		 * ScriptDebugModelPresentation.
		 * 
		 * the IDebugModelPresentation impl normally returned from a this method
		 * is an instance of LazyModelPresentation, which does not provide a way
		 * to obtain the underlying model presentation it is being lazy for.
		 * 
		 * the jdt instanciates its IDebugModelPresentation implementation
		 * outright in its VariableLabelProvider implenentation, so this should
		 * be ok to do.
		 */
		return DLTKDebugUIPlugin.getDefault().getModelPresentation(modelId);
	}

	protected String getVariableName(IVariable variable,
			IPresentationContext context) throws CoreException {
		IDebugModelPresentation presentation = getModelPresentation(context,
				variable.getModelIdentifier());
		if (presentation != null) {
			return ((ScriptDebugModelPresentation) presentation)
					.getVariableName(variable);
		}

		return super.getVariableName(variable, context);
	}

	protected String getValueText(IVariable variable, IValue value,
			IPresentationContext context) throws CoreException {
		IDebugModelPresentation presentation = getModelPresentation(context,
				value.getModelIdentifier());
		if (presentation != null) {
			return presentation.getText(value);
		}

		return super.getValueText(variable, value, context);
	}

	public void propertyChange(PropertyChangeEvent event) {
		// TODO: support for fq name vs 'last segment'
	}

}
