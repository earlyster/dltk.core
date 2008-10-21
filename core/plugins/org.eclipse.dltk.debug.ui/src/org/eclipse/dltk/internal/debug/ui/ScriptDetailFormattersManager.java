package org.eclipse.dltk.internal.debug.ui;

import java.util.HashMap;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.SimpleDLTKExtensionManager;
import org.eclipse.dltk.core.SimpleDLTKExtensionManager.ElementInfo;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationCommand;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationListener;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationResult;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.debug.core.model.IScriptValue;
import org.eclipse.dltk.debug.ui.DLTKDebugUILanguageManager;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.debug.ui.IDLTKDebugUIPreferenceConstants;
import org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class ScriptDetailFormattersManager implements IPropertyChangeListener {
	private static final String DEFAULT_FORMATTER_TYPE = "#DEFAULT#"; //$NON-NLS-1$
	private static final String ATTR_SNIPPET = "snippet"; //$NON-NLS-1$
	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTR_NATURE = "nature"; //$NON-NLS-1$
	private static final String SCRIPT_DETAIL_FORMATTER_EXTENSION = DLTKDebugUIPlugin.PLUGIN_ID
			+ ".scriptDetailFormatter"; //$NON-NLS-1$

	private static HashMap managerInstances = new HashMap();
	private static final String CANNOT_EVALUATE = Messages.ScriptDetailFormattersManager_cantEvaluateDetails;
	private HashMap formatters = new HashMap();
	private DetailFormatter defaultFormatter = null;

	/**
	 * Return the default detail formatters manager.
	 * 
	 * @param natureId
	 * 
	 * @return default detail formatters manager.
	 */
	static public ScriptDetailFormattersManager getDefault(String natureId) {
		ScriptDetailFormattersManager instance = (ScriptDetailFormattersManager) managerInstances
				.get(natureId);
		if (instance == null) {
			instance = new ScriptDetailFormattersManager(natureId);
			managerInstances.put(natureId, instance);
		}
		return instance;
	}

	private ScriptDetailFormattersManager(String natureId) {
		populateDetailFormatters(natureId);

		DLTKDebugUILanguageManager.getLanguageToolkit(natureId)
				.getPreferenceStore().addPropertyChangeListener(this);
	}

	private void populateDetailFormatters(String natureId) {
		SimpleDLTKExtensionManager manager = new SimpleDLTKExtensionManager(
				SCRIPT_DETAIL_FORMATTER_EXTENSION);
		ElementInfo[] infos = manager.getElementInfos();
		for (int i = 0; i < infos.length; i++) {
			IConfigurationElement config = infos[i].getConfig();
			String nature = config.getAttribute(ATTR_NATURE);
			if (natureId.equals(nature)) {
				String code = config.getAttribute(ATTR_SNIPPET);
				String type = config.getAttribute(ATTR_TYPE);
				DetailFormatter formatter = new DetailFormatter(type, code,
						true);
				if (DEFAULT_FORMATTER_TYPE.equals(type)) {
					setDefaultFormatter(formatter);
				} else {
					addFormatter(formatter);
				}
			}
		}
	}

	private String getValueText(IValue value) {
		if (value instanceof IScriptValue) {
			ScriptDebugModelPresentation presentation = DLTKDebugUIPlugin
					.getDefault().getModelPresentation(
							value.getModelIdentifier());
			return presentation.getDetailPaneText((IScriptValue) value);
		}
		return null;
	}

	public void computeValueDetail(final IScriptValue value,
			final IScriptThread thread, final IValueDetailListener listener) {
		if (thread == null || !thread.isSuspended()) {
			listener.detailComputed(value, getValueText(value));
			return;
		}
		final DetailFormatter formatter = getDetailFormatter(value);
		if (formatter == null || !formatter.isEnabled()) {
			/*
			 * if the client doesn't define a formatter, or the engine doesn't
			 * support the 'eval' command, fall back to using the value returned
			 * in the 'property' response
			 */
			listener.detailComputed(value, getValueText(value));
			return;
		}
		final IScriptEvaluationCommand command = value.createEvaluationCommand(
				formatter.getSnippet(), thread);
		if (command == null) {
			listener.detailComputed(value, getValueText(value));
			return;
		}
		command.asyncEvaluate(new IScriptEvaluationListener() {
			public void evaluationComplete(IScriptEvaluationResult result) {
				if (result == null)
					return;

				IScriptValue resultValue = result.getValue();
				if (resultValue != null) {
					listener.detailComputed(value, getValueText(resultValue));
				} else {
					try {
						listener
								.detailComputed(value, value.getValueString()/* CANNOT_EVALUATE */);
					} catch (DebugException e) {
						if (DLTKCore.DEBUG) {
							e.printStackTrace();
						}
						listener.detailComputed(value, CANNOT_EVALUATE);
					}
				}
			}
		});
	}

	public DetailFormatter getDetailFormatter(IScriptValue value) {
		DetailFormatter formatter = (DetailFormatter) formatters.get(value
				.getType().getName());
		if (formatter == null)
			formatter = getDefaultFormatter();
		return formatter;
	}

	private DetailFormatter getDefaultFormatter() {
		return defaultFormatter;
	}

	public void setDefaultFormatter(DetailFormatter formatter) {
		defaultFormatter = formatter;
	}

	public void addFormatter(DetailFormatter formatter) {
		formatters.put(formatter.getTypeName(), formatter);
	}

	public void removeFormatter(DetailFormatter formatter) {
		formatters.remove(formatter.getTypeName());
	}

	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (handlesPropertyEvent(property)) {
			// TODO: uncomment when supported
			// populateDetailFormatters(natureId);
			// cacheMap.clear();

			/*
			 * fire a change event on it so the variables view will update for
			 * any formatter changes.
			 */
			IAdaptable selected = DebugUITools.getDebugContext();
			if (selected != null) {
				IScriptStackFrame frame = (IScriptStackFrame) selected
						.getAdapter(IScriptStackFrame.class);
				if (frame != null) {
					DebugPlugin.getDefault().fireDebugEventSet(
							new DebugEvent[] { new DebugEvent(frame,
									DebugEvent.CHANGE) });
				}
			}
		}
	}

	private boolean handlesPropertyEvent(String property) {
		// TODO: uncomment when supported
		// if
		// (IDLTKDebugUIPreferenceConstants.PREF_DETAIL_FORMATTERS_LIST.equals
		// (property)) {
		// return true;
		// }

		if (IDLTKDebugUIPreferenceConstants.PREF_SHOW_DETAILS.equals(property)) {
			return true;
		}

		if (IDebugUIConstants.PREF_MAX_DETAIL_LENGTH.equals(property)) {
			return true;
		}

		return false;
	}

}
