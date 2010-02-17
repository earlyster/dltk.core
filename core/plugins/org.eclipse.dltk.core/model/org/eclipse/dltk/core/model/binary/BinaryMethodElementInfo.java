/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.model.binary;

import org.eclipse.dltk.compiler.env.IGenericMethod;
import org.eclipse.dltk.core.IParameter;
import org.eclipse.dltk.internal.core.SourceMethodUtils;

class BinaryMethodElementInfo extends BinaryMemberInfo implements
		IGenericMethod {

	private IParameter[] arguments;
	private boolean isConstructor;
	private String returnType;

	protected void setArguments(IParameter[] arguments) {
		this.arguments = arguments;
	}

	public IParameter[] getArguments() {
		return this.arguments;
	}

	/*
	 * @see org.eclipse.dltk.compiler.env.IGenericMethod#getModifiers()
	 */
	public int getModifiers() {
		return getFlags();
	}

	public String[] getArgumentNames() {
		return SourceMethodUtils.getParameterNames(arguments);
	}

	public void setIsConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public boolean isConstructor() {
		return isConstructor;
	}

	public String getReturnTypeName() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
}
