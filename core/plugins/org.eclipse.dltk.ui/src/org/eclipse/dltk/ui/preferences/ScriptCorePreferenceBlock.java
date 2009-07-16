/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.eclipse.dltk.ui.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

final class ScriptCorePreferenceBlock extends
		ImprovedAbstractConfigurationBlock {

	private final class ReindexOperation implements IRunnableWithProgress {
		public void run(IProgressMonitor monitor) {
			try {
				ResourcesPlugin.getWorkspace().build(
						IncrementalProjectBuilder.FULL_BUILD, monitor);
			} catch (CoreException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	ScriptCorePreferenceBlock(OverlayPreferenceStore store, PreferencePage page) {
		super(store, page);
	}

	private static FileCacheEntry[] getFileCaches() {
		final List result = new ArrayList();
		final String fileCacheExtPoint = DLTKCore.PLUGIN_ID + ".fileCache"; //$NON-NLS-1$
		final IConfigurationElement[] elements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						fileCacheExtPoint);
		for (int i = 0; i < elements.length; ++i) {
			final IConfigurationElement element = elements[i];
			final String id = element.getAttribute("id"); //$NON-NLS-1$
			final String name = element.getAttribute("name"); //$NON-NLS-1$
			if (id != null && name != null) {
				result.add(new FileCacheEntry(id, name));
			}
		}
		return (FileCacheEntry[]) result.toArray(new FileCacheEntry[result
				.size()]);
	}

	final FileCacheEntry[] cacheEntries = getFileCaches();

	private static class FileCacheEntry {
		final String id;
		final String name;

		FileCacheEntry(String id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	private Combo cacheCombo;

	public Control createControl(Composite parent) {
		Composite composite = SWTFactory.createComposite(parent, parent
				.getFont(), 1, 1, GridData.FILL_BOTH);

		Group coreGroup = SWTFactory.createGroup(composite,
				Messages.ScriptCorePreferenceBlock_coreOptions, 2, 1,
				GridData.FILL_HORIZONTAL);
		SWTFactory.createLabel(coreGroup,
				Messages.ScriptCorePreferenceBlock_fileCaching, 1);
		final String[] items = new String[cacheEntries.length];
		for (int i = 0; i < cacheEntries.length; ++i) {
			items[i] = cacheEntries[i].name;
		}
		cacheCombo = SWTFactory.createCombo(coreGroup, SWT.READ_ONLY
				| SWT.BORDER, 0, items);

		Group editorGroup = SWTFactory.createGroup(composite,
				Messages.ScriptCorePreferenceBlock_editOptions, 2, 1,
				GridData.FILL_HORIZONTAL);

		bindControl(
				SWTFactory
						.createCheckButton(
								editorGroup,
								PreferencesMessages.EditorPreferencePage_evaluateTemporaryProblems,
								2),
				PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS);
		// Connection timeout
		SWTFactory.createLabel(editorGroup,
				"Codeassist completion timeout(ms):", 1);
		final Text connectionTimeout = SWTFactory.createText(editorGroup,
				SWT.BORDER, 1, ""); //$NON-NLS-1$
		bindControl(connectionTimeout, PreferenceConstants.CODEASSIST_TIMEOUT,
				FieldValidators.POSITIVE_NUMBER_VALIDATOR);

		Group uiGroup = SWTFactory.createGroup(composite,
				Messages.ScriptCorePreferenceBlock_UI_Options, 1, 1,
				GridData.FILL_HORIZONTAL);

		bindControl(
				SWTFactory
						.createCheckButton(
								uiGroup,
								Messages.EditorPreferencePage_ResourceShowError_InvalidResourceName),
				PreferenceConstants.RESOURCE_SHOW_ERROR_INVALID_RESOURCE_NAME);

		createReIndex(composite);

		return composite;
	}

	private void initializeCacheField(String cacheId) {
		for (int i = 0; i < cacheEntries.length; ++i) {
			if (cacheId != null && cacheId.equals(cacheEntries[i].id)) {
				cacheCombo.select(i);
				break;
			}
		}
	}

	private void createReIndex(Composite composite) {
		if (DLTKCore.SHOW_REINDEX) {
			Group g = SWTFactory.createGroup(composite,
					Messages.ScriptCorePreferenceBlock_debugOptionsOperations,
					2, 1, GridData.FILL_HORIZONTAL);

			Label l = new Label(g, SWT.PUSH);
			l.setText(Messages.ScriptCorePreferencePage_manualReindex);
			Button reCreateIndex = new Button(g, SWT.PUSH);
			reCreateIndex.setText(Messages.ScriptCorePreferencePage_reindex);
			reCreateIndex.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					IndexManager indexManager = ModelManager.getModelManager()
							.getIndexManager();
					indexManager.rebuild();

					try {
						PlatformUI.getWorkbench().getProgressService().run(
								false, true, new ReindexOperation());
					} catch (InvocationTargetException e3) {
						if (DLTKCore.DEBUG) {
							e3.printStackTrace();
						}
					} catch (InterruptedException e3) {
						if (DLTKCore.DEBUG) {
							e3.printStackTrace();
						}
					}
				}
			});
		}
	}

	protected List createOverlayKeys() {
		ArrayList overlayKeys = new ArrayList();
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(
				OverlayPreferenceStore.BOOLEAN,
				PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(
				OverlayPreferenceStore.BOOLEAN,
				PreferenceConstants.RESOURCE_SHOW_ERROR_INVALID_RESOURCE_NAME));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(
				OverlayPreferenceStore.INT,
				PreferenceConstants.CODEASSIST_TIMEOUT));
		return overlayKeys;
	}

	public void initialize() {
		super.initialize();
		initializeCacheField(DLTKCore.getPlugin().getPluginPreferences()
				.getString(DLTKCore.FILE_CACHE));
	}

	public void performDefaults() {
		super.performDefaults();
		initializeCacheField(DLTKCore.getPlugin().getPluginPreferences()
				.getDefaultString(DLTKCore.FILE_CACHE));
	}

	protected void initializeFields() {
		super.initializeFields();
	}

	public void performOk() {
		super.performOk();
		final int cacheIndex = cacheCombo.getSelectionIndex();
		if (cacheIndex >= 0 && cacheIndex < cacheEntries.length) {
			final Preferences prefs = DLTKCore.getDefault()
					.getPluginPreferences();
			final String value = cacheEntries[cacheIndex].id;
			if (!value.equals(prefs.getString(DLTKCore.FILE_CACHE))) {
				prefs.setValue(DLTKCore.FILE_CACHE, value);
			}
		}
		DLTKCore.getDefault().savePluginPreferences();
	}

}
