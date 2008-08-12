/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.ui.preferences;

import java.util.List;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.dltk.compiler.task.TodoTask;
import org.eclipse.dltk.compiler.task.TodoTaskPreferences;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore.OverlayKey;
import org.eclipse.dltk.ui.util.PixelConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TodoTaskConfigurationBlock extends AbstractConfigurationBlock {

	private class TodoTaskLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element) {
			return getColumnText(element, 0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
		 *      int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
		 *      int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			final TodoTask task = (TodoTask) element;
			if (columnIndex == 0) {
				return task.name;
			} else {
				if (TodoTask.PRIORITY_HIGH.equals(task.priority)) {
					return PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_high_priority;
				} else if (TodoTask.PRIORITY_NORMAL.equals(task.priority)) {
					return PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_normal_priority;
				} else if (TodoTask.PRIORITY_LOW.equals(task.priority)) {
					return PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_low_priority;
				}
				return ""; //$NON-NLS-1$
			}
		}

	}

	private static class TodoTaskSorter extends ViewerSorter {
		public int compare(Viewer viewer, Object e1, Object e2) {
			return getComparator().compare(((TodoTask) e1).name,
					((TodoTask) e2).name);
		}
	}

	private static final int IDX_ADD = 0;
	private static final int IDX_EDIT = 1;
	private static final int IDX_REMOVE = 2;

	private ListDialogField fTodoTasksList;
	private Button fCaseSensitiveCheckbox;

	private final TodoTaskPreferences preferences;

	public TodoTaskConfigurationBlock(Preferences preferences,
			OverlayPreferenceStore store, PreferencePage mainPreferencePage) {
		super(store, mainPreferencePage);
		this.preferences = new TodoTaskPreferences(preferences);
		store.addKeys(createKeys());
	}

	private OverlayKey[] createKeys() {
		return new OverlayPreferenceStore.OverlayKey[] {
				new OverlayPreferenceStore.OverlayKey(
						OverlayPreferenceStore.STRING, TodoTaskPreferences.TAGS),
				new OverlayPreferenceStore.OverlayKey(
						OverlayPreferenceStore.BOOLEAN,
						TodoTaskPreferences.ENABLED),
				new OverlayPreferenceStore.OverlayKey(
						OverlayPreferenceStore.BOOLEAN,
						TodoTaskPreferences.CASE_SENSITIVE) };
	}

	public class TaskTagAdapter implements IListAdapter, IDialogFieldListener {

		private boolean canEdit(List selectedElements) {
			return selectedElements.size() == 1;
		}

		public void customButtonPressed(ListDialogField field, int index) {
			doTodoButtonPressed(index);
		}

		public void selectionChanged(ListDialogField field) {
			List selectedElements = field.getSelectedElements();
			field.enableButton(IDX_EDIT, canEdit(selectedElements));
		}

		public void doubleClicked(ListDialogField field) {
			if (canEdit(field.getSelectedElements())) {
				doTodoButtonPressed(IDX_EDIT);
			}
		}

		public void dialogFieldChanged(DialogField field) {
			updateModel(field);
		}

	}

	protected final void updateModel(DialogField field) {
		preferences.setTaskTags(fTodoTasksList.getElements());
	}

	private void doTodoButtonPressed(int index) {
		TodoTask edited = null;
		if (index != IDX_ADD) {
			edited = (TodoTask) fTodoTasksList.getSelectedElements().get(0);
		}
		if (index == IDX_ADD || index == IDX_EDIT) {
			TodoTaskInputDialog dialog = new TodoTaskInputDialog(getShell(),
					edited, fTodoTasksList.getElements());
			if (dialog.open() == Window.OK) {
				if (edited != null) {
					fTodoTasksList.replaceElement(edited, dialog.getResult());
				} else {
					fTodoTasksList.addElement(dialog.getResult());
				}
			}
		}
	}

	public Control createControl(Composite parent) {
		final TaskTagAdapter adapter = new TaskTagAdapter();
		final String[] buttons = new String[] {
				PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_add_button,
				PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_edit_button,
				PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_remove_button };
		fTodoTasksList = new ListDialogField(adapter, buttons,
				new TodoTaskLabelProvider());
		fTodoTasksList.setDialogFieldListener(adapter);
		fTodoTasksList.setRemoveButtonIndex(IDX_REMOVE);

		final String[] columnsHeaders = new String[] {
				PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_name_column,
				PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_priority_column, };
		fTodoTasksList.setTableColumns(new ListDialogField.ColumnsDescription(
				columnsHeaders, true));
		fTodoTasksList.setViewerSorter(new TodoTaskSorter());

		final GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;

		final PixelConverter conv = new PixelConverter(parent);

		final Composite markersComposite = new Composite(parent, SWT.NULL);
		markersComposite.setLayout(layout);
		markersComposite.setFont(parent.getFont());

		final Button enableCheckbox = addCheckBox(markersComposite,
				PreferencesMessages.TodoTaskConfigurationBlock_enableTaskTags, TodoTaskPreferences.ENABLED, 0);

		fCaseSensitiveCheckbox = addCheckBox(
				markersComposite,
				PreferencesMessages.TodoTaskConfigurationBlock_casesensitive_label,
				TodoTaskPreferences.CASE_SENSITIVE, 0);

		final GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = conv.convertWidthInCharsToPixels(50);
		fTodoTasksList.getListControl(markersComposite).setLayoutData(data);

		fTodoTasksList.getButtonBox(markersComposite).setLayoutData(
				new GridData(GridData.HORIZONTAL_ALIGN_FILL
						| GridData.VERTICAL_ALIGN_BEGINNING));

		enableCheckbox.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				final boolean enabled = ((Button) e.widget).getSelection();
				updateEnableState(enabled);
			}

		});

		return markersComposite;
	}

	protected void updateEnableState(boolean enabled) {
		fTodoTasksList.setEnabled(enabled);
		fCaseSensitiveCheckbox.setEnabled(enabled);
	}

	protected void initializeFields() {
		super.initializeFields();
		fTodoTasksList.setElements(preferences.getTaskTags());
		if (fTodoTasksList.getSize() > 0) {
			fTodoTasksList.selectFirstElement();
		} else {
			fTodoTasksList.enableButton(IDX_EDIT, false);
		}
		updateEnableState(preferences.isEnabled());
	}

}
