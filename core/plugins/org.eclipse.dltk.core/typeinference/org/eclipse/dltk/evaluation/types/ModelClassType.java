/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.evaluation.types;

import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.ti.types.IEvaluatedType;

public class ModelClassType implements IClassType {

	private IType fClass;

	public ModelClassType(IType classElement) {
		this.fClass = classElement;
	}

	public String getTypeName() {
		if (fClass != null) {
			return "class:" + fClass.getElementName(); //$NON-NLS-1$
		}
		return "class: !!unknown!!"; //$NON-NLS-1$
	}

	public IType getTypeDeclaration() {
		return this.fClass;
	}

	public boolean subtypeOf(IEvaluatedType type) {
		// TODO Auto-generated method stub
		return false;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fClass == null) ? 0 : fClass.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelClassType other = (ModelClassType) obj;
		if (fClass == null) {
			if (other.fClass != null)
				return false;
		} else if (!fClass.equals(other.fClass))
			return false;
		return true;
	}
}
