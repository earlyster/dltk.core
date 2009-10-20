/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.debug.ui.interpreters;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationsMessages;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.ui.dialogs.MultipleInputDialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.dialogs.ListSelectionDialog;

/**
 * Control used to edit the environment variables associated with a Interpreter
 * install
 */
@SuppressWarnings("restriction")
public abstract class AbstractInterpreterEnvironmentVariablesBlock implements
		SelectionListener, ISelectionChangedListener {

	private static final String VALUE_LABEL = InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_value;

	private static final String NAME_LABEL = InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_name;

	protected boolean fInCallback = false;
	protected IInterpreterInstall fInterpreterInstall;
	protected IInterpreterInstallType fInterpreterInstallType;

	// widgets
	protected EnvironmentVariableContentProvider fEnvironmentVariablesContentProvider;
	protected TreeViewer fVariablesViewer;
	private Button fRemoveButton;
	private Button fAddExistedButton;
	private Button fAddButton;
	private Button fEditButton;
	private Button fImportButton;
	private Button fExportButton;

	protected AddScriptInterpreterDialog fDialog;

	protected AbstractInterpreterEnvironmentVariablesBlock(
			AddScriptInterpreterDialog dialog) {
		this.fDialog = dialog;
	}

	/**
	 * Creates and returns the source lookup control.
	 * 
	 * @param parent
	 *            the parent widget of this control
	 */
	public Control createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 2;
		topLayout.marginHeight = 0;
		topLayout.marginWidth = 0;
		comp.setLayout(topLayout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(gd);
		createControlsIn(comp);
		return comp;
	}

	/**
	 * @since 2.0
	 */
	public void createControlsIn(Composite comp) {
		fVariablesViewer = new TreeViewer(comp);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 6;
		fVariablesViewer.getControl().setLayoutData(gd);
		fEnvironmentVariablesContentProvider = new EnvironmentVariableContentProvider();
		fVariablesViewer
				.setContentProvider(fEnvironmentVariablesContentProvider);
		fVariablesViewer.setLabelProvider(getLabelProvider());
		fVariablesViewer.setInput(this);
		fVariablesViewer.addSelectionChangedListener(this);

		Composite pathButtonComp = new Composite(comp, SWT.NONE);
		GridLayout pathButtonLayout = new GridLayout();
		pathButtonLayout.marginHeight = 0;
		pathButtonLayout.marginWidth = 0;
		pathButtonComp.setLayout(pathButtonLayout);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
				| GridData.HORIZONTAL_ALIGN_FILL);
		pathButtonComp.setLayoutData(gd);

		fAddButton = createPushButton(
				pathButtonComp,
				InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_add);
		fAddButton.addSelectionListener(this);

		fAddExistedButton = createPushButton(
				pathButtonComp,
				InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_addFromEnvironment);
		fAddExistedButton.addSelectionListener(this);
		fEditButton = createPushButton(
				pathButtonComp,
				InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_edit);
		fEditButton.addSelectionListener(this);

		fRemoveButton = createPushButton(pathButtonComp,
				InterpretersMessages.InterpreterLibraryBlock_6);
		fRemoveButton.addSelectionListener(this);

		fImportButton = createPushButton(
				pathButtonComp,
				InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_import);
		fImportButton.addSelectionListener(this);

		fExportButton = createPushButton(
				pathButtonComp,
				InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_export);
		fExportButton.addSelectionListener(this);
	}

	/**
	 * The "default" button has been toggled
	 */
	public void restoreDefaultVariables() {
		fEnvironmentVariablesContentProvider
				.setVariables(new EnvironmentVariable[0]);
		update();
	}

	/**
	 * Creates and returns a button
	 * 
	 * @param parent
	 *            parent widget
	 * @param label
	 *            label
	 * @return Button
	 */
	protected Button createPushButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		button.setText(label);
		setButtonLayoutData(button);
		return button;
	}

	/**
	 * Create some empty space
	 */
	protected void createVerticalSpacer(Composite comp, int colSpan) {
		Label label = new Label(comp, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = colSpan;
		label.setLayoutData(gd);
	}

	/**
	 * Updates buttons and status based on current libraries
	 */
	public void update() {
		updateButtons();
		IStatus status = Status.OK_STATUS;
		EnvironmentVariable[] standins = fEnvironmentVariablesContentProvider
				.getStandins();
		for (int i = 0; i < standins.length; i++) {
			IStatus st = standins[i].validate();
			if (!st.isOK()) {
				status = st;
				break;
			}
		}
		updateDialogStatus(status);
	}

	/**
	 * Saves settings in the given working copy
	 */
	public void performApply(IInterpreterInstall install) {
		EnvironmentVariable[] libs = fEnvironmentVariablesContentProvider
				.getVariables();
		install.setEnvironmentVariables(libs);
	}

	public EnvironmentVariable[] getEnvironmentVariables() {
		EnvironmentVariable[] libs = fEnvironmentVariablesContentProvider
				.getVariables();
		return libs;
	}

	/**
	 * Determines if the present setup is the default location s for this
	 * InterpreterEnvironment
	 * 
	 * @return true if the current set of locations are the defaults, false
	 *         otherwise
	 */
	protected boolean isDefaultLocations() {
		EnvironmentVariable[] EnvironmentVariables = fEnvironmentVariablesContentProvider
				.getVariables();
		IInterpreterInstall install = getInterpreterInstall();

		if (install == null || EnvironmentVariables == null) {
			return true;
		}
		IFileHandle installLocation = install.getInstallLocation();
		if (installLocation != null) {
			EnvironmentVariable[] def = new EnvironmentVariable[0];
			if (def.length == EnvironmentVariables.length) {
				for (int i = 0; i < def.length; i++) {
					if (!def[i].equals(EnvironmentVariables[i])) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the Interpreter install associated with this library block.
	 * 
	 * @return Interpreter install
	 */
	protected IInterpreterInstall getInterpreterInstall() {
		return fInterpreterInstall;
	}

	/**
	 * Returns the Interpreter install type associated with this library block.
	 * 
	 * @return Interpreter install
	 */
	protected IInterpreterInstallType getInterpreterInstallType() {
		return fInterpreterInstallType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt
	 * .events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		Object source = e.getSource();
		/*
		 * if (source == fUpButton) { fEnvironmentVariablesContentProvider
		 * .up((IStructuredSelection) fLibraryViewer.getSelection()); } else if
		 * (source == fDownButton) { fEnvironmentVariablesContentProvider
		 * .down((IStructuredSelection) fLibraryViewer.getSelection()); } else
		 */if (source == fRemoveButton) {
			EnvironmentVariable[] old = this.fEnvironmentVariablesContentProvider
					.getVariables();
			fEnvironmentVariablesContentProvider
					.remove((IStructuredSelection) fVariablesViewer
							.getSelection());
			fDialog.updateLibraries(this.fEnvironmentVariablesContentProvider
					.getVariables(), old);
			fDialog.updateValidateInterpreterLocation();
		} else if (source == fAddExistedButton) {
			addExisted((IStructuredSelection) fVariablesViewer.getSelection());
		} else if (source == fAddButton) {
			handleAdd();
		} else if (source == fEditButton) {
			EnvironmentVariable[] old = this.fEnvironmentVariablesContentProvider
					.getVariables();
			if (edit((IStructuredSelection) fVariablesViewer.getSelection())) {
				fDialog.updateLibraries(
						this.fEnvironmentVariablesContentProvider
								.getVariables(), old);
				fDialog.updateValidateInterpreterLocation();
			}
		} else if (source == fImportButton) {
			if (performImport()) {
				fDialog.updateValidateInterpreterLocation();
			}
		} else if (source == fExportButton) {
			performExport();
		}
		update();
	}

	private void performExport() {
		FileDialog dialog = new FileDialog(this.fDialog.getShell(), SWT.SAVE);
		dialog.setOverwrite(true);
		dialog
				.setText(InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_exportEnvironmentVariablesToFile);
		String file = dialog.open();
		if (file != null) {
			EnvironmentVariable[] variables = this.fEnvironmentVariablesContentProvider
					.getVariables();
			try {
				EnvironmentVariablesFileUtils.save(variables, file);
			} catch (Exception e) {
				// String text = "Failed to save environment variables";
				showErrorMessage(
						InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_environmentExport,
						e.getMessage());
			}
		}
	}

	private void showErrorMessage(String title, String text) {
		MessageBox box = new MessageBox(this.fDialog.getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText(title);
		box.setMessage(text);
		box.open();
	}

	private boolean performImport() {
		FileDialog dialog = new FileDialog(this.fDialog.getShell(), SWT.OPEN);
		dialog
				.setText(InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_importEnvironmentVariablesFromFile);
		String file = dialog.open();
		if (file != null) {
			File handle = new File(file);
			if (!handle.exists()) {
				String text = InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_selectedFileDoesntExist;
				showErrorMessage(
						InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_environmentImport,
						text);
				return false;
			}
			EnvironmentVariable[] vars = null;
			try {
				vars = EnvironmentVariablesFileUtils.load(file);
			} catch (Exception e) {
				showErrorMessage(
						InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_environmentImport,
						e.getMessage());
				return false;
			}
			if (vars != null) {
				EnvironmentVariable[] variables = this.fEnvironmentVariablesContentProvider
						.getVariables();
				Set<EnvironmentVariable> nvars = new HashSet<EnvironmentVariable>();
				nvars.addAll(Arrays.asList(vars));
				nvars.addAll(Arrays.asList(variables));
				this.fEnvironmentVariablesContentProvider.setVariables(nvars
						.toArray(new EnvironmentVariable[nvars.size()]));
			}
			return true;
		}
		return false;
	}

	private boolean edit(IStructuredSelection selection) {
		EnvironmentVariable var = (EnvironmentVariable) selection
				.getFirstElement();
		if (var == null) {
			return false;
		}
		String originalName = var.getName();
		String value = var.getValue();
		MultipleInputDialog dialog = new MultipleInputDialog(
				fDialog.getShell(),
				InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_editVariable);
		dialog.addTextField(NAME_LABEL, originalName, false);
		dialog.addVariablesField(VALUE_LABEL, value, true);

		if (dialog.open() != Window.OK) {
			return false;
		}
		String name = dialog.getStringValue(NAME_LABEL);
		value = dialog.getStringValue(VALUE_LABEL);
		if (!originalName.equals(name)) {
			final EnvironmentVariable newVar = new EnvironmentVariable(name,
					value);
			if (fEnvironmentVariablesContentProvider.addVariable(newVar)) {
				fEnvironmentVariablesContentProvider
						.remove(new StructuredSelection(var));
				fVariablesViewer.setSelection(new StructuredSelection(newVar),
						true);
			}
		} else {
			var.setValue(value);
			fVariablesViewer.refresh(true);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse
	 * .swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	private void handleAdd() {
		EnvironmentVariable newVar = add();
		if (newVar == null)
			return;
		EnvironmentVariable[] old = this.fEnvironmentVariablesContentProvider
				.getVariables();
		fEnvironmentVariablesContentProvider.addVariable(newVar);
		fVariablesViewer.setSelection(new StructuredSelection(newVar), true);
		fVariablesViewer.refresh();
		fDialog.updateLibraries(this.fEnvironmentVariablesContentProvider
				.getVariables(), old);
		update();
		fDialog.updateValidateInterpreterLocation();
	}

	private EnvironmentVariable add() {
		MultipleInputDialog dialog = new MultipleInputDialog(
				fDialog.getShell(),
				InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_addVariable);
		dialog.addTextField(NAME_LABEL, null, false);
		dialog.addVariablesField(VALUE_LABEL, null, true);

		if (dialog.open() != Window.OK) {
			return null;
		}

		String name = dialog.getStringValue(NAME_LABEL);
		String value = dialog.getStringValue(VALUE_LABEL);

		if (name != null && value != null && name.length() > 0
				&& value.length() > 0) {
			return new EnvironmentVariable(name.trim(), value.trim());
		}
		return null;
	}

	private void addExisted(IStructuredSelection selection) {
		EnvironmentVariable[] libs = addExisted();
		if (libs == null)
			return;
		EnvironmentVariable[] old = this.fEnvironmentVariablesContentProvider
				.getVariables();
		fEnvironmentVariablesContentProvider.add(libs, selection);
		fDialog.updateLibraries(this.fEnvironmentVariablesContentProvider
				.getVariables(), old);
		update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(
	 * org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		updateButtons();
	}

	/**
	 * Refresh the enable/disable state for the buttons.
	 */
	private void updateButtons() {
		IStructuredSelection selection = (IStructuredSelection) fVariablesViewer
				.getSelection();
		fRemoveButton.setEnabled(!selection.isEmpty());
		fEditButton.setEnabled(selection.size() == 1);
		@SuppressWarnings("unused")
		boolean enableUp = true, enableDown = true;
		Object[] libraries = fEnvironmentVariablesContentProvider
				.getElements(null);
		if (selection.isEmpty() || libraries.length == 0) {
			enableUp = false;
			enableDown = false;
		} else {
			Object first = libraries[0];
			Object last = libraries[libraries.length - 1];
			for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
				Object element = iter.next();
				Object lib;
				lib = element;
				if (lib == first) {
					enableUp = false;
				}
				if (lib == last) {
					enableDown = false;
				}
			}
		}
		// fUpButton.setEnabled(enableUp);
		// fDownButton.setEnabled(enableDown);
	}

	/**
	 * Initializes this control based on the settings in the given Interpreter
	 * install and type.
	 * 
	 * @param Interpreter
	 *            Interpreter or <code>null</code> if none
	 * @param type
	 *            type of Interpreter install
	 */

	public void initializeFrom(IInterpreterInstall Interpreter,
			IInterpreterInstallType type) {
		fInterpreterInstall = Interpreter;
		fInterpreterInstallType = type;
		if (Interpreter != null) {
			fEnvironmentVariablesContentProvider
					.setVariables(getInterpreterInstall()
							.getEnvironmentVariables());
		}
		update();
	}

	protected IBaseLabelProvider getLabelProvider() {
		return new EnvironmentVariablesLabelProvider();
	}

	protected void updateDialogStatus(IStatus status) {
		fDialog.setSystemLibraryStatus(status);
		fDialog.updateStatusLine();
	}

	protected void setButtonLayoutData(Button button) {
		fDialog.setButtonLayoutData(button);
	}

	/**
	 * @since 2.0
	 */
	@Deprecated
	protected final void getDialogSettions() {

	}

	protected EnvironmentVariable[] addExisted() {

		// get Environment Variables from the Environment
		Map<String, EnvironmentVariable> envVariables = getNativeEnvironment();
		if (envVariables.size() == 0) {
			MessageBox box = new MessageBox(fDialog.getShell(), SWT.ICON_ERROR);
			box
					.setMessage(NLS
							.bind(
									InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_couldNotRetrieveEnvironmentVariablesFrom,
									fDialog.getEnvironment().getName()));
			box
					.setText(InterpretersMessages.AbstractInterpreterEnvironmentVariablesBlock_failedToRetrieveEnvironment);
			box.open();
			return null;
		}

		// get Environment Variables from the table
		for (EnvironmentVariable var : fEnvironmentVariablesContentProvider
				.getVariables()) {
			envVariables.remove(var.getName());
		}

		ListSelectionDialog dialog = new ListSelectionDialog(
				fDialog.getShell(), envVariables,
				createSelectionDialogContentProvider(),
				createSelectionDialogLabelProvider(),
				LaunchConfigurationsMessages.EnvironmentTab_19);
		dialog.setTitle(LaunchConfigurationsMessages.EnvironmentTab_20);

		int button = dialog.open();
		if (button == Window.OK) {
			Object[] selected = dialog.getResult();
			EnvironmentVariable[] vars = new EnvironmentVariable[selected.length];
			for (int i = 0; i < vars.length; i++) {
				vars[i] = (EnvironmentVariable) selected[i];
			}
			return vars;
		}
		return null;
	}

	private ILabelProvider createSelectionDialogLabelProvider() {
		return new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				return DebugPluginImages
						.getImage(IDebugUIConstants.IMG_OBJS_ENVIRONMENT);
			}

			@Override
			public String getText(Object element) {
				EnvironmentVariable var = (EnvironmentVariable) element;
				return NLS.bind(LaunchConfigurationsMessages.EnvironmentTab_7,
						var.getName(), var.getValue());
			}
		};
	}

	private IStructuredContentProvider createSelectionDialogContentProvider() {
		return new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Map<?, ?>) {
					@SuppressWarnings("unchecked")
					final Map<String, EnvironmentVariable> env = (Map<String, EnvironmentVariable>) inputElement;
					final EnvironmentVariable[] elements = env.values()
							.toArray(new EnvironmentVariable[env.size()]);
					Arrays.sort(elements,
							new Comparator<EnvironmentVariable>() {
								public int compare(EnvironmentVariable s1,
										EnvironmentVariable s2) {
									return s1.getName().compareTo(s2.getName());
								}
							});
					return elements;
				} else {
					return new EnvironmentVariable[0];
				}
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		};
	}

	private Map<String, EnvironmentVariable> getNativeEnvironment() {
		IEnvironment environment = fDialog.getEnvironment();
		IExecutionEnvironment execEnvironment = (IExecutionEnvironment) environment
				.getAdapter(IExecutionEnvironment.class);
		Map<String, String> stringVars = execEnvironment
				.getEnvironmentVariables(true);
		HashMap<String, EnvironmentVariable> vars = new HashMap<String, EnvironmentVariable>();
		if (stringVars != null) {
			for (Map.Entry<String, String> entry : stringVars.entrySet()) {
				vars.put(entry.getKey(), new EnvironmentVariable(
						entry.getKey(), entry.getValue()));
			}
		}
		return vars;
	}
}
