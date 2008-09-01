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
	 * Delimiter replacement string. For example "::" for tcl, "." for python.
	 * 
	 * The returned value is used to replace internal class name separator '$'.
	 * It is used to check the match of the method calls like
	 * "ClassName::methodName()".
	 */
	String getDelimiterReplacementString();

	/**
	 * Extracts the "package" from the specified method search pattern. If the
	 * pattern have no type the method should return <code>null</code>.
	 * 
	 * @param patternString
	 * @return
	 */
	char[] extractDeclaringTypeQualification(String patternString);

	/**
	 * Extracts the "base" class name from the specified method search pattern.
	 * If the pattern have no type the method should return <code>null</code>.
	 * 
	 * @param patternString
	 * @return
	 */
	char[] extractDeclaringTypeSimpleName(String patternString);

	/**
	 * Extracts the method name from the specified method search pattern. If the
	 * pattern have no type - just return it as is.
	 * 
	 * @param patternString
	 * @return
	 */
	char[] extractSelector(String patternString);

	/**
	 * Extracts the "package" from the specified full type name. If the
	 * specified pattern have no package - the method should return
	 * <code>null</code>.
	 * 
	 * @param patternString
	 * @return
	 */
	char[] extractTypeQualification(String patternString);

	/**
	 * Extracts the "base" class name from the specified full type name. If the
	 * specified pattern have no package - it should be returned as is.
	 * 
	 * @param patternString
	 * @return
	 */
	char[] extractTypeChars(String patternString);

}
