package org.eclipse.dltk.ui.preferences;

/**
 * Preference page for script editor hover settings.
 * 
 * <p>
 * Implementations that wish to expose this preference page need only include
 * the following snippet in their <code>plugin.xml</code>:
 * </p>
 * 
 * <pre>
 * &lt;extension point=&quot;org.eclipse.ui.preferencePages&quot;&gt;
 *   &lt;page
 *     category=&quot;...&quot;
 *     class=&quot;org.eclipse.dltk.ui.preferences.ScriptEditorHoverPreferencePage:nature_id&quot;
 *     id=&quot;...&quot;
 *     name=&quot;...&quot; /&gt;  
 * &lt;extension&gt;
 * </pre>
 */
public class ScriptEditorHoverPreferencePage extends
		AbstractScriptPreferencePage {

	protected IPreferenceConfigurationBlock createConfigurationBlock(
			OverlayPreferenceStore overlayPreferenceStore) {
		return new ScriptEditorHoverConfigurationBlock(this,
				overlayPreferenceStore, getToolkit().getNatureId());
	}

	protected void setDescription() {
		// setDescription(PreferencesMessages.DLTKEditorPreferencePage_hoverTab_title);
	}
}
