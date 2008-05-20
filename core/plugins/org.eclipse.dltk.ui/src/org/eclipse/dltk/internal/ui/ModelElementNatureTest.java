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
package org.eclipse.dltk.internal.ui;

import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.ui.actions.IActionFilterTester;

public class ModelElementNatureTest implements IActionFilterTester {

	public boolean test(Object target, String name, String value) {
		if (target instanceof IModelElement) {
			final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
					.getLanguageToolkit((IModelElement) target);
			if (toolkit != null) {
				return toolkit.getNatureId().equals(value);
			}
		}
		return false;
	}

}
