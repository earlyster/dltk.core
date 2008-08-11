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

import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.ui.preferences.FieldValidators;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public abstract class FormatterModifyTabPage implements
		IFormatterModifiyTabPage {

	protected final IFormatterModifyDialog dialog;
	private ProjectionViewer previewViewer;

	/**
	 * @param dialogOwner
	 */
	public FormatterModifyTabPage(IFormatterModifyDialog dialog) {
		this.dialog = dialog;
	}

	public Composite createContents(Composite parent) {
		final SashForm page = new SashForm(parent, SWT.HORIZONTAL);
		Composite options = SWTFactory.createComposite(page, page.getFont(), 1,
				1, GridData.FILL_BOTH);
		createOptions(options);
		Composite previewBlock = SWTFactory.createComposite(page, page
				.getFont(), 1, 1, GridData.FILL_BOTH);
		previewViewer = dialog.getOwner().createPreview(previewBlock);
		// TODO load preview text
		return page;
	}

	public void updatePreview() {
		if (previewViewer != null) {
			// TODO update
		}
	}

	protected abstract void createOptions(Composite parent);

	protected Button createCheckbox(Composite parent, Object key, String text) {
		Button button = SWTFactory.createCheckButton(parent, text, null, false,
				1);
		dialog.getBindingManager().bindControl(button, key, null);
		return button;
	}

	protected Combo createCombo(Composite parent, Object key, String label,
			String[] items) {
		SWTFactory.createLabel(parent, label, 1);
		Combo combo = SWTFactory.createCombo(parent,
				SWT.READ_ONLY | SWT.BORDER, 1, items);
		dialog.getBindingManager().bindControl(combo, key);
		return combo;
	}

	protected Text createNumber(Composite parent, Object key, String label) {
		SWTFactory.createLabel(parent, label, 1);
		Text text = SWTFactory.createText(parent, SWT.BORDER, 1,
				Util.EMPTY_STRING);
		dialog.getBindingManager().bindControl(text, key,
				FieldValidators.POSITIVE_NUMBER_VALIDATOR);
		return text;
	}

}
