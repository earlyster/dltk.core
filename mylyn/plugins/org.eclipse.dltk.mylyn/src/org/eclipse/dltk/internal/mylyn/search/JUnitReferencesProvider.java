/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.internal.mylyn.search;

import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.internal.mylyn.DLTKStructureBridge;

/**
 * @author Mik Kersten
 */
public class JUnitReferencesProvider extends AbstractJavaRelationProvider {

	public static final String ID = ID_GENERIC + ".junitreferences"; //$NON-NLS-1$

	public static final String NAME = "tested by"; //$NON-NLS-1$

	public JUnitReferencesProvider() {
		super(DLTKStructureBridge.CONTENT_TYPE, ID);
	}

	@Override
	protected boolean acceptResultElement(IModelElement element) {
		if (element instanceof IMethod) {
			IMethod method = (IMethod) element;
			boolean isTestMethod = false;
			boolean isTestCase = false;
			if (method.getElementName().startsWith("test")) { //$NON-NLS-1$
				isTestMethod = true;
			}

			IModelElement parent = method.getParent();
			if (parent instanceof IType) {
				// IType type = (IType) parent;
				isTestCase = false; // InteractionContextTestUtil.isTestType(type);
			}
			return isTestMethod && isTestCase;
		}
		return false;
	}

	@Override
	protected String getSourceId() {
		return ID;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
