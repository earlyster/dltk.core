/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.wizards.dialogfields;

import org.eclipse.dltk.internal.ui.util.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog field containing a label, text control and a button control.
 */
public class StringButtonDialogField extends StringDialogField {

	private Button fBrowseButton;
	private String fBrowseButtonLabel;
	private IStringButtonAdapter fStringButtonAdapter;
	private boolean useLabel = true;

	private boolean fButtonEnabled;

	public StringButtonDialogField(IStringButtonAdapter adapter) {
		super();
		fStringButtonAdapter = adapter;
		fBrowseButtonLabel = "!Browse...!"; //$NON-NLS-1$
		fButtonEnabled = true;
	}

	/**
	 * Sets the label of the button.
	 */
	public void setButtonLabel(String label) {
		fBrowseButtonLabel = label;
	}

	// ------ adapter communication

	/**
	 * Programmatical pressing of the button
	 */
	public void changeControlPressed() {
		fStringButtonAdapter.changeControlPressed(this);
	}

	// ------- layout helpers

	@Override
	public Control[] doFillIntoGrid(Composite parent, int nColumns) {
		assertEnoughColumns(nColumns);
		final Control[] result = new Control[getNumberOfControls()];
		int resultIndex = 0;
		final boolean useLabel = isUseLabel();
		if (useLabel) {
			final Label label = getLabelControl(parent);
			label.setLayoutData(gridDataForLabel(1));
			result[resultIndex++] = label;
		}
		final Text text = getTextControl(parent);
		text.setLayoutData(gridDataForText(nColumns - 2));
		result[resultIndex++] = text;
		final Button button = getChangeControl(parent);
		button.setLayoutData(gridDataForButton(button, 1));
		result[resultIndex++] = button;
		return result;
	}

	@Override
	public int getNumberOfControls() {
		return isUseLabel() ? 3 : 2;
	}

	public boolean isUseLabel() {
		return useLabel;
	}

	public void setUseLabel(boolean value) {
		this.useLabel = value;
	}

	public static GridData gridDataForButton(Button button, int span) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = span;
		gd.widthHint = SWTUtil.getButtonWidthHint(button);
		return gd;
	}

	// ------- ui creation

	/**
	 * Creates or returns the created buttom widget.
	 * 
	 * @param parent
	 *            The parent composite or <code>null</code> if the widget has
	 *            already been created.
	 */
	public Button getChangeControl(Composite parent) {
		if (fBrowseButton == null) {
			assertCompositeNotNull(parent);

			fBrowseButton = new Button(parent, SWT.PUSH);
			fBrowseButton.setFont(parent.getFont());
			fBrowseButton.setText(fBrowseButtonLabel);
			fBrowseButton.setEnabled(isEnabled() && fButtonEnabled);
			fBrowseButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					changeControlPressed();
				}

				public void widgetSelected(SelectionEvent e) {
					changeControlPressed();
				}
			});

		}
		return fBrowseButton;
	}

	// ------ enable / disable management

	/**
	 * Sets the enable state of the button.
	 */
	public void enableButton(boolean enable) {
		if (isOkToUse(fBrowseButton)) {
			fBrowseButton.setEnabled(isEnabled() && enable);
		}
		fButtonEnabled = enable;
	}

	@Override
	protected void updateEnableState() {
		super.updateEnableState();
		if (isOkToUse(fBrowseButton)) {
			fBrowseButton.setEnabled(isEnabled() && fButtonEnabled);
		}
	}
}
