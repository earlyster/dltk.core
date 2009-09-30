/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.wizards;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.EnvironmentChangedListener;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IEnvironmentChangedListener;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.corext.util.Messages;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.launching.ScriptRuntime.DefaultInterpreterEntry;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.dialogs.ControlStatus;
import org.eclipse.dltk.ui.dialogs.StatusInfo;
import org.eclipse.dltk.ui.environment.IEnvironmentUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * The first page of the <code>SimpleProjectWizard</code>.
 */
public abstract class ProjectWizardFirstPage extends WizardPage implements
		ILocationGroup {

	/**
	 * Request a project name. Fires an event whenever the text field is
	 * changed, regardless of its content.
	 */
	public final class NameGroup extends Observable implements
			IDialogFieldListener {
		protected final StringDialogField fNameField;

		public NameGroup(Composite composite, String initialName) {
			final Composite nameComposite = new Composite(composite, SWT.NONE);
			nameComposite.setFont(composite.getFont());
			nameComposite.setLayout(initGridLayout(new GridLayout(2, false),
					false));
			nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			// text field for project name
			fNameField = new StringDialogField();
			fNameField
					.setLabelText(NewWizardMessages.ScriptProjectWizardFirstPage_NameGroup_label_text);
			fNameField.setDialogFieldListener(this);
			setName(initialName);
			fNameField.doFillIntoGrid(nameComposite, 2);
			LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		public String getName() {
			return fNameField.getText().trim();
		}

		public void postSetFocus() {
			fNameField.postSetFocusOnDialogField(getShell().getDisplay());
		}

		public void setName(String name) {
			fNameField.setText(name);
		}

		public void dialogFieldChanged(DialogField field) {
			fireEvent();
		}
	}

	/**
	 * Request a location. Fires an event whenever the checkbox or the location
	 * field is changed, regardless of whether the change originates from the
	 * user or has been invoked programmatically.
	 */
	public class LocationGroup extends Observable implements Observer,
			IStringButtonAdapter, IDialogFieldListener {
		protected final SelectionButtonDialogField fWorkspaceRadio;
		protected final SelectionButtonDialogField fExternalRadio;
		protected final StringButtonDialogField fLocation;
		protected final ComboDialogField fEnvironment;
		private IEnvironment[] environments;

		private String fPreviousExternalLocation;
		private static final String DIALOGSTORE_LAST_EXTERNAL_LOC = DLTKUIPlugin.PLUGIN_ID
				+ ".last.external.project"; //$NON-NLS-1$
		private static final String DIALOGSTORE_LAST_EXTERNAL_ENVIRONMENT = DLTKUIPlugin.PLUGIN_ID
				+ ".last.external.environment"; //$NON-NLS-1$

		public LocationGroup() {
			fPreviousExternalLocation = Util.EMPTY_STRING;

			fWorkspaceRadio = new SelectionButtonDialogField(SWT.RADIO);
			fWorkspaceRadio.setDialogFieldListener(this);
			fWorkspaceRadio
					.setLabelText(NewWizardMessages.ScriptProjectWizardFirstPage_LocationGroup_workspace_desc);

			fExternalRadio = new SelectionButtonDialogField(SWT.RADIO);
			fExternalRadio.setDialogFieldListener(this);
			fExternalRadio
					.setLabelText(NewWizardMessages.ScriptProjectWizardFirstPage_LocationGroup_external_desc);

			fLocation = new StringButtonDialogField(this);
			fLocation.setDialogFieldListener(this);
			fLocation
					.setLabelText(NewWizardMessages.ScriptProjectWizardFirstPage_LocationGroup_locationLabel_desc);
			fLocation
					.setButtonLabel(NewWizardMessages.ScriptProjectWizardFirstPage_LocationGroup_browseButton_desc);

			fEnvironment = new ComboDialogField(SWT.DROP_DOWN | SWT.READ_ONLY);
			fEnvironment
					.setLabelText(NewWizardMessages.ProjectWizardFirstPage_host);
			fEnvironment.setDialogFieldListener(this);
			fEnvironment.setDialogFieldListener(new IDialogFieldListener() {
				public void dialogFieldChanged(DialogField field) {
					updateInterpreters();
				}
			});
		}

		public void createControls(Composite composite) {
			final int numColumns = 3;
			final Group group = new Group(composite, SWT.NONE);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			group.setLayout(initGridLayout(new GridLayout(numColumns, false),
					true));
			group
					.setText(NewWizardMessages.ScriptProjectWizardFirstPage_LocationGroup_title);
			createControls(group, numColumns);
		}

		protected void createControls(Group group, int numColumns) {
			fWorkspaceRadio.doFillIntoGrid(group, numColumns);
			fExternalRadio.doFillIntoGrid(group, numColumns);
			fLocation.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fLocation.getTextControl(null));
			environmentChangedListener = new EnvironmentChangedListener() {
				public void environmentsModified() {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							try {
								initEnvironments(false);
							} catch (Exception e) {
								if (DLTKCore.DEBUG) {
									e.printStackTrace();
								}
							}
						}
					});
				}
			};
			EnvironmentManager
					.addEnvironmentChangedListener(environmentChangedListener);
			initEnvironments(true);
			fEnvironment.doFillIntoGrid(group, numColumns);
			LayoutUtil
					.setHorizontalGrabbing(fEnvironment.getComboControl(null));

			fWorkspaceRadio.setSelection(true);
			fExternalRadio.setSelection(false);
		}

		private IEnvironmentChangedListener environmentChangedListener = null;

		private void initEnvironments(boolean initializeWithLocal) {
			final IEnvironment selection;
			if (!initializeWithLocal && environments != null) {
				final int index = fEnvironment.getSelectionIndex();
				if (index >= 0 && index < environments.length) {
					selection = environments[index];
				} else {
					selection = null;
				}
			} else {
				selection = null;
			}
			environments = EnvironmentManager.getEnvironments(false);
			final String[] items = new String[environments.length];
			int selectionIndex = 0;
			for (int i = 0; i < items.length; i++) {
				final IEnvironment env = environments[i];
				items[i] = env.getName();
				if (selection == null ? env.isLocal() : selection.equals(env)) {
					selectionIndex = i;
				}
			}
			fEnvironment.setItems(items);
			fEnvironment.selectItem(selectionIndex);
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		private void updateInterpreters() {
			handlePossibleInterpreterChange();
		}

		protected String getDefaultPath(String name) {
			IEnvironment environment = this.getEnvironment();
			if (environment != null && environment.isLocal()) {
				final IPath path = Platform.getLocation().append(name);
				return path.toOSString();
			} else {
				return ""; //$NON-NLS-1$
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 * java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			if (isInWorkspace()) {
				fLocation.setText(getDefaultPath(fNameGroup.getName()));
			}
			fireEvent();
		}

		public IPath getLocation() {
			if (isInWorkspace()) {
				return Platform.getLocation();
			}
			return new Path(fLocation.getText().trim());
		}

		public boolean isInWorkspace() {
			return fWorkspaceRadio.isSelected();
		}

		public boolean isExternalProject() {
			return fExternalRadio.isSelected();
		}

		public IEnvironment getEnvironment() {
			if (isInWorkspace()) {
				return EnvironmentManager.getLocalEnvironment();
			} else {
				final int index = fEnvironment.getSelectionIndex();
				if (index >= 0 && index < environments.length) {
					return environments[index];
				} else {
					return EnvironmentManager.getLocalEnvironment();
				}
			}
		}

		public void changeControlPressed(DialogField field) {
			final IEnvironment environment = getEnvironment();
			final IEnvironmentUI environmentUI = (IEnvironmentUI) environment
					.getAdapter(IEnvironmentUI.class);
			if (environmentUI != null) {
				String directoryName = fLocation.getText().trim();
				if (directoryName.length() == 0) {
					final IDialogSettings dSettings = DLTKUIPlugin.getDefault()
							.getDialogSettings();
					final String savedEnvId = dSettings
							.get(DIALOGSTORE_LAST_EXTERNAL_ENVIRONMENT);
					if (savedEnvId == null
							|| savedEnvId.equals(environment.getId())) {
						final String prevLocation = dSettings
								.get(DIALOGSTORE_LAST_EXTERNAL_LOC);
						if (prevLocation != null) {
							directoryName = prevLocation;
						}
					}
				}
				final String selectedDirectory = environmentUI.selectFolder(
						getShell(), directoryName);

				if (selectedDirectory != null) {
					final IDialogSettings dSettings = DLTKUIPlugin.getDefault()
							.getDialogSettings();
					fLocation.setText(selectedDirectory);
					dSettings.put(DIALOGSTORE_LAST_EXTERNAL_LOC,
							selectedDirectory);
					dSettings.put(DIALOGSTORE_LAST_EXTERNAL_ENVIRONMENT,
							environment.getId());
				}
			}
		}

		protected static final int ANY = 0;
		protected static final int WORKSPACE = 1;
		protected static final int EXTERNAL = 2;

		protected boolean isModeField(DialogField field, int kind) {
			switch (kind) {
			case ANY:
				return field == fWorkspaceRadio || field == fExternalRadio;
			case WORKSPACE:
				return field == fWorkspaceRadio;
			case EXTERNAL:
				return field == fExternalRadio;
			default:
				return false;
			}
		}

		public void dialogFieldChanged(DialogField field) {
			if (isModeField(field, ANY)) {
				final boolean external = isExternalProject();
				fLocation.setEnabled(external);
				fEnvironment.setEnabled(external);
			}
			if (isModeField(field, WORKSPACE)) {
				final boolean checked = isInWorkspace();
				if (checked) {
					fPreviousExternalLocation = fLocation.getText();
					fLocation.setText(getDefaultPath(fNameGroup.getName()));
				} else {
					IEnvironment environment = this.getEnvironment();
					if (environment != null && environment.isLocal()) {
						fLocation.setText(fPreviousExternalLocation);
					} else {
						fLocation.setText(""); //$NON-NLS-1$
					}
				}
				updateInterpreters();
			}

			if (isModeField(field, EXTERNAL)) {
				updateInterpreters();
			}

			fireEvent();
		}

		protected void dispose() {
			if (environmentChangedListener != null) {
				EnvironmentManager
						.removeEnvironmentChangedListener(environmentChangedListener);
				environmentChangedListener = null;
			}
		}

		public IStatus validate(IProject handle) {
			final String location = getLocation().toOSString();
			// check whether location is empty
			if (location.length() == 0) {
				return new StatusInfo(
						IStatus.WARNING,
						NewWizardMessages.ScriptProjectWizardFirstPage_Message_enterLocation);
			}
			// check whether the location is a syntactically correct path
			if (!Path.EMPTY.isValidPath(location)) {
				return new StatusInfo(
						IStatus.ERROR,
						NewWizardMessages.ScriptProjectWizardFirstPage_Message_invalidDirectory);
			}
			final IPath projectPath = Path.fromOSString(location);
			final IEnvironment environment = getEnvironment();
			// check whether the location has the workspace as prefix
			if (!isInWorkspace() && environment.isLocal()
					&& Platform.getLocation().isPrefixOf(projectPath)) {
				return new StatusInfo(
						IStatus.ERROR,
						NewWizardMessages.ScriptProjectWizardFirstPage_Message_cannotCreateInWorkspace);
			}
			if (!isInWorkspace() && environment.isLocal()) {
				// If we do not place the contents in the workspace validate the
				// location.
				final IStatus locationStatus = DLTKUIPlugin.getWorkspace()
						.validateProjectLocation(handle, projectPath);
				if (!locationStatus.isOK()) {
					return new StatusInfo(IStatus.ERROR, locationStatus
							.getMessage());
				}
			}
			return Status.OK_STATUS;
		}

	}

	protected interface IInterpreterGroup {

		/**
		 * @return
		 */
		IEnvironment getEnvironment();

		/**
		 * @return
		 */
		IInterpreterInstall getSelectedInterpreter();

		/**
		 * 
		 */
		void handlePossibleInterpreterChange();

		/**
		 * @since 2.0
		 */
		boolean isInterpreterPresent();

		/**
		 * Returns the control to be decorated if error occurs
		 * 
		 * @since 2.0
		 */
		Control getDecorationTarget();

	}

	protected abstract class AbstractInterpreterGroup extends Observable
			implements Observer, SelectionListener, IDialogFieldListener,
			IInterpreterGroup {

		protected final ComboDialogField fInterpreterEnvironment;
		protected final SelectionButtonDialogField fUseDefaultInterpreter;
		protected final SelectionButtonDialogField fUseProjectInterpreter;
		protected final ComboDialogField fInterpreterCombo;
		private final Group fGroup;
		private String[] fComplianceLabels;
		private final Link fPreferenceLink;
		private IInterpreterInstall[] fInstalledInterpreters;
		private boolean interpretersPresent;

		public AbstractInterpreterGroup(Composite composite) {
			fGroup = new Group(composite, SWT.NONE);
			fGroup.setFont(composite.getFont());
			fGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			final GridLayout groupLayout = initGridLayout(new GridLayout(3,
					false), true);
			groupLayout.marginHeight /= 2;
			fGroup.setLayout(groupLayout);
			fGroup
					.setText(NewWizardMessages.ScriptProjectWizardFirstPage_InterpreterEnvironmentGroup_title);

			if (isTargetEnvironmentAllowed()) {
				final Composite interpreterEnvironmentComposite = new Composite(
						fGroup, SWT.NONE);
				interpreterEnvironmentComposite.setLayout(initGridLayout(
						new GridLayout(2, false), false));
				final GridData interpreterEnvironmentData = new GridData(
						GridData.FILL_HORIZONTAL);
				interpreterEnvironmentData.horizontalSpan = 3;
				interpreterEnvironmentComposite
						.setLayoutData(interpreterEnvironmentData);
				fInterpreterEnvironment = new ComboDialogField(SWT.READ_ONLY
						| SWT.BORDER);
				fInterpreterEnvironment
						.setLabelText(NewWizardMessages.ProjectWizardFirstPage_InterpreterTargetEnvironment);
				fInterpreterEnvironment.doFillIntoGrid(
						interpreterEnvironmentComposite, 2);
				LayoutUtil.setHorizontalGrabbing(fInterpreterEnvironment
						.getComboControl(null));
				fInterpreterEnvironment.setDialogFieldListener(this);
			} else {
				fInterpreterEnvironment = null;
			}

			fUseDefaultInterpreter = new SelectionButtonDialogField(SWT.RADIO);
			fUseDefaultInterpreter.setLabelText(getDefaultInterpreterLabel());
			fUseDefaultInterpreter.doFillIntoGrid(fGroup, 2);
			fUseDefaultInterpreter.setDialogFieldListener(this);

			fPreferenceLink = new Link(fGroup, SWT.NONE);
			fPreferenceLink.setFont(fGroup.getFont());
			fPreferenceLink
					.setText(NewWizardMessages.ScriptProjectWizardFirstPage_InterpreterEnvironmentGroup_link_description);
			fPreferenceLink.setLayoutData(new GridData(GridData.END,
					GridData.CENTER, false, false));
			fPreferenceLink.addSelectionListener(this);

			fUseProjectInterpreter = new SelectionButtonDialogField(SWT.RADIO);
			fUseProjectInterpreter
					.setLabelText(NewWizardMessages.ScriptProjectWizardFirstPage_InterpreterEnvironmentGroup_specific_compliance);
			fUseProjectInterpreter.doFillIntoGrid(fGroup, 1);
			fUseProjectInterpreter.setDialogFieldListener(this);

			fInterpreterCombo = new ComboDialogField(SWT.READ_ONLY);
			fillInstalledInterpreters(fInterpreterCombo);
			fInterpreterCombo.setDialogFieldListener(this);

			Combo comboControl = fInterpreterCombo.getComboControl(fGroup);
			GridData gridData = new GridData(GridData.BEGINNING,
					GridData.CENTER, true, false);
			gridData.minimumWidth = 100;
			comboControl.setLayoutData(gridData); // make sure column 2 is
			// grabing (but no fill)
			comboControl.setVisibleItemCount(20);

			// DialogField.createEmptySpace(fGroup);

			fUseDefaultInterpreter.setSelection(true);
			fInterpreterCombo.setEnabled(fUseProjectInterpreter.isSelected());
		}

		public IEnvironment getEnvironment() {
			if (isTargetEnvironmentAllowed() && fLocationGroup.isInWorkspace()
					&& fEnvironments != null) {
				final int index = fInterpreterEnvironment.getSelectionIndex();
				if (index >= 0 && index < fEnvironments.length) {
					return fEnvironments[index];
				}
			}
			return fLocationGroup.getEnvironment();
		}

		private void fillInstalledInterpreters(ComboDialogField comboField) {
			String selectedItem = null;
			int selectionIndex = -1;
			if (fUseProjectInterpreter.isSelected()) {
				selectionIndex = comboField.getSelectionIndex();
				if (selectionIndex != -1) {// paranoia
					selectedItem = comboField.getItems()[selectionIndex];
				}
			}

			fInstalledInterpreters = getWorkspaceInterpeters();

			selectionIndex = -1;// find new index
			fComplianceLabels = new String[fInstalledInterpreters.length];
			for (int i = 0; i < fInstalledInterpreters.length; i++) {
				fComplianceLabels[i] = fInstalledInterpreters[i].getName();
				if (selectedItem != null
						&& fComplianceLabels[i].equals(selectedItem)) {
					selectionIndex = i;
				}
			}
			comboField.setItems(fComplianceLabels);
			if (selectionIndex == -1) {
				fInterpreterCombo.selectItem(getDefaultInterpreterName());
			} else {
				fInterpreterCombo.selectItem(selectedItem);
			}
			interpretersPresent = (fInstalledInterpreters.length > 0);
		}

		private IInterpreterInstall[] getWorkspaceInterpeters() {
			List<IInterpreterInstall> standins = new ArrayList<IInterpreterInstall>();
			IInterpreterInstallType[] types = ScriptRuntime
					.getInterpreterInstallTypes(getCurrentLanguageNature());
			IEnvironment environment = getEnvironment();
			for (int i = 0; i < types.length; i++) {
				IInterpreterInstallType type = types[i];
				IInterpreterInstall[] installs = type.getInterpreterInstalls();
				for (int j = 0; j < installs.length; j++) {
					IInterpreterInstall install = installs[j];
					String envId = install.getEnvironmentId();
					if (envId != null && envId.equals(environment.getId())) {
						standins.add(new InterpreterStandin(install));
					}
				}
			}
			return standins.toArray(new IInterpreterInstall[standins.size()]);
		}

		private String getDefaultInterpreterName() {
			IInterpreterInstall inst = ScriptRuntime
					.getDefaultInterpreterInstall(new DefaultInterpreterEntry(
							getCurrentLanguageNature(), getEnvironment()
									.getId()));
			if (inst != null)
				return inst.getName();
			else
				return "undefined"; //$NON-NLS-1$
		}

		private String getDefaultInterpreterLabel() {
			return Messages
					.format(
							NewWizardMessages.ScriptProjectWizardFirstPage_InterpreterEnvironmentGroup_default_compliance,
							getDefaultInterpreterName());
		}

		public void update(Observable o, Object arg) {
			updateEnableState();
		}

		private void updateEnableState() {
			if (fDetectGroup == null)
				return;
			final boolean detect = fDetectGroup.mustDetect()
					&& interpretersPresent;
			fUseDefaultInterpreter.setEnabled(!detect);
			fUseProjectInterpreter.setEnabled(!detect);
			fInterpreterCombo.setEnabled(!detect
					&& fUseProjectInterpreter.isSelected());
			fPreferenceLink.setEnabled(!detect);
			fGroup.setEnabled(!detect);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
		 * .swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
		}

		/**
		 * Shows window with appropriate language preference page.
		 * 
		 */
		void showInterpreterPreferencePage() {
			final String pageId = getIntereprtersPreferencePageId();

			PreferencesUtil.createPreferenceDialogOn(getShell(), pageId,
					new String[] { pageId }, null).open();
		}

		protected abstract String getIntereprtersPreferencePageId();

		protected final String getCurrentLanguageNature() {
			return getScriptNature();
		}

		protected boolean isTargetEnvironmentAllowed() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org
		 * .eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			showInterpreterPreferencePage();
			handlePossibleInterpreterChange();
			updateEnableState();
			// fDetectGroup.handlePossibleJInterpreterChange();
		}

		private IEnvironment[] fEnvironments = null;

		public void handlePossibleInterpreterChange() {
			if (isTargetEnvironmentAllowed()) {
				refreshEnvironments();
			}
			refreshInterpreters();
		}

		private void refreshInterpreters() {
			fUseDefaultInterpreter.setLabelText(getDefaultInterpreterLabel());
			fillInstalledInterpreters(fInterpreterCombo);
			setChanged();
			notifyObservers();
		}

		private void refreshEnvironments() {
			final IEnvironment selection;
			if (fLocationGroup.isExternalProject()) {
				selection = fLocationGroup.getEnvironment();
			} else {
				final int index = fInterpreterEnvironment.getSelectionIndex();
				if (index >= 0 && index < fEnvironments.length) {
					selection = fEnvironments[index];
				} else {
					selection = fLocationGroup.getEnvironment();
				}
			}
			final IEnvironment[] environments = fLocationGroup.environments;
			if (!Arrays.equals(environments, fEnvironments)) {
				fEnvironments = environments;
				final String[] items = new String[environments.length];
				int selectionIndex = -1;
				for (int i = 0; i < items.length; i++) {
					final IEnvironment env = environments[i];
					items[i] = env.getName();
					if (selection != null && selection.equals(env)) {
						selectionIndex = i;
					}
				}
				fInterpreterEnvironment.setItems(items);
				fInterpreterEnvironment.selectItem(selectionIndex);
			} else if (selection != null) {
				for (int i = 0; i < environments.length; i++) {
					if (selection.equals(environments[i])) {
						fInterpreterEnvironment.selectItem(i);
					}
				}
			}
			fInterpreterEnvironment.setEnabled(fLocationGroup.isInWorkspace());
		}

		public void dialogFieldChanged(DialogField field) {
			if (field == fInterpreterEnvironment) {
				if (isTargetEnvironmentAllowed()) {
					refreshInterpreters();
				}
			} else if (field == fUseDefaultInterpreter
					|| field == fUseProjectInterpreter) {
				setChanged();
				notifyObservers();
			}
			updateEnableState();
			// fDetectGroup.handlePossibleInterpreterChange();
		}

		public boolean isUseSpecific() {
			return fUseProjectInterpreter.isSelected();
		}

		public IInterpreterInstall getSelectedInterpreter() {
			if (fUseProjectInterpreter.isSelected()) {
				int index = fInterpreterCombo.getSelectionIndex();
				if (index >= 0 && index < fComplianceLabels.length) { // paranoia
					return fInstalledInterpreters[index];
				}
			}
			return null;
		}

		/**
		 * @since 2.0
		 */
		public boolean isInterpreterPresent() {
			return interpretersPresent;
		}

		/**
		 * @since 2.0
		 */
		public Control getDecorationTarget() {
			if (fUseDefaultInterpreter.isSelected()) {
				return fUseDefaultInterpreter.getSelectionButton();
			} else {
				return fUseProjectInterpreter.getSelectionButton();
			}
		}
	}

	/**
	 * @since 2.0
	 */
	protected enum DefaultInterpreterGroupOption {
		NO_TARGET_ENVIRONMENT
	}

	/**
	 * @since 2.0
	 */
	protected class DefaultInterpreterGroup extends AbstractInterpreterGroup {

		private final String interpreterPreferencePage;
		private final List<DefaultInterpreterGroupOption> options;

		/**
		 * @param composite
		 */
		public DefaultInterpreterGroup(Composite composite,
				String interpreterPreferencePage,
				DefaultInterpreterGroupOption... options) {
			super(composite);
			this.interpreterPreferencePage = interpreterPreferencePage;
			this.options = Arrays.asList(options);
		}

		@Override
		protected String getIntereprtersPreferencePageId() {
			return interpreterPreferencePage;
		}

		@Override
		protected boolean isTargetEnvironmentAllowed() {
			if (options
					.contains(DefaultInterpreterGroupOption.NO_TARGET_ENVIRONMENT)) {
				return false;
			}
			return super.isTargetEnvironmentAllowed();
		}

	}

	/**
	 * Show a warning when the project location contains files.
	 */
	protected final class DetectGroup extends Observable implements Observer,
			SelectionListener {
		private final Link fHintText;
		private boolean fDetect;

		public DetectGroup(Composite composite) {
			fHintText = new Link(composite, SWT.WRAP);
			fHintText.setFont(composite.getFont());
			fHintText.addSelectionListener(this);
			GridData gd = new GridData(GridData.FILL, SWT.FILL, true, true);
			gd.widthHint = convertWidthInCharsToPixels(50);
			fHintText.setLayoutData(gd);
			if (supportInterpreter()) {
				handlePossibleInterpreterChange();
			}
		}

		private boolean isValidProjectName(String name) {
			if (name.length() == 0) {
				return false;
			}
			final IWorkspace workspace = DLTKUIPlugin.getWorkspace();
			return workspace.validateName(name, IResource.PROJECT).isOK()
					&& workspace.getRoot().findMember(name) == null;
		}

		public void update(Observable o, Object arg) {
			if (o instanceof LocationGroup) {
				boolean oldDetectState = fDetect;
				IPath location = fLocationGroup.getLocation();
				if (fLocationGroup.isInWorkspace()) {
					if (!isValidProjectName(getProjectName())) {
						fDetect = false;
					} else {
						IEnvironment environment = fLocationGroup
								.getEnvironment();
						final IFileHandle directory = environment
								.getFile(location.append(getProjectName()));
						fDetect = directory.isDirectory();
					}
				} else {
					IEnvironment environment = fLocationGroup.getEnvironment();
					if (location.toPortableString().length() > 0) {
						final IFileHandle directory = environment
								.getFile(location);
						fDetect = directory.isDirectory();
					}
				}
				if (oldDetectState != fDetect) {
					setChanged();
					notifyObservers();
					if (fDetect) {
						fHintText.setVisible(true);
						fHintText
								.setText(NewWizardMessages.ScriptProjectWizardFirstPage_DetectGroup_message);
					} else {
						fHintText.setVisible(false);
					}
					if (supportInterpreter()) {
						handlePossibleInterpreterChange();
					}

				}
			}
		}

		public boolean mustDetect() {
			return fDetect;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
		 * .swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org
		 * .eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			if (DLTKCore.DEBUG) {
				System.err
						.println("DetectGroup show compilancePreferencePage..."); //$NON-NLS-1$
			}
			if (supportInterpreter()) {
				handlePossibleInterpreterChange();
			}
		}
	}

	/**
	 * Validates project fields. Returns {@link IStatus} or <code>null</code> if
	 * there are no any problems.
	 * 
	 * @return
	 */
	protected IStatus validateProject() {
		final String name = fNameGroup.getName();
		// check whether the project name field is empty
		if (name.length() == 0) {
			return new StatusInfo(
					IStatus.OK,
					NewWizardMessages.ScriptProjectWizardFirstPage_Message_enterProjectName);
		}
		// check whether the project name is valid
		final IStatus nameStatus = DLTKUIPlugin.getWorkspace().validateName(
				name, IResource.PROJECT);
		if (!nameStatus.isOK()) {
			return nameStatus;
		}
		// check whether project already exists
		final IProject handle = getProjectHandle();
		if (handle.exists()) {
			return new StatusInfo(
					IStatus.ERROR,
					NewWizardMessages.ScriptProjectWizardFirstPage_Message_projectAlreadyExists);
		}
		return null;
	}

	/**
	 * Tests if valid project is specified.
	 * 
	 * @return
	 */
	protected boolean isValidProject() {
		return validateProject() == null;
	}

	private ControlDecorationManager fDecorationManager;

	/**
	 * Validate this page and show appropriate warnings and error
	 * NewWizardMessages.
	 */
	private final class Validator implements Observer {
		public void update(Observable o, Object arg) {
			final IControlDecorationManager manager = fDecorationManager
					.beginReporting();
			try {
				validate(manager, o, arg);
			} finally {
				manager.commit();
			}
		}

		private void validate(IControlDecorationManager decorations,
				Observable o, Object arg) {
			IStatus projectStatus = validateProject();
			if (projectStatus == null) {
				decorations.hide(fNameGroup.fNameField.getTextControl());
				projectStatus = fLocationGroup.validate(getProjectHandle());
				if (projectStatus.isOK()) {
					projectStatus = null;
				}
				if (projectStatus != null) {
					if (projectStatus instanceof ControlStatus) {
						final ControlStatus cStatus = (ControlStatus) projectStatus;
						decorations.show(cStatus.getControl(), cStatus);
					} else {
						decorations.show(fLocationGroup.fLocation
								.getTextControl(), projectStatus);
					}
				}
			} else {
				decorations.show(fNameGroup.fNameField.getTextControl(),
						projectStatus);
			}
			if (projectStatus != null) {
				if (projectStatus.getSeverity() != IStatus.ERROR) {
					setErrorMessage(null);
					setMessage(projectStatus.getMessage());
				} else {
					setErrorMessage(projectStatus.getMessage());
				}
				setPageComplete(false);
				return;
			}
			if (supportInterpreter() && interpeterRequired()) {
				if (!fInterpreterGroup.isInterpreterPresent()) {
					setErrorMessage(NewWizardMessages.ProjectWizardFirstPage_atLeastOneInterpreterMustBeConfigured);
					setPageComplete(false);
					decorations
							.show(
									fInterpreterGroup.getDecorationTarget(),
									new StatusInfo(
											IStatus.ERROR,
											NewWizardMessages.ProjectWizardFirstPage_atLeastOneInterpreterMustBeConfigured));
					return;
				}
			}
			setPageComplete(true);
			setErrorMessage(null);
			setMessage(null);
		}
	}

	protected NameGroup fNameGroup;
	protected LocationGroup fLocationGroup;
	// private LayoutGroup fLayoutGroup;
	// private InterpreterEnvironmentGroup fInterpreterEnvironmentGroup;
	protected DetectGroup fDetectGroup;
	private Validator fValidator;
	protected String fInitialName;
	private static final String PAGE_NAME = NewWizardMessages.ScriptProjectWizardFirstPage_page_pageName;

	/**
	 * Create a new <code>SimpleProjectFirstPage</code>.
	 */
	public ProjectWizardFirstPage() {
		super(PAGE_NAME);
		setPageComplete(false);
		setTitle(NewWizardMessages.ScriptProjectWizardFirstPage_page_title);
		setDescription(NewWizardMessages.ScriptProjectWizardFirstPage_page_description);
		fInitialName = ""; //$NON-NLS-1$
	}

	public void setName(String name) {
		fInitialName = name;
		if (fNameGroup != null) {
			fNameGroup.setName(name);
		}
	}

	/**
	 * Return true if some interpreters are available for selection.
	 * 
	 * @return true if interpreters are available for selection
	 */
	public boolean isInterpretersPresent() {
		return fInterpreterGroup.isInterpreterPresent();
	}

	/**
	 * @since 2.0
	 */
	public abstract String getScriptNature();

	protected abstract boolean interpeterRequired();

	protected boolean supportInterpreter() {
		return true;
	}

	protected abstract IInterpreterGroup createInterpreterGroup(Composite parent);

	protected IInterpreterGroup getInterpreterGroup() {
		return fInterpreterGroup;
	}

	protected void handlePossibleInterpreterChange() {
		if (fInterpreterGroup != null) {
			fInterpreterGroup.handlePossibleInterpreterChange();
		}
	}

	protected Observable getInterpreterGroupObservable() {
		if (fInterpreterGroup instanceof Observable) {
			return (Observable) fInterpreterGroup;
		} else {
			return null;
		}
	}

	protected IInterpreterInstall getInterpreter() {
		return fInterpreterGroup != null ? fInterpreterGroup
				.getSelectedInterpreter() : null;
	}

	protected IEnvironment getInterpreterEnvironment() {
		return fInterpreterGroup.getEnvironment();
	}

	public IInterpreterInstall getSelectedInterpreter() {
		return getInterpreter();
	}

	private IInterpreterGroup fInterpreterGroup;

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		// create UI elements
		fNameGroup = new NameGroup(composite, fInitialName);
		fLocationGroup = createLocationGroup();
		fLocationGroup.createControls(composite);

		// fInterpreterEnvironmentGroup= new
		// InterpreterEnvironmentGroup(composite);
		// ProjectWizardFirstPage.AbstractInterpreterGroup interpGroup = null;
		if (supportInterpreter()) {
			fInterpreterGroup = createInterpreterGroup(composite);
		} else {
			fInterpreterGroup = null;
		}
		createCustomGroups(composite);
		// fLayoutGroup= new LayoutGroup(composite);
		fDetectGroup = new DetectGroup(composite);
		// establish connections
		fNameGroup.addObserver(fLocationGroup);
		// fDetectGroup.addObserver(fLayoutGroup);
		// fDetectGroup.addObserver(fInterpreterEnvironmentGroup);

		fLocationGroup.addObserver(fDetectGroup);
		fLocationGroup.addObserver(locationObservers);
		// initialize all elements
		fNameGroup.notifyObservers();
		// create and connect validator
		fValidator = new Validator();
		fDecorationManager = new ControlDecorationManager();
		Observable interpreterGroupObservable = getInterpreterGroupObservable();
		if (supportInterpreter() && interpreterGroupObservable != null) {
			// fDetectGroup.addObserver(getInterpreterGroupObservable());
			interpreterGroupObservable.addObserver(fValidator);
			handlePossibleInterpreterChange();
		}
		fNameGroup.addObserver(fValidator);
		fLocationGroup.addObserver(fValidator);

		setControl(composite);
		Dialog.applyDialogFont(composite);
		if (DLTKCore.DEBUG) {
			System.err.println("Add help support here..."); //$NON-NLS-1$
		}
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
		// IDLTKHelpContextIds.NEW_JAVAPROJECT_WIZARD_PAGE);
	}

	protected LocationGroup createLocationGroup() {
		return new LocationGroup();
	}

	protected void createCustomGroups(Composite composite) {
	}

	/**
	 * Returns the current project location path as entered by the user, or its
	 * anticipated initial value. Note that if the default has been returned the
	 * path in a project description used to create a project should not be set.
	 * <p>
	 * TODO At some point this method has to be converted to return an URI
	 * instead of an path. However, this first requires support from Platform/UI
	 * to specify a project location different than in a local file system.
	 * </p>
	 * 
	 * @return the project location path or its anticipated initial value.
	 */
	public URI getLocationURI() {
		IEnvironment environment = getEnvironment();
		return environment.getURI(fLocationGroup.getLocation());
	}

	public IEnvironment getEnvironment() {
		return fLocationGroup.getEnvironment();
	}

	public IPath getLocation() {
		return fLocationGroup.getLocation();
	}

	private class LocationObservers implements Observer {
		final ListenerList observers = new ListenerList();

		public void update(Observable o, Object arg) {
			final Object[] listeners = observers.getListeners();
			for (int i = 0; i < listeners.length; ++i) {
				((ILocationGroup.Listener) listeners[i]).update(
						ProjectWizardFirstPage.this, arg);
			}
		}

	}

	private LocationObservers locationObservers = new LocationObservers();

	public void addLocationListener(ILocationGroup.Listener listener) {
		locationObservers.observers.add(listener);
	}

	/**
	 * Creates a project resource handle for the current project name field
	 * value.
	 * <p>
	 * This method does not create the project resource; this is the
	 * responsibility of <code>IProject::create</code> invoked by the new
	 * project resource wizard.
	 * </p>
	 * 
	 * @return the new project resource handle
	 */
	public IProject getProjectHandle() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(
				fNameGroup.getName());
	}

	public boolean isInWorkspace() {
		return fLocationGroup.isInWorkspace();
	}

	public String getProjectName() {
		return fNameGroup.getName();
	}

	public boolean getDetect() {
		return fDetectGroup.mustDetect();
	}

	public boolean isSrc() {
		return false;
		// return true;//fLayoutGroup.isSrcBin();
	}

	/*
	 * see @DialogPage.setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			fNameGroup.postSetFocus();
		}
	}

	/*
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public void dispose() {
		fDecorationManager.dispose();
		fLocationGroup.dispose();
		super.dispose();
	}

	/**
	 * Initialize a grid layout with the default Dialog settings.
	 */
	protected GridLayout initGridLayout(GridLayout layout, boolean margins) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins) {
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		return layout;
	}

	/**
	 * Set the layout data for a button.
	 */
	protected GridData setButtonLayoutData(Button button) {
		return super.setButtonLayoutData(button);
	}

}
