package org.eclipse.dltk.debug.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.debug.ui.IDLTKDebugUIPreferenceConstants;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.ImprovedAbstractConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

/**
 * Preference page for script detail formatters.
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
 *     class=&quot;org.eclipse.dltk.debug.ui.preferences.ScriptDetailFormattersPreferencePage:[nature_id]&quot;
 *     id=&quot;...&quot;
 *     name=&quot;...&quot; /&gt;  
 * &lt;extension&gt;
 * </pre>
 */
public class ScriptDetailFormattersPreferencePage extends
		AbstractScriptDebugPreferencePage {

	/*
	 * @see
	 * org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage
	 * #createConfigurationBlock
	 * (org.eclipse.dltk.ui.preferences.OverlayPreferenceStore)
	 */
	protected IPreferenceConfigurationBlock createConfigurationBlock(
			OverlayPreferenceStore store) {
		return new ScriptDetailFormattersConfigurationBlock(store, this);
	}

	/*
	 * @see
	 * org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage
	 * #setDescription()
	 */
	protected void setDescription() {
		setDescription(ScriptDebugPreferencesMessages.ScriptDetailFormattersPreferencePage_description);
	}

	class ScriptDetailFormattersConfigurationBlock extends
			ImprovedAbstractConfigurationBlock {

		private Button fDetailPaneButton;
		private Button fInlineAllButton;

		private Button fInlineFormattersButton;

		public ScriptDetailFormattersConfigurationBlock(
				OverlayPreferenceStore store, PreferencePage page) {
			super(store, page);
		}

		public Control createControl(Composite parent) {
			final Composite composite = SWTFactory.createComposite(parent,
					parent.getFont(), 2, 0, GridData.FILL_BOTH);

			createLabelPreferences(composite);

			return composite;
		}

		public void performOk() {
			String value = IDLTKDebugUIPreferenceConstants.DETAIL_PANE;
			if (fInlineAllButton.getSelection()) {
				value = IDLTKDebugUIPreferenceConstants.INLINE_ALL;
			}
			// TODO: uncomment when inline formatter support added
			// else if (fInlineFormattersButton.getSelection()) {
			// value = IDLTKDebugUIPreferenceConstants.INLINE_FORMATTERS;
			// }

			getPreferenceStore().setValue(
					IDLTKDebugUIPreferenceConstants.PREF_SHOW_DETAILS, value);
		}

		protected List createOverlayKeys() {
			ArrayList keys = new ArrayList(1);
			keys.add(new OverlayPreferenceStore.OverlayKey(
					OverlayPreferenceStore.STRING,
					IDLTKDebugUIPreferenceConstants.PREF_SHOW_DETAILS));

			return keys;
		}

		protected void initializeFields() {
			super.initializeFields();
			initializeLabels();
		}

		private void createLabelPreferences(Composite parent) {
			Group group = SWTFactory
					.createGroup(
							parent,
							ScriptDebugPreferencesMessages.ScriptDetailFormattersPreferencePage_1,
							1, 2, GridData.FILL_HORIZONTAL);

			// Create the 3 detail option radio buttons
			// TODO: uncomment when inline formatter support added
			// fInlineFormattersButton = SWTFactory
			// .createRadioButton(
			// group,
			// ScriptDebugPreferencesMessages.
			// ScriptDetailFormattersPreferencePage_2);
			fInlineAllButton = SWTFactory
					.createRadioButton(
							group,
							ScriptDebugPreferencesMessages.ScriptDetailFormattersPreferencePage_3);
			fDetailPaneButton = SWTFactory
					.createRadioButton(
							group,
							ScriptDebugPreferencesMessages.ScriptDetailFormattersPreferencePage_4);

		}

		private void initializeLabels() {
			IPreferenceStore store = getPreferenceStore();
			String preference = store
					.getString(IDLTKDebugUIPreferenceConstants.PREF_SHOW_DETAILS);

			// TODO: uncomment when inline formatter support added
			// fInlineFormattersButton
			// .setSelection(IDLTKDebugUIPreferenceConstants.INLINE_FORMATTERS
			// .equals(preference));
			fInlineAllButton
					.setSelection(IDLTKDebugUIPreferenceConstants.INLINE_ALL
							.equals(preference));
			fDetailPaneButton
					.setSelection(IDLTKDebugUIPreferenceConstants.DETAIL_PANE
							.equals(preference));
		}
	}

}
