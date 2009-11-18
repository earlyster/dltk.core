/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.compiler;

public class SourceElementRequestorAdaptor implements ISourceElementRequestor {

	public void acceptFieldReference(char[] fieldName, int sourcePosition) {
	}

	public void acceptMethodReference(String methodName, int argCount,
			int sourcePosition, int sourceEndPosition) {
	}

	public void acceptPackage(int declarationStart, int declarationEnd,
			String name) {
	}

	public void acceptTypeReference(char[][] typeName, int sourceStart,
			int sourceEnd) {
	}

	public void acceptTypeReference(char[] typeName, int sourcePosition) {
	}

	/**
	 * @since 2.0
	 */
	public void enterField(FieldInfo info) {
	}

	/**
	 * @since 2.0
	 */
	public boolean enterFieldCheckDuplicates(FieldInfo info) {
		return false;
	}

	/**
	 * @since 2.0
	 */
	public boolean enterFieldWithParentType(FieldInfo info, String parentName,
			String delimiter) {
		return false;
	}

	/**
	 * @since 2.0
	 */
	public void enterMethod(MethodInfo info) {
	}

	/**
	 * @since 2.0
	 */
	public void enterMethodRemoveSame(MethodInfo info) {
	}

	/**
	 * @since 2.0
	 */
	public boolean enterMethodWithParentType(MethodInfo info,
			String parentName, String delimiter) {
		return false;
	}

	public void enterModule() {
	}

	public void enterModuleRoot() {
	}

	/**
	 * @since 2.0
	 */
	public void enterType(TypeInfo info) {
	}

	public boolean enterTypeAppend(String fullName, String delimiter) {
		return false;
	}

	public void exitField(int declarationEnd) {
	}

	public void exitMethod(int declarationEnd) {
	}

	public void exitModule(int declarationEnd) {
	}

	public void exitModuleRoot() {
	}

	public void exitType(int declarationEnd) {
	}

	/**
	 * @since 2.0
	 */
	public void acceptImport(ImportInfo importInfo) {
	}
}
