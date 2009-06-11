/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.evaluation.types;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ti.types.IEvaluatedType;

public class OldClassType implements IClassType {

	private TypeDeclaration fClass;
	private ModuleDeclaration fModule;

	public OldClassType(ModuleDeclaration module, TypeDeclaration method) {
		this.fClass = method;
		this.fModule = module;
	}

	public String getTypeName() {
		if (fClass != null) {
			return "class:" + fClass.getName(); //$NON-NLS-1$
		}
		return "class: !!unknown!!"; //$NON-NLS-1$
	}

	public TypeDeclaration getTypeDeclaration() {

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
		result = prime * result + ((fModule == null) ? 0 : fModule.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OldClassType other = (OldClassType) obj;
		if (fClass == null) {
			if (other.fClass != null)
				return false;
		} else if (!fClass.equals(other.fClass))
			return false;
		if (fModule == null) {
			if (other.fModule != null)
				return false;
		} else if (!fModule.equals(other.fModule))
			return false;
		return true;
	}
}
