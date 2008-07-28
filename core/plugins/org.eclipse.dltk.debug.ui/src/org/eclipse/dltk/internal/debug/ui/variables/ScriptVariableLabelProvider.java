package org.eclipse.dltk.internal.debug.ui.variables;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.DelegatingModelPresentation;
import org.eclipse.debug.internal.ui.model.elements.VariableLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.views.DebugModelPresentationContext;
import org.eclipse.debug.ui.IDebugModelPresentation;
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

	/**
	 * XXX 3.3 compatibility getModelPresentation(IPresentationContext,String)
	 * 
	 * @param context
	 * @param modelId
	 * @return
	 */
	protected IDebugModelPresentation getModelPresentation(
			IPresentationContext context, String modelId) {
		if (context instanceof DebugModelPresentationContext) {
			DebugModelPresentationContext debugContext = (DebugModelPresentationContext) context;
			IDebugModelPresentation presentation = debugContext
					.getModelPresentation();
			if (presentation instanceof DelegatingModelPresentation) {
				return ((DelegatingModelPresentation) presentation)
						.getPresentation(modelId);
			}
		}
		return null;
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
