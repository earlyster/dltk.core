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
package org.eclipse.dltk.internal.ui.typehierarchy;

import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.actions.OpenAction;
import org.eclipse.ui.IWorkbenchSite;

public class HierarchyOpenAction extends OpenAction {

	/**
	 * @param site
	 */
	public HierarchyOpenAction(IWorkbenchSite site) {
		super(site);
	}

	/*
	 * @see OpenAction#checkElement(java.lang.Object)
	 */
	protected boolean checkElement(Object element) {
		return element instanceof CumulativeType
				|| element instanceof CumulativeType.Part
				|| super.checkElement(element);
	}

	/*
	 * @see OpenAction#run(java.lang.Object[])
	 */
	public void run(Object[] elements) {
		if (elements.length == 1 && elements[0] instanceof CumulativeType) {
			selectAndOpen(((CumulativeType) elements[0]).getTypes());
		} else {
			super.run(elements);
		}
	}

	/*
	 * @see OpenAction#getElementToOpen(java.lang.Object)
	 */
	public Object getElementToOpen(Object object) throws ModelException {
		if (object instanceof CumulativeType.Part) {
			return ((CumulativeType.Part) object).type;
		}
		return super.getElementToOpen(object);
	}
}
