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
		Button button = SWTFactory.createCheckButton(parent, text, null, false,
				1);
		bindingManager.bindControl(button, key, null);
		return button;
	}

	public Combo createCombo(Composite parent, Object key, String label,
			String[] items) {
		SWTFactory.createLabel(parent, label, 1);
		Combo combo = SWTFactory.createCombo(parent,
				SWT.READ_ONLY | SWT.BORDER, 1, items);
		bindingManager.bindControl(combo, key);
		return combo;
	}

	public Text createNumber(Composite parent, Object key, String label) {
		SWTFactory.createLabel(parent, label, 1);
		Text text = SWTFactory.createText(parent, SWT.BORDER, 1,
				Util.EMPTY_STRING);
		bindingManager.bindControl(text, key,
				FieldValidators.POSITIVE_NUMBER_VALIDATOR);
		return text;
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
