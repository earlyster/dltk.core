/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.search.indexing;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.search.SearchDocument;
import org.eclipse.dltk.internal.core.search.matching.FieldPattern;
import org.eclipse.dltk.internal.core.search.matching.MethodDeclarationPattern;
import org.eclipse.dltk.internal.core.search.matching.MethodPattern;
import org.eclipse.dltk.internal.core.search.matching.SuperTypeReferencePattern;
import org.eclipse.dltk.internal.core.search.matching.TypeDeclarationPattern;

public abstract class AbstractIndexer implements IIndexConstants {

	protected SearchDocument document;

	public AbstractIndexer(SearchDocument document) {
		this.document = document;
	}

	public void addTypeDeclaration(int modifiers, char[] packageName,
			String name, char[][] enclosingTypeNames, String[] superclasss) {

		char[] indexKey = TypeDeclarationPattern.createIndexKey(modifiers, name
				.toCharArray(), packageName, enclosingTypeNames, CharOperation
				.stringArrayToCharCharArray(superclasss));
		addIndexEntry(TYPE_DECL, indexKey);
		//
		if (superclasss != null) {
			// for (int i = 0; i < superclasss.length; ++i) {
			// char[] superclass = erasure(superclasss[i].toCharArray());
			// addTypeReference(superclass);
			// }
			for (int i = 0, max = superclasss.length; i < max; i++) {
				String superClass = erasure(superclasss[i]);
				addTypeReference(superClass);
				addIndexEntry(SUPER_REF, SuperTypeReferencePattern
						.createIndexKey(modifiers, packageName, name
								.toCharArray(), enclosingTypeNames, null,
								TYPE_SUFFIX, superClass.toCharArray(),
								TYPE_SUFFIX));

			}
		}
	}

	private String erasure(String typeName) {
		return typeName;
	}

	public void addConstructorDeclaration(String typeName,
			String[] parameterTypes, String[] exceptionTypes) {
		// int argCount = parameterTypes == null ? 0 : parameterTypes.length;
		// addIndexEntry(CONSTRUCTOR_DECL,
		// ConstructorPattern.createIndexKey(CharOperation.lastSegment(typeName,
		// '.'),
		// argCount));
		//	
		// if (parameterTypes != null) {
		// for (int i = 0; i < argCount; i++)
		// addTypeReference(parameterTypes[i]);
		// }
		// if (exceptionTypes != null)
		// for (int i = 0, max = exceptionTypes.length; i < max; i++)
		// addTypeReference(exceptionTypes[i]);
	}

	public void addConstructorReference(char[] typeName, int argCount) {
		// char[] simpleTypeName = CharOperation.lastSegment(typeName,'.');
		// addTypeReference(simpleTypeName);
		// addIndexEntry(CONSTRUCTOR_REF,
		// ConstructorPattern.createIndexKey(simpleTypeName, argCount));
		// char[] innermostTypeName =
		// CharOperation.lastSegment(simpleTypeName,'$');
		// if (innermostTypeName != simpleTypeName)
		// addIndexEntry(CONSTRUCTOR_REF,
		// ConstructorPattern.createIndexKey(innermostTypeName, argCount));
	}

	public void addFieldDeclaration(String fieldName, String typeName) {
		addIndexEntry(FIELD_DECL, FieldPattern.createIndexKey(fieldName));
		if (typeName != null)
			addTypeReference(typeName);
	}

	public void addFieldReference(String fieldName) {
		addNameReference(fieldName);
	}

	protected void addIndexEntry(char[] category, char[] key) {
		this.document.addIndexEntry(category, key);
	}

	public void addMethodDeclaration(int modifiers, char[] packageName,
			String[] enclosingTypeNames, String methodName,
			String[] parameterNames, String[] exceptionTypes) {

		addIndexEntry(METHOD_DECL, MethodDeclarationPattern.createIndexKey(
				modifiers, methodName.toCharArray(), parameterNames,
				packageName, enclosingTypeNames));

		// if (parameterNames != null) {
		// for (int i = 0; i < argCount; i++)
		// addNameReference((parameterNames[i]).toCharArray());
		// }
		// if (exceptionTypes != null)
		// for (int i = 0, max = exceptionTypes.length; i < max; i++)
		// addTypeReference(exceptionTypes[i]);
		// if (returnType != null)
		// addTypeReference(returnType);
	}

	public void addMethodReference(String methodName, int argCount) {
		addIndexEntry(METHOD_REF, MethodPattern.createIndexKey(methodName
				.toCharArray(), argCount));
	}

	public void addNameReference(String name) {
		addIndexEntry(REF, name.toCharArray());
	}

	public void addTypeReference(String typeName) {
		addNameReference(typeName);
	}

	public abstract void indexDocument();

}
