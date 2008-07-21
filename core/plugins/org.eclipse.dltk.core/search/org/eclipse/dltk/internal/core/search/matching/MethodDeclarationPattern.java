/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search.matching;

import java.io.IOException;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.index.EntryResult;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IIndexConstants;

public class MethodDeclarationPattern extends DLTKSearchPattern implements
		IIndexConstants {
	public char[] simpleName;
	protected static char[][] CATEGORIES = { METHOD_DECL };

	/*
	 * Create index key for type declaration pattern: key = typeName /
	 * packageName / enclosingTypeName / modifiers or for secondary types key =
	 * typeName / packageName / enclosingTypeName / modifiers / 'S'
	 */
	public static char[] createIndexKey(int modifiers, char[] typeName,
			char[] packageName, char[][] enclosingTypeNames) { // ,
		// char
		// typeSuffix)
		// {
		int typeNameLength = typeName == null ? 0 : typeName.length;
		int packageLength = packageName == null ? 0 : packageName.length;
		int enclosingNamesLength = 0;
		if (enclosingTypeNames != null) {
			for (int i = 0, length = enclosingTypeNames.length; i < length;) {
				enclosingNamesLength += enclosingTypeNames[i].length;
				if (++i < length)
					enclosingNamesLength++; // for the '.' separator
			}
		}
		int resultLength = typeNameLength + packageLength
				+ enclosingNamesLength + 5;

		char[] result = new char[resultLength];
		int pos = 0;
		if (typeNameLength > 0) {
			System.arraycopy(typeName, 0, result, pos, typeNameLength);
			pos += typeNameLength;
		}
		result[pos++] = SEPARATOR;
		if (packageLength > 0) {
			System.arraycopy(packageName, 0, result, pos, packageLength);
			pos += packageLength;
		}
		result[pos++] = SEPARATOR;
		if (enclosingTypeNames != null && enclosingNamesLength > 0) {
			for (int i = 0, length = enclosingTypeNames.length; i < length;) {
				char[] enclosingName = enclosingTypeNames[i];
				int itsLength = enclosingName.length;
				System.arraycopy(enclosingName, 0, result, pos, itsLength);
				pos += itsLength;
				if (++i < length)
					result[pos++] = '$';
			}
		}
		result[pos++] = SEPARATOR;
		result[pos++] = (char) modifiers;
		result[pos] = (char) (modifiers >> 16);
		return result;
	}

	public MethodDeclarationPattern(char[] simpleName, int matchRule,
			IDLTKLanguageToolkit toolkit) {
		this(matchRule, toolkit);
		this.simpleName = (isCaseSensitive() || isCamelCase()) ? simpleName
				: CharOperation.toLowerCase(simpleName);
	}

	MethodDeclarationPattern(int matchRule, IDLTKLanguageToolkit toolkit) {
		super(TYPE_DECL_PATTERN, matchRule, toolkit);
	}

	/*
	 * Type entries are encoded as: simpleTypeName / packageName /
	 * enclosingTypeName / modifiers e.g. Object/java.lang//0 e.g.
	 * Cloneable/java.lang//512 e.g. LazyValue/javax.swing/UIDefaults/0 or for
	 * secondary types as: simpleTypeName / packageName / enclosingTypeName /
	 * modifiers / S
	 */
	public void decodeIndexKey(char[] key) {
		int slash = CharOperation.indexOf(SEPARATOR, key, 0);
		this.simpleName = CharOperation.subarray(key, 0, slash);
		//int start = ++slash;
	}

	public SearchPattern getBlankPattern() {
		return new MethodDeclarationPattern(R_EXACT_MATCH | R_CASE_SENSITIVE,
				getToolkit());
	}

	public char[][] getIndexCategories() {
		return CATEGORIES;
	}

	public boolean matchesDecodedKey(SearchPattern decodedPattern) {
		MethodDeclarationPattern pattern = (MethodDeclarationPattern) decodedPattern;
		if (!matchesName(this.simpleName, pattern.simpleName))
			return false;
		return true;
	}

	public EntryResult[] queryIn(Index index) throws IOException {
		char[] key = this.simpleName; // can be null
		int matchRule = getMatchRule();
		switch (getMatchMode()) {
		case R_PREFIX_MATCH:
			// do a prefix query with the simpleName
			break;
		case R_EXACT_MATCH:
			if (this.isCamelCase)
				break;
			matchRule &= ~R_EXACT_MATCH;
			matchRule |= R_PREFIX_MATCH;
			key = CharOperation.append(this.simpleName, SEPARATOR);
			break;
		case R_PATTERN_MATCH:
			if (this.simpleName[this.simpleName.length - 1] != '*') {
				key = CharOperation.concat(this.simpleName, ONE_STAR,
						SEPARATOR);
			}
			break;
		case R_REGEXP_MATCH:
			// TODO (frederic) implement regular expression match
			break;
		}
		return index.query(getIndexCategories(), key, matchRule); 
		// match rule is irrelevant when the key is null
	}

	protected StringBuffer print(StringBuffer output) {
		output.append("MethodDeclarationPattern: "); //$NON-NLS-1$
		output.append("name<"); //$NON-NLS-1$
		if (simpleName != null)
			output.append(simpleName);
		else
			output.append("*"); //$NON-NLS-1$
		output.append(">"); //$NON-NLS-1$
		return super.print(output);
	}
}
