/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
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

import org.eclipse.dltk.core.IModelElement;

/**
 * This interface denotes a source of a module, providing its name and content.
 * 
 * It was renamed from org.eclipse.dltk.compiler.env.ISourceModule to make names
 * unique.
 * 
 * If the object also implements {@link org.eclipse.dltk.core.ISourceModule},
 * then it could be treated as a source file of the project, otherwise it's just
 * a source code, without any physical location.
 * 
 * @since 2.0
 */
public interface IModuleSource extends IDependent {

	/**
	 * Answer the contents of the source module as string. Should return empty
	 * string on error.
	 */
	String getSourceContents();

	/**
	 * Answer the contents of the source module as char[]. Should return empty
	 * array on error.
	 */
	char[] getContentsAsCharArray();

	/**
	 * Returns the {@link IModelElement} this source module is related to or
	 * <code>null</code> if source is not associated with any model element.
	 * 
	 * It doesn't mean this source code is exactly the same as the code of this
	 * model element, etc
	 */
	IModelElement getModelElement();

}
