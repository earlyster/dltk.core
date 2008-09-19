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
package org.eclipse.dltk.internal.testing.ui;

import org.eclipse.dltk.internal.testing.model.TestCategoryElement;
import org.eclipse.dltk.internal.testing.model.TestSuiteElement;
import org.eclipse.jface.viewers.ViewerComparator;

public class TestTreeComparator extends ViewerComparator {

	public int category(Object element) {
		if (element instanceof TestCategoryElement) {
			return 0;
		} else if (element instanceof TestSuiteElement) {
			return 1;
		} else {
			return 2;
		}
	}

}
