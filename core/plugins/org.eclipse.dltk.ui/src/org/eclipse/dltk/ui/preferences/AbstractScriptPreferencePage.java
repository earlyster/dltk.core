package org.eclipse.dltk.ui.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.DLTKExecuteExtensionHelper;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.jface.preference.IPreferenceStore;

public abstract class AbstractScriptPreferencePage extends
		AbstractConfigurationBlockPreferencePage implements
		IExecutableExtension {

	private IDLTKLanguageToolkit fToolkit;

	/*
	 * @see
	 * org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org
	 * .eclipse.core.runtime.IConfigurationElement, java.lang.String,
	 * java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) {
		fToolkit = DLTKExecuteExtensionHelper.getLanguageToolkit(config,
				propertyName, data);
		IDLTKUILanguageToolkit uiToolkit = DLTKUILanguageManager
				.getLanguageToolkit(fToolkit.getNatureId());

		IPreferenceStore preferenceStore = uiToolkit.getPreferenceStore();
		Assert.isNotNull(preferenceStore);
		setPreferenceStore(preferenceStore);
	}

	/*
	 * @see
	 * org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage
	 * #getHelpId()
	 */
	protected String getHelpId() {
		return null;
	}

	/*
	 * @see
	 * org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage
	 * #setPreferenceStore()
	 */
	protected final void setPreferenceStore() {
		// do nothing, see setInitializationData
	}

	protected IDLTKLanguageToolkit getToolkit() {
		return fToolkit;
	}

}
