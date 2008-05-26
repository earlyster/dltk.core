/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.dltk.search;

import java.util.List;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.mylyn.internal.dltk.DLTKStructureBridge;

public class DLTKWriteAccessProvider extends AbstractDLTKRelationProvider {

	public List getDegreesOfSeparation() {
		return null;
	}

	public static final String ID = ID_GENERIC + ".writeaccess";

	public static final String NAME = "written by";

	public DLTKWriteAccessProvider() {
		super(DLTKStructureBridge.CONTENT_TYPE, ID);
	}

	protected boolean acceptElement(IModelElement modelElement) {
		return modelElement instanceof IField;
	}

	protected String getSourceId() {
		return ID;
	}

	public String getName() {
		return NAME;
	}
}
