package org.eclipse.dltk.ui.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.DLTKExecuteExtensionHelper;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Abstract base class for script preference pages.
 * 
 * <p>
 * Preferenece pages that extend from this class will have their preference
 * values stored in the <code>IPreferenceStore</code> returned from the plugin's
 * {@link IDLTKUILanguageToolkit} implementation.
 * </p>
 * 
 * <p>
 * These pages will also need to be configured with the specific nature id for
 * the plugin in the <code>plugin.xml</code> file. ie:
 * </p>
 * 
 * <pre>
 * &lt;page 
 *   category=&quot;...&quot;
 *   class=&quot;class:nature_id&quot;
 *   id=&quot;...&quot;
 *   name=&quot;...&quot; /&gt;
 * </pre>
 */
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
	public final void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) {
		fToolkit = DLTKExecuteExtensionHelper.getLanguageToolkit(config,
				propertyName, data);

		Assert.isNotNull(fToolkit);
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
		IDLTKUILanguageToolkit uiToolkit = DLTKUILanguageManager
				.getLanguageToolkit(fToolkit.getNatureId());
		Assert.isNotNull(uiToolkit);

		IPreferenceStore store = uiToolkit.getPreferenceStore();

		Assert.isNotNull(store);
		setPreferenceStore(store);
	}

	protected final IDLTKLanguageToolkit getToolkit() {
		return fToolkit;
	}
}
