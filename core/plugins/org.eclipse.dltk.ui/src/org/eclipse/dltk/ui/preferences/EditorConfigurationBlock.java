/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.dltk.ui.preferences;

import java.util.ArrayList;

import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class EditorConfigurationBlock extends AbstractConfigurationBlock {
	public static final int FLAG_TAB_POLICY = 1;
	public static final int FLAG_EDITOR_SMART_NAVIGATION = 2;
	public static final int FLAG_TAB_ALWAYS_INDENT = 4;

	private final int flags;

	public EditorConfigurationBlock(PreferencePage mainPreferencePage,
			OverlayPreferenceStore store) {
		this(mainPreferencePage, store, FLAG_TAB_POLICY
				| FLAG_EDITOR_SMART_NAVIGATION);
	}

	/**
	 * @param mainPreferencePage
	 * @param store
	 * @param disableSmart
	 * @deprecated
	 */
	public EditorConfigurationBlock(PreferencePage mainPreferencePage,
			OverlayPreferenceStore store, boolean disableSmart) {
		this(mainPreferencePage, store, FLAG_TAB_POLICY
				| (disableSmart ? 0 : FLAG_EDITOR_SMART_NAVIGATION));
	}

	/**
	 * @param mainPreferencePage
	 * @param store
	 * @param disableSmart
	 * @param tabAlwaysIndent
	 * @deprecated
	 */
	public EditorConfigurationBlock(PreferencePage mainPreferencePage,
			OverlayPreferenceStore store, boolean disableSmart,
			boolean tabAlwaysIndent) {
		this(mainPreferencePage, store, FLAG_TAB_POLICY
				| (disableSmart ? 0 : FLAG_EDITOR_SMART_NAVIGATION)
				| (tabAlwaysIndent ? FLAG_TAB_ALWAYS_INDENT : 0));
	}

	public EditorConfigurationBlock(PreferencePage mainPreferencePage,
			OverlayPreferenceStore store, int flags) {
		super(store, mainPreferencePage);
		this.flags = flags;
		getPreferenceStore().addKeys(createOverlayStoreKeys(flags));
	}

	private static OverlayPreferenceStore.OverlayKey[] createOverlayStoreKeys(
			int flags) {
		ArrayList keys = new ArrayList();
		if ((flags & FLAG_EDITOR_SMART_NAVIGATION) != 0) {
			keys.add(new OverlayPreferenceStore.OverlayKey(
					OverlayPreferenceStore.BOOLEAN,
					PreferenceConstants.EDITOR_SMART_HOME_END));
			keys.add(new OverlayPreferenceStore.OverlayKey(
					OverlayPreferenceStore.BOOLEAN,
					PreferenceConstants.EDITOR_SUB_WORD_NAVIGATION));
			keys.add(new OverlayPreferenceStore.OverlayKey(
					OverlayPreferenceStore.BOOLEAN,
					PreferenceConstants.EDITOR_SMART_INDENT));
		}
		if ((flags & FLAG_TAB_POLICY) != 0) {
			keys.add(new OverlayPreferenceStore.OverlayKey(
					OverlayPreferenceStore.STRING,
					CodeFormatterConstants.FORMATTER_TAB_CHAR));
			keys.add(new OverlayPreferenceStore.OverlayKey(
					OverlayPreferenceStore.INT,
					CodeFormatterConstants.FORMATTER_TAB_SIZE));
			keys.add(new OverlayPreferenceStore.OverlayKey(
					OverlayPreferenceStore.INT,
					CodeFormatterConstants.FORMATTER_INDENTATION_SIZE));
		}
		if ((flags & FLAG_TAB_ALWAYS_INDENT) != 0) {
			keys.add(new OverlayPreferenceStore.OverlayKey(
					OverlayPreferenceStore.BOOLEAN,
					PreferenceConstants.EDITOR_TAB_ALWAYS_INDENT));
		}

		return (OverlayPreferenceStore.OverlayKey[]) keys
				.toArray(new OverlayPreferenceStore.OverlayKey[keys.size()]);
	}

	/**
	 * Creates page for appearance preferences.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the control for the preference page
	 */
	public Control createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout());

		if ((flags & FLAG_EDITOR_SMART_NAVIGATION) != 0) {
			Composite composite;

			composite = createSubsection(control, null,
					PreferencesMessages.EditorPreferencePage_title0);
			createSettingsGroup(composite);
		}

		createTabsGroup(control);

		return control;
	}

	private void createTabsGroup(Composite composite) {
		Composite generalGroup = createSubsection(composite, null,
				FormatterMessages.IndentationTabPage_preview_header);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		generalGroup.setLayout(layout);

		if ((flags & FLAG_TAB_POLICY) != 0) {
			final String[] tabPolicyValues = new String[] {
					CodeFormatterConstants.SPACE, CodeFormatterConstants.TAB,
					CodeFormatterConstants.MIXED };
			final String[] tabPolicyLabels = new String[] {
					FormatterMessages.IndentationTabPage_general_group_option_tab_policy_SPACE,
					FormatterMessages.IndentationTabPage_general_group_option_tab_policy_TAB,
					FormatterMessages.IndentationTabPage_general_group_option_tab_policy_MIXED };

			addComboBox(
					generalGroup,
					FormatterMessages.IndentationTabPage_general_group_option_tab_policy,
					CodeFormatterConstants.FORMATTER_TAB_CHAR, tabPolicyLabels,
					tabPolicyValues);

			addLabelledTextField(
					generalGroup,
					FormatterMessages.IndentationTabPage_general_group_option_indent_size,
					CodeFormatterConstants.FORMATTER_INDENTATION_SIZE, 2, 1,
					true);

			addLabelledTextField(
					generalGroup,
					FormatterMessages.IndentationTabPage_general_group_option_tab_size,
					CodeFormatterConstants.FORMATTER_TAB_SIZE, 2, 1, true);
		}

		if ((flags & FLAG_TAB_ALWAYS_INDENT) != 0) {
			addCheckBox(generalGroup,
					PreferencesMessages.EditorPreferencePage_tabAlwaysIndent,
					PreferenceConstants.EDITOR_TAB_ALWAYS_INDENT, 2);
		}
	}

	private Control createSettingsGroup(Composite composite) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		addCheckBox(composite,
				PreferencesMessages.EditorPreferencePage_smartHomeEnd,
				PreferenceConstants.EDITOR_SMART_HOME_END, 0);

		addCheckBox(composite,
				PreferencesMessages.EditorPreferencePage_subWordNavigation,
				PreferenceConstants.EDITOR_SUB_WORD_NAVIGATION, 0);

		addCheckBox(composite,
				PreferencesMessages.EditorPreferencePage_smartIndent,
				PreferenceConstants.EDITOR_SMART_INDENT, 0);

		return composite;
	}

	public void initialize() {

		super.initialize();

	}

	public void performDefaults() {
		super.performDefaults();

	}
}
