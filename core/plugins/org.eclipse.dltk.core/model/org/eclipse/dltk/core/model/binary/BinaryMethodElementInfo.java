/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.model.binary;

class BinaryMethodElementInfo extends BinaryMemberInfo {

	private String[] argumentNames;
	private String[] argumentInitializers;
	private String[] argumentTypes;
	private boolean isConstructor;
	private String returnType;

	protected void setArgumentNames(String[] names) {
		this.argumentNames = names;
	}

	public String[] getArgumentNames() {
		return this.argumentNames;
	}

	protected void setArgumentInializers(String[] initializers) {
		this.argumentInitializers = initializers;
	}

	public String[] getArgumentInitializers() {
		return this.argumentInitializers;
	}

	public void setArgumentTypes(String[] argumentTypes) {
		this.argumentTypes = argumentTypes;
	}

	public String[] getArgumentTypes() {
		return argumentTypes;
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
