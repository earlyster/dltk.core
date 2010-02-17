/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import org.eclipse.dltk.compiler.env.ISourceMethod;
import org.eclipse.dltk.core.IParameter;

class SourceMethodElementInfo extends MemberElementInfo implements
		ISourceMethod {

	/**
	 * For a source method (that is, a method contained in a source module) this
	 * is a collection of the names of the parameters for this method, in the
	 * order the parameters are delcared.
	 */
	private IParameter[] arguments;
	private boolean isConstructor;
	private String type;

	public String[] getArgumentNames() {
		return SourceMethodUtils.getParameterNames(arguments);
	}

	public IParameter[] getArguments() {
		return arguments;
	}

	public void setArguments(IParameter[] params) {
		this.arguments = params;
	}

	public void setIsConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public boolean isConstructor() {
		return isConstructor;
	}

	public String getReturnTypeName() {
		return type;
	}

	public void setReturnType(String type) {
		this.type = type;
	}
}
