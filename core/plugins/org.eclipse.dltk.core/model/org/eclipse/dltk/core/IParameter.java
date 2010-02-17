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
package org.eclipse.dltk.core;

public interface IParameter {

	/**
	 * Returns the parameter name
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Returns the parameter type as source code string or <code>null</code> if
	 * not specified
	 * 
	 * @return
	 */
	String getType();

	/**
	 * Returns the parameter default value as source code string or
	 * <code>null</code> if none
	 * 
	 * @return
	 */
	String getDefaultValue();

}
