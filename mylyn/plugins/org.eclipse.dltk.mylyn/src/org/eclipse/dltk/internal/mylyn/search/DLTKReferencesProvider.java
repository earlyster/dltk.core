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
import org.eclipse.dltk.internal.mylyn.DLTKStructureBridge;

/**
 * @author Mik Kersten
 */
public class DLTKReferencesProvider extends AbstractJavaRelationProvider {

	public static final String ID = ID_GENERIC + ".references"; //$NON-NLS-1$

	public static final String NAME = "referenced by"; //$NON-NLS-1$

	public DLTKReferencesProvider() {
		super(DLTKStructureBridge.CONTENT_TYPE, ID);
	}

	@Override
	protected boolean acceptResultElement(IModelElement element) {
//		if (element instanceof IImportDeclaration) {
//			return false;
//		}
		if (element instanceof IMethod) {
			IMethod method = (IMethod) element;
			if (method.getElementName().startsWith("test")) { //$NON-NLS-1$
				return false; // HACK
			} else {
				return true;
			}
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
