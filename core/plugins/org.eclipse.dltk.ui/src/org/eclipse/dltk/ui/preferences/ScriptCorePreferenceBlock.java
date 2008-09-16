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
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

final class ScriptCorePreferenceBlock extends
		ImprovedAbstractConfigurationBlock {
	private Button nonLocalEmptyFileContentTypeChecking;
	private Button filesWithExtensionsContentChecking;

	private final class ReindexOperation implements IRunnableWithProgress {
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
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

	public Control createControl(Composite parent) {
		Composite composite = SWTFactory.createComposite(parent, parent
				.getFont(), 1, 1, GridData.FILL_BOTH);

		Group coreGroup = SWTFactory.createGroup(composite,
				Messages.ScriptCorePreferenceBlock_coreOptions, 1, 1,
				GridData.FILL_HORIZONTAL);

		this.nonLocalEmptyFileContentTypeChecking = SWTFactory
				.createCheckButton(
						coreGroup,
						Messages.ScriptCorePreferenceBlock_emptyFileContentCheckingForNonLocalProjects);

		this.filesWithExtensionsContentChecking = SWTFactory
				.createCheckButton(
						coreGroup,
						Messages.ScriptCorePreferenceBlock_filesWithExtensionContentCheking);

		Group editorGroup = SWTFactory.createGroup(composite,
				Messages.ScriptCorePreferenceBlock_editOptions, 1, 1,
				GridData.FILL_HORIZONTAL);

		bindControl(
				SWTFactory
						.createCheckButton(
								editorGroup,
								PreferencesMessages.EditorPreferencePage_evaluateTemporaryProblems),
				PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS);

		createReIndex(composite);

		return composite;
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
		return overlayKeys;
	}

	public void initialize() {
		super.initialize();
		Preferences preferences = DLTKCore.getPlugin().getPluginPreferences();
		String value = preferences
				.getString(DLTKCore.CORE_NON_LOCAL_EMPTY_FILE_CONTENT_TYPE_CHECKING);
		this.nonLocalEmptyFileContentTypeChecking.setSelection(DLTKCore.ENABLED
				.equals(value));

		value = preferences
				.getString(DLTKCore.CORE_FILES_WITH_EXTENSION_CONTENT_CHECKING);
		this.filesWithExtensionsContentChecking.setSelection(DLTKCore.ENABLED
				.equals(value));
	}

	public void performDefaults() {
		super.performDefaults();
		Preferences preferences = DLTKCore.getPlugin().getPluginPreferences();
		String value = preferences
				.getDefaultString(DLTKCore.CORE_NON_LOCAL_EMPTY_FILE_CONTENT_TYPE_CHECKING);
		this.nonLocalEmptyFileContentTypeChecking.setSelection(DLTKCore.ENABLED
				.equals(value));
		value = preferences
				.getDefaultString(DLTKCore.CORE_FILES_WITH_EXTENSION_CONTENT_CHECKING);
		this.filesWithExtensionsContentChecking.setSelection(DLTKCore.ENABLED
				.equals(value));
	}

	protected void initializeFields() {
		super.initializeFields();
	}

	public void performOk() {
		super.performOk();
		Preferences preferences = DLTKCore.getPlugin().getPluginPreferences();
		preferences
				.setValue(
						DLTKCore.CORE_NON_LOCAL_EMPTY_FILE_CONTENT_TYPE_CHECKING,
						this.nonLocalEmptyFileContentTypeChecking
								.getSelection() ? DLTKCore.ENABLED
								: DLTKCore.DISABLED);
		preferences
				.setValue(
						DLTKCore.CORE_FILES_WITH_EXTENSION_CONTENT_CHECKING,
						this.filesWithExtensionsContentChecking.getSelection() ? DLTKCore.ENABLED
								: DLTKCore.DISABLED);
		DLTKCore.getDefault().savePluginPreferences();
	}

}