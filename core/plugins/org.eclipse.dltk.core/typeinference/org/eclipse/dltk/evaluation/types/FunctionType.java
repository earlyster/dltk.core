/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.evaluation.types;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ti.types.IEvaluatedType;

public class FunctionType implements IFunctionType {
	
	private MethodDeclaration fMethod;
	private ModuleDeclaration fModule;

	private boolean fWrongCall = false; // set to true then call with diffirent
										// arguments.

	public FunctionType(ModuleDeclaration module, MethodDeclaration method) {
		this.fMethod = method;
		this.fModule = module;
	}

	public FunctionType(ModuleDeclaration module, MethodDeclaration method,
			boolean wrongCall) {
		this.fMethod = method;
		this.fModule = module;
		this.fWrongCall = wrongCall;
	}

	public String getTypeName() {
		String add = ""; //$NON-NLS-1$
		if (this.fWrongCall) {
			add = " wrong arguments call"; //$NON-NLS-1$
		}
		if (fMethod != null) {
			return "function:" + fMethod.getName() + add; //$NON-NLS-1$
		}
		return "function: !!unknown!!"; //$NON-NLS-1$
	}

	public ASTNode getFunction() {
		return this.fMethod;
	}

	public boolean subtypeOf(IEvaluatedType type) {
		// TODO Auto-generated method stub
		return false;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fMethod == null) ? 0 : fMethod.hashCode());
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
		FunctionType other = (FunctionType) obj;
		if (fMethod == null) {
			if (other.fMethod != null)
				return false;
		} else if (!fMethod.equals(other.fMethod))
			return false;
		if (fModule == null) {
			if (other.fModule != null)
				return false;
		} else if (!fModule.equals(other.fModule))
			return false;
		return true;
	}
}
