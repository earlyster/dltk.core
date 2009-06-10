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

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.internal.mylyn.DLTKStructureBridge;

/**
 * @author Mik Kersten
 */
public class DLTKReadAccessProvider extends AbstractJavaRelationProvider {

	public static final String ID = ID_GENERIC + ".readaccess"; //$NON-NLS-1$

	public static final String NAME = "read by"; //$NON-NLS-1$

	public DLTKReadAccessProvider() {
		super(DLTKStructureBridge.CONTENT_TYPE, ID);
	}

	@Override
	protected boolean acceptElement(IModelElement javaElement) {
		return javaElement instanceof IField;
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
