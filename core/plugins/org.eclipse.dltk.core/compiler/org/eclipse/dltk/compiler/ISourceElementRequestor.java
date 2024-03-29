/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.compiler;

public interface ISourceElementRequestor extends IElementRequestor {
	/**
	 * Adds selected field only if it isn't already added. If field is added
	 * into a method, then field name is also compared with the method
	 * parameters names.
	 * 
	 * @param info
	 * @return <code>true</code> if field has been just added or
	 *         <code>false</code> if another field with the same was found.
	 * @since 2.0
	 */
	boolean enterFieldCheckDuplicates(IElementRequestor.FieldInfo info);

	/**
	 * equivalent to enterMethod except for removing previous declared methods
	 * with same name.
	 * 
	 * @param info
	 * @since 2.0
	 */
	void enterMethodRemoveSame(IElementRequestor.MethodInfo info);

	/**
	 * If type with same name already exist, then enter it instead.
	 * 
	 * @param info
	 * @return boolean false if no such type found.
	 */
	boolean enterTypeAppend(String fullName, String delimiter);

	static int UPDATE_TYPE = 1;

	/**
	 * Updates the field information. The particular details to be updated are
	 * specified via <code>flags</code> parameter. To be used in cases when you
	 * can't evaluate all the details at once.
	 * 
	 * EXPERIMENTAL
	 * 
	 * @param fieldInfo
	 * @param flags
	 *            the bit-wise or of update constants {@link #UPDATE_TYPE}
	 */
	void updateField(FieldInfo fieldInfo, int flags);

}
