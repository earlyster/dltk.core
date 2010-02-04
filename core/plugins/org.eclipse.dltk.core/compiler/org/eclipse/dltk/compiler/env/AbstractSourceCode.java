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

import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.IModelElement;

public abstract class AbstractSourceCode implements ISourceModule {

	/*
	 * @see org.eclipse.dltk.compiler.env.ISourceModule#getModelElement()
	 */
	public IModelElement getModelElement() {
		return null;
	}

	/*
	 * @see org.eclipse.dltk.compiler.env.IDependent#getFileName()
	 */
	public String getFileName() {
		return Util.EMPTY_STRING;
	}

}
