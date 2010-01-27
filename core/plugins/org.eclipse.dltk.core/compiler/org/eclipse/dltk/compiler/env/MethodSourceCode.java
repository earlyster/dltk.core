/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.compiler.env;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;

public class MethodSourceCode implements ISourceModule {

	private final IMethod method;

	/**
	 * @param method
	 */
	public MethodSourceCode(IMethod method) {
		this.method = method;
	}

	/*
	 * @see org.eclipse.dltk.compiler.env.ISourceModule#getContentsAsCharArray()
	 */
	public char[] getContentsAsCharArray() {
		try {
			return method.getSource().toCharArray();
		} catch (ModelException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			return CharOperation.NO_CHAR;
		}
	}

	/*
	 * @see org.eclipse.dltk.compiler.env.ISourceModule#getModelElement()
	 */
	public IModelElement getModelElement() {
		return method;
	}

	/*
	 * @see org.eclipse.dltk.compiler.env.ISourceModule#getScriptFolder()
	 */
	public IPath getScriptFolder() {
		return method.getSourceModule().getPath().removeLastSegments(1);
	}

	/*
	 * @see org.eclipse.dltk.compiler.env.ISourceModule#getSourceContents()
	 */
	public String getSourceContents() {
		try {
			return method.getSource();
		} catch (ModelException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			return Util.EMPTY_STRING;
		}
	}

	/*
	 * @see org.eclipse.dltk.compiler.env.IDependent#getFileName()
	 */
	public String getFileName() {
		return method.getSourceModule().getPath().toString();
	}

}
