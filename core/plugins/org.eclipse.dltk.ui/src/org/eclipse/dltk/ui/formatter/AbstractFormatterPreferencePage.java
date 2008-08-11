/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKContributionExtensionManager;
import org.eclipse.dltk.core.IDLTKContributedExtension;
import org.eclipse.dltk.core.IPreferencesSaveDelegate;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.internal.ui.preferences.ScriptSourcePreviewerUpdater;
import org.eclipse.dltk.internal.ui.text.DLTKColorManager;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.formatter.preferences.IFormatterDialogOwner;
import org.eclipse.dltk.ui.formatter.preferences.IFormatterModifyDialog;
import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPropertyAndPreferencePage;
import org.eclipse.dltk.ui.preferences.AbstractOptionsBlock;
import org.eclipse.dltk.ui.preferences.ComboViewerBlock;
import org.eclipse.dltk.ui.preferences.ContributedExtensionOptionsBlock;
import org.eclipse.dltk.ui.preferences.PreferenceKey;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class AbstractFormatterPreferencePage extends
		AbstractConfigurationBlockPropertyAndPreferencePage {

	protected class FormatterSelectionBlock extends
			ContributedExtensionOptionsBlock implements IFormatterDialogOwner {

		private IColorManager fColorManager;
		private ProjectionViewer fPreviewViewer;

		public FormatterSelectionBlock(IStatusChangeListener context,
				IProject project, IWorkbenchPreferenceContainer container) {
			super(context, project, collectPreferenceKeys(), container);
			fColorManager = new DLTKColorManager(false);
		}

		public void dispose() {
			fColorManager.dispose();
			super.dispose();
		}

		protected DLTKContributionExtensionManager getExtensionManager() {
			return ScriptFormatterManager.getInstance();
		}

		protected int getSelectorGroupColumns() {
			return 3;
		}

		protected ComboViewerBlock createComboViewerBlock(Composite group) {
			ComboViewerBlock result = super.createComboViewerBlock(group);
			SWTFactory.createPushButton(group,
					FormatterMessages.FormatterPreferencePage_edit, null)
					.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							editButtonPressed();
						}
					});
			return result;
		}

		protected void editButtonPressed() {
			IScriptFormatterFactory factory = (IScriptFormatterFactory) getSelectedExtension();
			if (factory != null) {
				final IFormatterModifyDialog dialog = factory
						.createDialog(this);
				if (dialog != null) {
					final Map oldPrefs = getFormatterPrefs(factory);
					dialog.setPreferences(oldPrefs);
					if (dialog.open() == Window.OK) {
						final Map preferences = dialog.getPreferences();
						if (!preferences.equals(oldPrefs)) {
							factory.savePreferences(preferences,
									new SaveDelegate((Map) prefKeys
											.get(factory)));
							formatterPrefs.put(factory, preferences);
							updatePreview(factory);
						}
					}
				}
			}
		}

		private class SaveDelegate implements IPreferencesSaveDelegate {

			final Map keyMap;

			SaveDelegate(Map keyMap) {
				this.keyMap = keyMap;
			}

			private PreferenceKey getPrefKey(String key) {
				return (PreferenceKey) keyMap.get(key);
			}

			public void setBoolean(String qualifier, String key, boolean value) {
				PreferenceKey pKey = getPrefKey(key);
				if (pKey != null) {
					setValue(pKey, value);
				}
			}

			public void setInt(String qualifier, String key, int value) {
				PreferenceKey pKey = getPrefKey(key);
				if (pKey != null) {
					setValue(pKey, String.valueOf(value));
				}
			}

			public void setString(String qualifier, String key, String value) {
				PreferenceKey pKey = getPrefKey(key);
				if (pKey != null) {
					setValue(pKey, value);
				}
			}

		}

		protected void saveChanges(IPreferencesSaveDelegate delegate) {
			IScriptFormatterFactory factory = (IScriptFormatterFactory) getSelectedExtension();
			if (factory != null && formatterPrefs.containsKey(factory)) {
				Map prefs = (Map) formatterPrefs.get(factory);
				factory.savePreferences(prefs, delegate);
			}
		}

		/*
		 * increase visibility
		 */
		public Shell getShell() {
			return super.getShell();
		}

		protected String getSelectorGroupLabel() {
			return FormatterMessages.FormatterPreferencePage_groupName;
		}

		protected String getSelectorNameLabel() {
			return FormatterMessages.FormatterPreferencePage_selectionLabel;
		}

		protected void createSelectorBlock(Composite composite) {
			super.createSelectorBlock(composite);
			Composite previewGroup = SWTFactory.createGroup(composite,
					FormatterMessages.FormatterPreferencePage_preview, 1, 1,
					GridData.FILL_BOTH);
			fPreviewViewer = createPreview(previewGroup);
		}

		/**
		 * @param composite
		 */
		public ProjectionViewer createPreview(Composite composite) {
			IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
			IPreferenceStore store = new ChainedPreferenceStore(
					new IPreferenceStore[] { getPreferenceStore(),
							generalTextStore });
			ProjectionViewer viewer = createPreviewViewer(composite, null,
					null, false, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER,
					store);
			if (viewer == null) {
				return null;
			}
			ScriptSourceViewerConfiguration configuration = createSimpleSourceViewerConfiguration(
					fColorManager, store, null, false);
			viewer.configure(configuration);
			if (viewer.getTextWidget().getTabs() == 0) {
				viewer.getTextWidget().setTabs(4);
			}
			new ScriptSourcePreviewerUpdater(viewer, configuration, store);
			viewer.setEditable(false);
			IDocument document = new Document();
			IDLTKUILanguageToolkit toolkit = DLTKUILanguageManager
					.getLanguageToolkit(getNatureId());
			toolkit.getTextTools().setupDocumentPartitioner(document,
					toolkit.getPartitioningId());
			viewer.setDocument(document);
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.widthHint = convertWidthInCharsToPixels(20);
			gd.heightHint = convertHeightInCharsToPixels(5);
			viewer.getControl().setLayoutData(gd);
			return viewer;
		}

		/**
		 * @param parent
		 * @param verticalRuler
		 * @param overviewRuler
		 * @param showAnnotationsOverview
		 * @param styles
		 * @param store
		 * @return
		 */
		private ProjectionViewer createPreviewViewer(Composite parent,
				IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
				boolean showAnnotationsOverview, int styles,
				IPreferenceStore store) {
			return new ScriptSourceViewer(parent, verticalRuler, overviewRuler,
					showAnnotationsOverview, styles, store);
		}

		protected String getPreferenceLinkMessage() {
			return FormatterMessages.FormatterPreferencePage_settingsLink;
		}

		protected PreferenceKey getSavedContributionKey() {
			return AbstractFormatterPreferencePage.this
					.getFormatterPreferenceKey();
		}

		protected String getNatureId() {
			return AbstractFormatterPreferencePage.this.getNatureId();
		}

		protected void selectionChanged(IDLTKContributedExtension extension) {
			super.selectionChanged(extension);
			if (fPreviewViewer != null) {
				IScriptFormatterFactory formatterFactory = (IScriptFormatterFactory) extension;
				updatePreview(formatterFactory);
			}
		}

		private void updatePreview(IScriptFormatterFactory formatterFactory) {
			String content = formatterFactory.getPreviewContent();
			if (content != null) {
				fPreviewViewer.getControl().setEnabled(true);
				// TODO change background to white
				IScriptFormatter formatter = formatterFactory.createFormatter(
						Util.LINE_SEPARATOR,
						getFormatterPrefs(formatterFactory));
				try {
					TextEdit textEdit = formatter.format(content, 0, content
							.length(), 0);
					if (textEdit != null) {
						IDocument document = new Document(content);
						textEdit.apply(document);
						content = document.get();
					}
				} catch (BadLocationException e) {
					DLTKUIPlugin.log(e);
				}
				fPreviewViewer.getDocument().set(content);
			} else {
				fPreviewViewer.getControl().setEnabled(false);
				// TODO change background to gray
				fPreviewViewer.getDocument().set(Util.EMPTY_STRING);
			}
		}

		private final Map formatterPrefs = new IdentityHashMap();

		/**
		 * @param formatterFactory
		 * @return
		 */
		private Map getFormatterPrefs(IScriptFormatterFactory formatterFactory) {
			Map prefs = (Map) formatterPrefs.get(formatterFactory);
			if (prefs == null) {
				prefs = formatterFactory
						.retrievePreferences(new PreferencesLookupDelegate(
								getProject()));
				if (prefs != null) {
					formatterPrefs.put(formatterFactory, prefs);
				} else {
					prefs = Collections.EMPTY_MAP;
				}
			}
			return prefs;
		}

	}

	protected AbstractOptionsBlock createOptionsBlock(
			IStatusChangeListener newStatusChangedListener, IProject project,
			IWorkbenchPreferenceContainer container) {
		return new FormatterSelectionBlock(newStatusChangedListener, project,
				container);
	}

	protected PreferenceKey[] collectPreferenceKeys() {
		List result = new ArrayList();
		result.add(getFormatterPreferenceKey());
		IDLTKContributedExtension[] extensions = ScriptFormatterManager
				.getInstance().getContributions(getNatureId());
		for (int i = 0; i < extensions.length; ++i) {
			IScriptFormatterFactory factory = (IScriptFormatterFactory) extensions[i];
			final String qualifier = factory.getPreferenceQualifier();
			final String[] keys = factory.getPreferenceKeys();
			if (qualifier != null && keys != null) {
				final Map keyMap = new HashMap();
				for (int j = 0; j < keys.length; ++j) {
					final PreferenceKey prefKey = new PreferenceKey(qualifier,
							keys[j]);
					keyMap.put(keys[j], prefKey);
					result.add(prefKey);
				}
				prefKeys.put(factory, keyMap);
			}
		}
		return (PreferenceKey[]) result
				.toArray(new PreferenceKey[result.size()]);
	}

	private final Map prefKeys = new HashMap();

	/**
	 * @param colorManager
	 * @param store
	 * @param object
	 * @param b
	 * @return
	 */
	protected abstract ScriptSourceViewerConfiguration createSimpleSourceViewerConfiguration(
			IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, boolean configureFormatter);

	protected abstract String getNatureId();

	protected abstract PreferenceKey getFormatterPreferenceKey();

	protected String getHelpId() {
		return null;
	}

	protected void setDescription() {
		// empty
	}

	protected String getPreferencePageId() {
		return null;
	}

	protected String getProjectHelpId() {
		return null;
	}

	protected String getPropertyPageId() {
		return null;
	}

}
