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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public interface IFormatterControlManager {

	Button createCheckbox(Composite parent, Object key, String text);

	Button createCheckbox(Composite parent, Object key, String text, int hspan);

	Combo createCombo(Composite parent, Object key, String label, String[] items);

	Text createNumber(Composite parent, Object key, String label);

	void enableControl(Control control, boolean enabled);

}
