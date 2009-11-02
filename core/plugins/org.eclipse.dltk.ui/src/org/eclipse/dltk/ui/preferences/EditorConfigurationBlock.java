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
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore.OverlayKey;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class EditorConfigurationBlock extends AbstractConfigurationBlock {
	public static final int FLAG_TAB_POLICY = 1;
	public static final int FLAG_EDITOR_SMART_NAVIGATION = 2;
	public static final int FLAG_TAB_ALWAYS_INDENT = 4;
	/**
	 * @since 2.0
	 */
	public static final int FLAG_EDITOR_APPEARANCE_COLOR_OPTIONS = 8;

	private final int flags;

	protected static class EditorColorItem {
		final String name;
		final String colorKey;
		final String systemDefaultKey;
		final int systemColor;

		public EditorColorItem(String name, String colorKey) {
			this(name, colorKey, null, 0);
		}

		public EditorColorItem(String name, String colorKey,
				String systemDefaultKey, int systemColor) {
			this.name = name;
			this.colorKey = colorKey;
			this.systemDefaultKey = systemDefaultKey;
			this.systemColor = systemColor;
		}

	}

	private final EditorColorItem[] fColorListModel;

	protected EditorColorItem[] createColorListModel() {
		return new EditorColorItem[] {
				new EditorColorItem(
						PreferencesMessages.EditorPreferencePage_matchingBracketsHighlightColor,
						PreferenceConstants.EDITOR_MATCHING_BRACKETS_COLOR),
				new EditorColorItem(
						PreferencesMessages.EditorPreferencePage_backgroundForMethodParameters,
						PreferenceConstants.CODEASSIST_PARAMETERS_BACKGROUND),
				new EditorColorItem(
						PreferencesMessages.EditorPreferencePage_foregroundForMethodParameters,
						PreferenceConstants.CODEASSIST_PARAMETERS_FOREGROUND),
				new EditorColorItem(
						PreferencesMessages.EditorPreferencePage_backgroundForCompletionReplacement,
						PreferenceConstants.CODEASSIST_REPLACEMENT_BACKGROUND),
				new EditorColorItem(
						PreferencesMessages.EditorPreferencePage_foregroundForCompletionReplacement,
						PreferenceConstants.CODEASSIST_REPLACEMENT_FOREGROUND),
				new EditorColorItem(
						PreferencesMessages.EditorPreferencePage_sourceHoverBackgroundColor,
						PreferenceConstants.EDITOR_SOURCE_HOVER_BACKGROUND_COLOR,
						PreferenceConstants.EDITOR_SOURCE_HOVER_BACKGROUND_COLOR_SYSTEM_DEFAULT,
						SWT.COLOR_INFO_BACKGROUND) };
	}

	private List colorList;
	private ColorSelector colorEditor;
	private Button colorDefault;

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
		flags = flags | FLAG_EDITOR_APPEARANCE_COLOR_OPTIONS;
		this.flags = flags;
		if ((flags & FLAG_EDITOR_APPEARANCE_COLOR_OPTIONS) != 0) {
			fColorListModel = createColorListModel();
		} else {
			fColorListModel = null;
		}
		getPreferenceStore().addKeys(
				createOverlayStoreKeys(flags, fColorListModel));
	}

	private static OverlayPreferenceStore.OverlayKey[] createOverlayStoreKeys(
			int flags, EditorColorItem[] colorItems) {
		ArrayList<OverlayKey> keys = new ArrayList<OverlayKey>();
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
		if ((flags & FLAG_EDITOR_APPEARANCE_COLOR_OPTIONS) != 0) {
			keys.add(new OverlayPreferenceStore.OverlayKey(
					OverlayPreferenceStore.BOOLEAN,
					PreferenceConstants.EDITOR_MATCHING_BRACKETS));
			// keys.add(new OverlayPreferenceStore.OverlayKey(
			// OverlayPreferenceStore.BOOLEAN,
			// PreferenceConstants.EDITOR_QUICKASSIST_LIGHTBULB));
			// keys.add(new OverlayPreferenceStore.OverlayKey(
			// OverlayPreferenceStore.BOOLEAN,
			// PreferenceConstants.EDITOR_SHOW_SEGMENTS));
			if (colorItems != null) {
				for (EditorColorItem item : colorItems) {
					keys.add(new OverlayPreferenceStore.OverlayKey(
							OverlayPreferenceStore.STRING, item.colorKey));
					if (item.systemDefaultKey != null) {
						keys.add(new OverlayPreferenceStore.OverlayKey(
								OverlayPreferenceStore.BOOLEAN,
								item.systemDefaultKey));
					}
				}
			}
		}

		return keys.toArray(new OverlayPreferenceStore.OverlayKey[keys.size()]);
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

		if ((flags & FLAG_EDITOR_APPEARANCE_COLOR_OPTIONS) != 0) {
			createAppearanceOptionsGroup(control);
		}

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

	private Control createAppearanceOptionsGroup(Composite composite) {
		Label spacer = new Label(composite, SWT.LEFT);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		gd.heightHint = convertHeightInCharsToPixels(1) / 2;
		spacer.setLayoutData(gd);

		addCheckBox(composite,
				PreferencesMessages.EditorPreferencePage_matchingBrackets,
				PreferenceConstants.EDITOR_MATCHING_BRACKETS, 0);

		// Button showSegsB = addCheckBox(composite,
		// PreferencesMessages.EditorPreferencePage_showSegments,
		// PreferenceConstants.EDITOR_SHOW_SEGMENTS, 0);
		// showSegsB.setEnabled(false);

		Label l = new Label(composite, SWT.LEFT);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		gd.heightHint = convertHeightInCharsToPixels(1) / 2;
		l.setLayoutData(gd);

		l = new Label(composite, SWT.LEFT);
		l.setText(PreferencesMessages.EditorPreferencePage_title1);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		l.setLayoutData(gd);

		Composite editorComposite = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		editorComposite.setLayout(layout);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.FILL_VERTICAL);
		gd.horizontalSpan = 2;
		editorComposite.setLayoutData(gd);

		colorList = new List(editorComposite, SWT.SINGLE | SWT.V_SCROLL
				| SWT.BORDER);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
				| GridData.FILL_HORIZONTAL);
		gd.heightHint = convertHeightInCharsToPixels(12);
		colorList.setLayoutData(gd);

		Composite stylesComposite = new Composite(editorComposite, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		stylesComposite.setLayout(layout);
		stylesComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		l = new Label(stylesComposite, SWT.LEFT);
		l.setText(PreferencesMessages.EditorPreferencePage_color);
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		l.setLayoutData(gd);

		colorEditor = new ColorSelector(stylesComposite);
		Button foregroundColorButton = colorEditor.getButton();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = GridData.BEGINNING;
		foregroundColorButton.setLayoutData(gd);

		SelectionListener colorDefaultSelectionListener = new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				boolean systemDefault = colorDefault.getSelection();
				colorEditor.getButton().setEnabled(!systemDefault);

				int i = colorList.getSelectionIndex();
				if (i == -1)
					return;

				String key = fColorListModel[i].systemDefaultKey;
				if (key != null)
					getPreferenceStore().setValue(key, systemDefault);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};

		colorDefault = new Button(stylesComposite, SWT.CHECK);
		colorDefault
				.setText(PreferencesMessages.EditorPreferencePage_systemDefault);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.horizontalSpan = 2;
		colorDefault.setLayoutData(gd);
		colorDefault.setVisible(false);
		colorDefault.addSelectionListener(colorDefaultSelectionListener);

		colorList.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				handleAppearanceColorListSelection();
			}
		});
		foregroundColorButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				int i = colorList.getSelectionIndex();
				if (i == -1)
					return;

				String key = fColorListModel[i].colorKey;
				PreferenceConverter.setValue(getPreferenceStore(), key,
						colorEditor.getColorValue());
			}
		});

		return composite;
	}

	private void handleAppearanceColorListSelection() {
		int i = colorList.getSelectionIndex();
		if (i == -1)
			return;
		String key = fColorListModel[i].colorKey;
		RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), key);
		colorEditor.setColorValue(rgb);
		updateAppearanceColorWidgets(fColorListModel[i].systemDefaultKey);
	}

	private void updateAppearanceColorWidgets(String systemDefaultKey) {
		if (systemDefaultKey == null) {
			colorDefault.setSelection(false);
			colorDefault.setVisible(false);
			colorEditor.getButton().setEnabled(true);
		} else {
			boolean systemDefault = getPreferenceStore().getBoolean(
					systemDefaultKey);
			colorDefault.setSelection(systemDefault);
			colorDefault.setVisible(true);
			colorEditor.getButton().setEnabled(!systemDefault);
		}
	}

	@Override
	public void initialize() {

		super.initialize();

		if ((flags & FLAG_EDITOR_APPEARANCE_COLOR_OPTIONS) != 0) {
			initializeDefaultColors();

			for (int i = 0; i < fColorListModel.length; i++)
				colorList.add(fColorListModel[i].name);

			colorList.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (colorList != null && !colorList.isDisposed()) {
						colorList.select(0);
						handleAppearanceColorListSelection();
					}
				}
			});
		}

	}

	private void initializeDefaultColors() {
		for (EditorColorItem item : fColorListModel) {
			if (item.systemDefaultKey != null
					&& getPreferenceStore().getBoolean(item.systemDefaultKey)) {
				RGB rgb = colorList.getDisplay().getSystemColor(
						item.systemColor).getRGB();
				PreferenceConverter.setValue(getPreferenceStore(),
						item.colorKey, rgb);
			}
		}
	}

	@Override
	public void performDefaults() {
		super.performDefaults();
		if ((flags & FLAG_EDITOR_APPEARANCE_COLOR_OPTIONS) != 0) {
			initializeDefaultColors();
			handleAppearanceColorListSelection();
		}
	}
}
