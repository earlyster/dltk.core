/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Yuri Strot)
 *******************************************************************************/
package org.eclipse.dltk.core;

public interface ISearchPatternProcessor {

	/**
	 * Delimeter replacement string. For example "::" for tcl, "." for python.
	 */
	String getDelimeterReplacementString();

	// Method pattern operations
	char[] extractDeclaringTypeQualification(String patternString);

	char[] extractDeclaringTypeSimpleName(String patternString);

	char[] extractSelector(String patternString);


	// Type pattern operations
	char[] extractTypeQualification(String patternString);
	char[] extractTypeChars(String patternString);
	// Field pattern operations
}
