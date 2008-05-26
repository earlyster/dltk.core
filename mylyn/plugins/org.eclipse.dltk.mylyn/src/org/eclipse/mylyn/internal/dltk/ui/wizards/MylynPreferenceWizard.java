/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.dltk.ui.wizards;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.TaskContextWorkingSetPage;
import org.eclipse.mylyn.internal.dltk.MylynDLTKPlugin;
import org.eclipse.mylyn.internal.dltk.MylynDLTKPrefConstants;
import org.eclipse.mylyn.internal.dltk.ui.DLTKUiUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.dialogs.IWorkingSetNewWizard;

public class MylynPreferenceWizard extends Wizard implements INewWizard {

	private MylynPreferenceWizardPage preferencePage;

	public static final String MYLYN_FIRST_RUN = "org.eclipse.dltk.mylyn.ui.first.run";

	private IPreferenceStore scriptPrefs = DLTKUIPlugin.getDefault()
			.getPreferenceStore();

	public void init() {
		// setDefaultPageImageDescriptor(ContextUiImages.MYLYN);
		setWindowTitle("Mylyn Recommended Preferences");
		super.setDefaultPageImageDescriptor(MylynDLTKPlugin
				.imageDescriptorFromPlugin(MylynDLTKPlugin.PLUGIN_ID,
						"icons/wizban/banner-prefs.gif"));
		preferencePage = new MylynPreferenceWizardPage(
				"Automatic preference settings");
	}

	public MylynPreferenceWizard() {
		super();
		init();
	}

	public MylynPreferenceWizard(String htmlDocs) {
		super();
		init();
	}

	public boolean performFinish() {
		setPreferences();
		if (preferencePage.isOpenTaskList()) {
			// TaskListView.openInActivePerspective();
		}
		return true;
	}

	private void setPreferences() {
		boolean mylarContentAssist = preferencePage
				.isMylynContentAssistDefault();
		DLTKUiUtil.installContentAssist(scriptPrefs, mylarContentAssist);

		if (preferencePage.isAutoFolding()) {
			ContextUiPlugin.getDefault().getPreferenceStore().setValue(
					MylynDLTKPrefConstants.ACTIVE_FOLDING_ENABLED, true);
			scriptPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED,
					true);
			// javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_PROVIDER,
			// DEFAULT_FOLDING_PROVIDER);
		} else {
			ContextUiPlugin.getDefault().getPreferenceStore().setValue(
					MylynDLTKPrefConstants.ACTIVE_FOLDING_ENABLED, false);
		}

		if (preferencePage.closeEditors()) {
			ContextUiPlugin.getDefault().getPreferenceStore().setValue(
					MylynDLTKPrefConstants.AUTO_MANAGE_EDITORS, true);
		} else {
			ContextUiPlugin.getDefault().getPreferenceStore().setValue(
					MylynDLTKPrefConstants.AUTO_MANAGE_EDITORS, false);
		}

		if (preferencePage.isCreateWorkingSet()) {
			IWorkingSetManager workingSetManager = ContextUiPlugin.getDefault()
					.getWorkbench().getWorkingSetManager();
			IWorkingSetNewWizard wizard = workingSetManager
					.createWorkingSetNewWizard(new String[] { "org.eclipse.mylyn.workingSetPage" });
			if (wizard != null
					&& workingSetManager
							.getWorkingSet(TaskContextWorkingSetPage.WORKING_SET_NAME) == null) {
				WizardDialog dialog = new WizardDialog(Display.getCurrent()
						.getActiveShell(), wizard);
				dialog.create();
				if (dialog.open() == Window.OK) {
					IWorkingSet workingSet = wizard.getSelection();
					if (workingSet != null) {
						workingSetManager.addWorkingSet(workingSet);
					}
				}
			}
		} else {
			IWorkingSetManager workingSetManager = ContextUiPlugin.getDefault()
					.getWorkbench().getWorkingSetManager();
			IWorkingSet workingSet = workingSetManager
					.getWorkingSet(TaskContextWorkingSetPage.WORKING_SET_NAME);
			if (workingSet != null) {
				workingSetManager.removeWorkingSet(workingSet);
			}
		}
	}

	public void addPages() {
		addPage(preferencePage);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

}
