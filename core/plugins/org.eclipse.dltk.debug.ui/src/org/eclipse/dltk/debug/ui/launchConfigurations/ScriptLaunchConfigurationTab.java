package org.eclipse.dltk.debug.ui.launchConfigurations;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.core.ScriptModelHelper;
import org.eclipse.dltk.debug.ui.messages.DLTKLaunchConfigurationsMessages;
import org.eclipse.dltk.debug.ui.preferences.ScriptDebugPreferencesMessages;
import org.eclipse.dltk.internal.corext.util.Messages;
import org.eclipse.dltk.internal.launching.DLTKLaunchingPlugin;
import org.eclipse.dltk.internal.launching.LaunchConfigurationUtils;
import org.eclipse.dltk.internal.launching.LaunchConfigurationUtils.ILaunchConfigDefaultBooleanProvider;
import org.eclipse.dltk.internal.ui.DLTKUIStatus;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 */
public abstract class ScriptLaunchConfigurationTab extends
		AbstractLaunchConfigurationTab {
	protected static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private String fMode;
	// this options only active then mode is ILaunchManager.DEBUG
	private Button breakOnFirstLine;
	private Button enableLogging;

	private Button fProjButton;
	private Text fProjText;

	private WidgetListener fListener = new WidgetListener();

	public ScriptLaunchConfigurationTab(String mode) {
		this.fMode = mode;
	}

	/*
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse
	 * .swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		comp.setLayout(topLayout);

		createProjectEditor(comp);
		createVerticalSpacer(comp, 1);

		doCreateControl(comp);
		createVerticalSpacer(comp, 1);

		createDebugOptionsGroup(comp);

		createCustomSections(comp);
		Dialog.applyDialogFont(comp);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IScriptDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
	}

	/**
	 * Create some custom user sections.
	 */
	protected void createCustomSections(Composite comp) {
	}

	/**
	 * Creates the widgets for specifying a main type.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	protected void createDebugOptionsGroup(Composite parent) {
		if (ILaunchManager.DEBUG_MODE.equals(fMode)) {
			String text = DLTKLaunchConfigurationsMessages.ScriptLaunchConfigurationTab_debugOptions;
			Group group = new Group(parent, SWT.NONE);
			group.setText(text);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			if (parent.getLayout() instanceof GridLayout) {
				gd.horizontalSpan = ((GridLayout) parent.getLayout()).numColumns;
			}
			group.setLayoutData(gd);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			group.setLayout(layout);

			addBreakOnFirstLineButton(group);
			addDbgpLoggingButton(group);
		}
	}

	/*
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse
	 * .debug.core.ILaunchConfiguration)
	 */
	public final void initializeFrom(ILaunchConfiguration config) {
		updateProjectFromConfig(config);
		doInitializeForm(config);
	}

	/*
	 * @see
	 * org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse
	 * .debug.core.ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration launchConfig) {
		validatePage();
		return !isError();
	}

	/**
	 * This is a top level method to initiate the page validation.
	 */
	protected final void validatePage() {
		setErrorMessage(null);
		setMessage(null);
		validate();
	}

	/**
	 * Validates the page. This method should be overridden when more checks are
	 * needed.
	 * 
	 * @return <code>true</code> if input is correct and <code>false</code>
	 *         otherwise
	 */
	protected boolean validate() {
		return validateProject();
	}

	protected boolean isError() {
		return getErrorMessage() != null;
	}

	/*
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse
	 * .debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public final void performApply(ILaunchConfigurationWorkingCopy config) {
		String project = fProjText.getText().trim();
		config.setAttribute(
				ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, project);

		if (ILaunchManager.DEBUG_MODE.equals(fMode)) {
			if (breakOnFirstLine != null)
				config
						.setAttribute(
								ScriptLaunchConfigurationConstants.ENABLE_BREAK_ON_FIRST_LINE,
								breakOnFirstLine.getSelection());
			if (enableLogging != null)
				config.setAttribute(
						ScriptLaunchConfigurationConstants.ENABLE_DBGP_LOGGING,
						enableLogging.getSelection());
		}

		doPerformApply(config);
	}

	/*
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.
	 * debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// do nothing
	}

	/**
	 * Returns the current preference value for the 'break on first line'
	 * setting
	 */
	protected abstract boolean breakOnFirstLinePrefEnabled(
			PreferencesLookupDelegate delegate);

	/**
	 * Returns the current preference value for the 'Dbgp logging enabled'
	 * setting
	 */
	protected abstract boolean dbpgLoggingPrefEnabled(
			PreferencesLookupDelegate delegate);

	/**
	 * @deprecated not used anymore
	 * @return
	 */
	protected final boolean doCanSave() {
		return false;
	}

	/**
	 * Creates the sub-class specific control.
	 * 
	 * <p>
	 * Sub-classes can widgets directly to the <code>composite</code> object
	 * that is passed to them.
	 * </p>
	 * 
	 * @param composite
	 *            control composite
	 * 
	 * @see #createControl(Composite)
	 */
	protected abstract void doCreateControl(Composite composite);

	/**
	 * Performs the sub-class specific configuration tab initialization.
	 * 
	 * @param config
	 *            launch configuration
	 * 
	 * @see #initializeFrom(ILaunchConfiguration)
	 */
	protected abstract void doInitializeForm(ILaunchConfiguration config);

	protected abstract void doPerformApply(
			ILaunchConfigurationWorkingCopy config);

	protected abstract String getNatureID();

	/**
	 * Validate the specified toolkit is valid for the launch configuration
	 * 
	 * @param toolkit
	 *            language toolkit
	 * 
	 * @return <code>true</code> if the toolkit is valid for the launch
	 *         configuration, <code>false</code> otherwise
	 * @deprecated
	 */
	protected final boolean isValidToolkit(IDLTKLanguageToolkit toolkit) {
		/*
		 * This method is not used anymore - the test in validateProject() is
		 * now performed comparing natureId.
		 */
		return false;
	}

	/**
	 * Add the 'break on first line' option to a group composite.
	 * 
	 * <p>
	 * Sub-classes are responsible for adding this option to a group composite
	 * of their choosing.
	 * </p>
	 * 
	 * @param group
	 *            group composite
	 */
	private void addBreakOnFirstLineButton(Composite group) {
		breakOnFirstLine = createCheckButton(group,
				ScriptDebugPreferencesMessages.BreakOnFirstLineLabel);
		breakOnFirstLine.addSelectionListener(getWidgetListener());
		createVerticalSpacer(group, 1);
	}

	/**
	 * Add the 'Dbgp logging enabled' option to a group composite.
	 * 
	 * <p>
	 * Sub-classes are responsible for adding this option to a group composite
	 * of their choosing.
	 * </p>
	 * 
	 * @param group
	 *            group composite
	 */
	private void addDbgpLoggingButton(Composite group) {
		enableLogging = createCheckButton(group,
				ScriptDebugPreferencesMessages.EnableDbgpLoggingLabel);
		enableLogging.addSelectionListener(getWidgetListener());
		createVerticalSpacer(group, 1);
	}

	/**
	 * chooses a project for the type of launch config that it is
	 * 
	 * @return
	 */
	protected IScriptProject chooseProject() {
		final ILabelProvider labelProvider = DLTKUILanguageManager
				.createLabelProvider(getNatureID());
		final ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				getShell(), labelProvider);
		dialog
				.setTitle(DLTKLaunchConfigurationsMessages.mainTab_chooseProject_title);
		dialog
				.setMessage(DLTKLaunchConfigurationsMessages.mainTab_chooseProject_message);

		try {
			final IScriptProject[] projects = ScriptModelHelper
					.getOpenedScriptProjects(DLTKCore
							.create(getWorkspaceRoot()), getNatureID());
			dialog.setElements(projects);
		} catch (ModelException e) {
			DLTKLaunchingPlugin.log(e);
		}

		final IScriptProject project = getProject();
		if (project != null) {
			dialog.setInitialSelections(new Object[] { project });
		}

		if (dialog.open() == Window.OK) {
			return (IScriptProject) dialog.getFirstResult();
		}

		return null;
	}

	protected boolean needGroupForField(String fieldName) {
		return true;
	}

	/**
	 * @param parent
	 * @param fieldName
	 * @param text
	 */
	protected Label createLabelForField(Composite parent, String fieldName,
			String text) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	/**
	 * @param enabled
	 */
	protected void setEnableProjectField(boolean enabled) {
		fProjText.setEnabled(enabled);
		fProjButton.setEnabled(enabled);
	}

	protected static final String FIELD_PROJECT = "project"; //$NON-NLS-1$

	/**
	 * Creates a project editor
	 * 
	 * <p>
	 * Creates a group containing an input text field and 'Browse' button to
	 * select a project from the workspace.
	 * </p>
	 * 
	 * @param parent
	 *            the parent composite
	 */
	protected void createProjectEditor(Composite parent) {
		final Composite editParent;
		if (needGroupForField(FIELD_PROJECT)) {
			Group group = new Group(parent, SWT.NONE);
			group
					.setText(DLTKLaunchConfigurationsMessages.mainTab_projectGroup);

			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);

			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			group.setLayout(layout);
			editParent = group;
		} else {
			createLabelForField(parent, FIELD_PROJECT,
					DLTKLaunchConfigurationsMessages.mainTab_projectGroup);
			editParent = parent;
		}
		fProjText = new Text(editParent, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fProjText.setLayoutData(gd);
		fProjText.addModifyListener(fListener);

		fProjButton = createPushButton(editParent,
				DLTKLaunchConfigurationsMessages.mainTab_projectButton, null);
		fProjButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleProjectButtonSelected();
			}
		});
	}

	protected String getLanguageName() {
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(getNatureID());
		if (toolkit != null) {
			return toolkit.getLanguageName();
		}
		return null;
	}

	/**
	 * Return the IScriptProject corresponding to the project name in the
	 * project name text field, or null if the text does not match a project
	 * name.
	 */
	protected IScriptProject getProject() {
		if (fProjText == null) {
			return null;
		}

		String projectName = fProjText.getText().trim();
		if (projectName.length() < 1) {
			return null;
		} // end if

		return getScriptModel().getScriptProject(projectName);
	}

	/**
	 * Attemps to guess the current project and script being launched.
	 * 
	 * <p>
	 * If the project and script are able to be determined, the string array
	 * returned will contain the project name in position 0 and the script name
	 * in position 1.
	 * </p>
	 * 
	 * @return project name and script name as string array, or
	 *         <code>null</code> if they could not be determined.
	 */
	protected String[] getProjectAndScriptNames() {
		IWorkbenchPage page = DLTKUIPlugin.getActivePage();
		if (page == null) {
			return null;
		}

		IEditorPart editor = page.getActiveEditor();
		if (editor == null) {
			return null;
		}

		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput == null) {
			return null;
		}

		IModelElement me = DLTKUIPlugin.getEditorInputModelElement(editorInput);
		if (me != null) {
			IScriptProject project = me.getScriptProject();

			if ((project != null) && validateProject(project)) {
				String projectName = project.getProject().getName();

				/*
				 * TODO: validate script is an executable and not library/module
				 * otherwise, return null and make user select
				 */
				IResource resource = me.getResource();
				if (resource != null) {
					String scriptName = resource.getProjectRelativePath()
							.toPortableString();
					// me.getResource().getLocation().toPortableString();
					// /*me.getResource().getFullPath().toPortableString();*/

					return new String[] { projectName, scriptName };
				}
			}
		}

		return null;
	}

	protected final String getProjectName() {
		return fProjText.getText().trim();
	}

	/**
	 * Convenience method to get access to thescriptmodel.
	 */
	protected IScriptModel getScriptModel() {
		return DLTKCore.create(getWorkspaceRoot());
	}

	protected WidgetListener getWidgetListener() {
		return fListener;
	}

	/**
	 * Convenience method to get the workspace root.
	 */
	protected IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	protected String guessProjectName() {
		return EMPTY_STRING;
	}

	/**
	 * Show a dialog that lets the user select a project. This in turn provides
	 * context for the main type, allowing the user to key a main type name, or
	 * constraining the search for main types to the specified project.
	 */
	protected void handleProjectButtonSelected() {
		IScriptProject project = chooseProject();
		if (project == null) {
			return;
		}

		if (!validateProject(project)) {
			String msg = Messages
					.format(
							DLTKLaunchConfigurationsMessages.mainTab_errorDlg_notALangProject,
							new String[] { getLanguageName() });
			String reason = Messages
					.format(
							DLTKLaunchConfigurationsMessages.mainTab_errorDlg_reasonNotALangProject,
							new String[] { getLanguageName() });
			ErrorDialog
					.openError(
							getShell(),
							DLTKLaunchConfigurationsMessages.mainTab_errorDlg_invalidProject,
							msg, DLTKUIStatus.createError(IStatus.ERROR,
									reason, null));
			return;
		}

		String projectName = project.getElementName();
		setProjectName(projectName);
	}

	/**
	 * Sets the name of the project associated with the launch configuration
	 * 
	 * @param name
	 *            project name
	 */
	protected final void setProjectName(String name) {
		PreferencesLookupDelegate delegate = new PreferencesLookupDelegate(
				getProject());
		setProjectName(name, delegate);
	}

	private void setProjectName(String name, PreferencesLookupDelegate delegate) {
		fProjText.setText(name);
		if (ILaunchManager.DEBUG_MODE.equals(fMode)) {
			if (breakOnFirstLine != null)
				breakOnFirstLine
						.setSelection(breakOnFirstLinePrefEnabled(delegate));
			if (enableLogging != null)
				enableLogging.setSelection(dbpgLoggingPrefEnabled(delegate));
		}
	}

	/**
	 * updates the project text field form the configuration
	 * 
	 * @param config
	 *            the configuration we are editing
	 */
	protected void updateProjectFromConfig(ILaunchConfiguration config) {
		String projectName = LaunchConfigurationUtils.getProjectName(config);
		if (projectName == null) {
			projectName = guessProjectName();
		}

		final PreferencesLookupDelegate delegate = new PreferencesLookupDelegate(
				getProject());

		setProjectName(projectName, delegate);

		if (ILaunchManager.DEBUG_MODE.equals(fMode)) {
			if (breakOnFirstLine != null) {
				ILaunchConfigDefaultBooleanProvider provider = new ILaunchConfigDefaultBooleanProvider() {
					public boolean getDefault() {
						return breakOnFirstLinePrefEnabled(delegate);
					}
				};
				breakOnFirstLine.setSelection(LaunchConfigurationUtils
						.isBreakOnFirstLineEnabled(config, provider));
			}

			if (enableLogging != null) {
				ILaunchConfigDefaultBooleanProvider provider = new ILaunchConfigDefaultBooleanProvider() {
					public boolean getDefault() {
						return dbpgLoggingPrefEnabled(delegate);
					}
				};
				enableLogging.setSelection(LaunchConfigurationUtils
						.isDbgpLoggingEnabled(config, provider));
			}
		}
	}

	protected boolean validateProject() {
		String projectName = getProjectName();
		if (projectName.length() == 0) {
			setErrorMessage(DLTKLaunchConfigurationsMessages.error_selectProject);
			return false;
		}

		IScriptProject proj = getScriptModel().getScriptProject(projectName);
		if (proj == null || !validateProject(proj)) {
			setErrorMessage(DLTKLaunchConfigurationsMessages.error_notAValidProject);
			return false;
		}

		return true;
	}

	protected boolean validateProject(IScriptProject project) {
		if (project == null) {
			return false;
		}
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(project);
		return toolkit != null && toolkit.getNatureId().equals(getNatureID());
	}

	/**
	 * A listener which handles widget change events for the controls in this
	 * tab.
	 */
	class WidgetListener implements ModifyListener, SelectionListener {
		public void modifyText(ModifyEvent e) {
			setErrorMessage(null);
			if (e.getSource() == fProjText) {
				IScriptProject proj = getProject();
				if (proj != null) {
					if (!validateProject(proj)) {
						setErrorMessage(DLTKLaunchConfigurationsMessages.error_notAValidProject);
					}
				} else {
					setErrorMessage(DLTKLaunchConfigurationsMessages.error_selectProject);
				}
			}

			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			/* do nothing */
		}

		public void widgetSelected(SelectionEvent e) {
			updateLaunchConfigurationDialog();
		}
	}

}
