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
package org.eclipse.dltk.ui.formatter.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.ui.formatter.IFormatterControlManager;
import org.eclipse.dltk.ui.preferences.ControlBindingManager;
import org.eclipse.dltk.ui.preferences.FieldValidators;
import org.eclipse.dltk.ui.preferences.IPreferenceDelegate;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FormatterControlManager implements IFormatterControlManager,
		IStatusChangeListener {

	private final ControlBindingManager bindingManager;
	private final IStatusChangeListener listener;

	public FormatterControlManager(IPreferenceDelegate delegate,
			IStatusChangeListener listener) {
		bindingManager = new ControlBindingManager(delegate, this);
		this.listener = listener;
	}

	public Button createCheckbox(Composite parent, Object key, String text) {
		return createCheckbox(parent, key, text, 1);
	}

	public Button createCheckbox(Composite parent, Object key, String text,
			int hspan) {
		Button button = SWTFactory.createCheckButton(parent, text, null, false,
				hspan);
		bindingManager.bindControl(button, key, null);
		return button;
	}

	public Combo createCombo(Composite parent, Object key, String label,
			String[] items) {
		final Label labelControl = SWTFactory.createLabel(parent, label, 1);
		Combo combo = SWTFactory.createCombo(parent,
				SWT.READ_ONLY | SWT.BORDER, 1, items);
		bindingManager.bindControl(combo, key);
		registerAssociatedLabel(combo, labelControl);
		return combo;
	}

	public Text createNumber(Composite parent, Object key, String label) {
		final Label labelControl = SWTFactory.createLabel(parent, label, 1);
		Text text = SWTFactory.createText(parent, SWT.BORDER, 1,
				Util.EMPTY_STRING);
		bindingManager.bindControl(text, key,
				FieldValidators.POSITIVE_NUMBER_VALIDATOR);
		registerAssociatedLabel(text, labelControl);
		return text;
	}

	private final Map labelAssociations = new HashMap();

	/**
	 * @param control
	 * @param label
	 */
	private void registerAssociatedLabel(Control control, Label label) {
		labelAssociations.put(control, label);
	}

	public void enableControl(Control control, boolean enabled) {
		control.setEnabled(enabled);
		final Label label = (Label) labelAssociations.get(control);
		if (label != null) {
			label.setEnabled(enabled);
		}
	}

	private boolean initialization;

	public void initialize() {
		initialization = true;
		try {
			bindingManager.initialize();
		} finally {
			initialization = false;
		}
		listener.statusChanged(bindingManager.getStatus());
	}

	public void statusChanged(IStatus status) {
		if (!initialization) {
			listener.statusChanged(status);
		}
	}

}
