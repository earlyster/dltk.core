/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.wizards.dialogfields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

/**
 * A list with check boxes and a button bar. Typical buttons are 'Check All' and
 * 'Uncheck All'. List model is independent of widget creation. DialogFields
 * controls are: Label, List and Composite containing buttons.
 */
public class CheckedListDialogField<E> extends ListDialogField<E> {

	private int fCheckAllButtonIndex;
	private int fUncheckAllButtonIndex;

	private List<E> fCheckedElements;
	private List<E> fGrayedElements;

	public CheckedListDialogField(IListAdapter adapter,
			String[] customButtonLabels, ILabelProvider lprovider) {
		super(adapter, customButtonLabels, lprovider);
		fCheckedElements = new ArrayList<E>();
		fGrayedElements = new ArrayList<E>();

		fCheckAllButtonIndex = -1;
		fUncheckAllButtonIndex = -1;
	}

	/**
	 * Sets the index of the 'check' button in the button label array passed in
	 * the constructor. The behavior of the button marked as the check button
	 * will then be handled internally. (enable state, button invocation
	 * behavior)
	 */
	public void setCheckAllButtonIndex(int checkButtonIndex) {
		fCheckAllButtonIndex = checkButtonIndex;
	}

	/**
	 * Sets the index of the 'uncheck' button in the button label array passed
	 * in the constructor. The behavior of the button marked as the uncheck
	 * button will then be handled internally. (enable state, button invocation
	 * behavior)
	 */
	public void setUncheckAllButtonIndex(int uncheckButtonIndex) {
		fUncheckAllButtonIndex = uncheckButtonIndex;
	}

	/*
	 * @see ListDialogField#createTableViewer
	 */
	@Override
	protected TableViewer createTableViewer(Composite parent) {
		Table table = new Table(parent, SWT.CHECK + getListStyle());
		table.setFont(parent.getFont());
		CheckboxTableViewer tableViewer = new CheckboxTableViewer(table);
		tableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent e) {
				doCheckStateChanged(e);
			}
		});
		return tableViewer;
	}

	/*
	 * @see ListDialogField#getListControl
	 */
	@Override
	public Control getListControl(Composite parent) {
		Control control = super.getListControl(parent);
		if (parent != null) {
			final CheckboxTableViewer checkTable = (CheckboxTableViewer) fTable;
			checkTable.setCheckedElements(fCheckedElements.toArray());
			checkTable.setGrayedElements(fGrayedElements.toArray());
		}
		return control;
	}

	/*
	 * @see DialogField#dialogFieldChanged Hooks in to get element changes to
	 * update check model.
	 */
	@Override
	public void dialogFieldChanged() {
		for (int i = fCheckedElements.size() - 1; i >= 0; i--) {
			if (!fElements.contains(fCheckedElements.get(i))) {
				fCheckedElements.remove(i);
			}
		}
		super.dialogFieldChanged();
	}

	private void checkStateChanged() {
		// call super and do not update check model
		super.dialogFieldChanged();
	}

	/**
	 * Gets the checked elements.
	 */
	public List<E> getCheckedElements() {
		if (isOkToUse(fTableControl)) {
			// workaround for bug 53853
			Object[] checked = ((CheckboxTableViewer) fTable)
					.getCheckedElements();
			ArrayList<E> res = new ArrayList<E>(checked.length);
			for (int i = 0; i < checked.length; i++) {
				@SuppressWarnings("unchecked")
				final E element = (E) checked[i];
				res.add(element);
			}
			return res;
		}

		return new ArrayList<E>(fCheckedElements);
	}

	/**
	 * Returns the number of checked elements.
	 */
	public int getCheckedSize() {
		return fCheckedElements.size();
	}

	/**
	 * Returns true if the element is checked.
	 */
	public boolean isChecked(Object obj) {
		if (isOkToUse(fTableControl)) {
			return ((CheckboxTableViewer) fTable).getChecked(obj);
		}

		return fCheckedElements.contains(obj);
	}

	public boolean isGrayed(Object obj) {
		if (isOkToUse(fTableControl)) {
			return ((CheckboxTableViewer) fTable).getGrayed(obj);
		}

		return fGrayedElements.contains(obj);
	}

	/**
	 * Sets the checked elements.
	 */
	public void setCheckedElements(Collection<E> list) {
		fCheckedElements = new ArrayList<E>(list);
		if (isOkToUse(fTableControl)) {
			((CheckboxTableViewer) fTable).setCheckedElements(list.toArray());
		}
		checkStateChanged();
	}

	public void setGrayedElements(Collection<E> list) {
		fGrayedElements = new ArrayList<E>(list);
		if (isOkToUse(fTableControl)) {
			((CheckboxTableViewer) fTable).setGrayedElements(list.toArray());
		}
		checkStateChanged();
	}

	/**
	 * Sets the checked state of an element.
	 */
	public void setChecked(E object, boolean state) {
		setCheckedWithoutUpdate(object, state);
		checkStateChanged();
	}

	/**
	 * Sets the checked state of an element. No dialog changed listener is
	 * informed.
	 */
	public void setCheckedWithoutUpdate(E object, boolean state) {
		if (state) {
			if (!fCheckedElements.contains(object)) {
				fCheckedElements.add(object);
			}
		} else {
			fCheckedElements.remove(object);
		}
		if (isOkToUse(fTableControl)) {
			((CheckboxTableViewer) fTable).setChecked(object, state);
		}
	}

	public void setGrayedWithoutUpdate(E object, boolean state) {
		if (state) {
			if (!fGrayedElements.contains(object)) {
				fGrayedElements.add(object);
			}
		} else {
			fGrayedElements.remove(object);
		}
		if (isOkToUse(fTableControl)) {
			((CheckboxTableViewer) fTable).setGrayed(object, state);
		}
	}

	/**
	 * Sets the check state of all elements
	 */
	public void checkAll(boolean state) {
		if (state) {
			fCheckedElements = getElements();
		} else {
			fCheckedElements.clear();
		}
		if (isOkToUse(fTableControl)) {
			((CheckboxTableViewer) fTable).setAllChecked(state);
		}
		checkStateChanged();
	}

	private final ListenerList checkStateListeners = new ListenerList();

	public void addCheckStateListener(ICheckStateListener listener) {
		checkStateListeners.add(listener);
	}

	private void doCheckStateChanged(CheckStateChangedEvent e) {
		if (e.getChecked()) {
			@SuppressWarnings("unchecked")
			final E element = (E) e.getElement();
			fCheckedElements.add(element);
		} else {
			fCheckedElements.remove(e.getElement());
		}
		final Object[] listeners = checkStateListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((ICheckStateListener) listeners[i]).checkStateChanged(e);
		}
		checkStateChanged();
	}

	@Override
	public void replaceElement(E oldElement, E newElement)
			throws IllegalArgumentException {
		boolean wasChecked = isChecked(oldElement);
		super.replaceElement(oldElement, newElement);
		setChecked(newElement, wasChecked);
	}

	// ------ enable / disable management

	/*
	 * @see ListDialogField#getManagedButtonState
	 */
	@Override
	protected boolean getManagedButtonState(ISelection sel, int index) {
		if (index == fCheckAllButtonIndex) {
			return !fElements.isEmpty();
		} else if (index == fUncheckAllButtonIndex) {
			return !fElements.isEmpty();
		}
		return super.getManagedButtonState(sel, index);
	}

	/*
	 * @see ListDialogField#extraButtonPressed
	 */
	@Override
	protected boolean managedButtonPressed(int index) {
		if (index == fCheckAllButtonIndex) {
			checkAll(true);
		} else if (index == fUncheckAllButtonIndex) {
			checkAll(false);
		} else {
			return super.managedButtonPressed(index);
		}
		return true;
	}

	@Override
	public void refresh() {
		super.refresh();
		if (isOkToUse(fTableControl)) {
			final CheckboxTableViewer checkTable = (CheckboxTableViewer) fTable;
			checkTable.setCheckedElements(fCheckedElements.toArray());
			checkTable.setGrayedElements(fGrayedElements.toArray());
		}
	}

}
