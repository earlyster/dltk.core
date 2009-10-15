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

class SourceMethodElementInfo extends MemberElementInfo implements
		ISourceMethod {

	/**
	 * For a source method (that is, a method contained in a source module) this
	 * is a collection of the names of the parameters for this method, in the
	 * order the parameters are delcared.
	 */
	private String[] argumentNames;
	private String[] argumentInitializers;
	private boolean isConstructor;
	private String type;

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
