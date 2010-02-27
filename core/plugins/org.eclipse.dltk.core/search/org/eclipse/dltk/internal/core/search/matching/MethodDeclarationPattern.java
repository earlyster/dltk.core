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
	public char[] pkg;
	public char[][] enclosingTypeNames;
	// set to CLASS_SUFFIX for only matching classes
	// set to INTERFACE_SUFFIX for only matching interfaces
	// set to ENUM_SUFFIX for only matching enums
	// set to ANNOTATION_TYPE_SUFFIX for only matching annotation types
	// set to TYPE_SUFFIX for matching both classes and interfaces
	public char typeSuffix;
	public int modifiers;
	public boolean secondary = false;
	public char[][] parameterNames;
	protected static char[][] CATEGORIES = { METHOD_DECL };

	/*
	 * Create index key for method declaration pattern: key = methodName /
	 * packageName / enclosingTypeName / modifiers or for secondary types key =
	 * typeName / packageName / enclosingTypeName / modifiers / 'S'
	 */
	public static char[] createIndexKey(int modifiers, char[] methodName,
			String[] parameterNames, char[] packageName,
			String[] enclosingTypeNames) {

		int typeNameLength = methodName == null ? 0 : methodName.length;

		int parameterNamesLength = 0;
		if (parameterNames != null) {
			for (int i = 0, length = parameterNames.length; i < length;) {
				parameterNamesLength += parameterNames[i].length();
				if (++i < length)
					parameterNamesLength++; // for the '.' separator
			}
		}

		int packageLength = packageName == null ? 0 : packageName.length;
		int enclosingNamesLength = 0;
		if (enclosingTypeNames != null) {
			for (int i = 0, length = enclosingTypeNames.length; i < length;) {
				enclosingNamesLength += enclosingTypeNames[i].length();
				if (++i < length)
					enclosingNamesLength++; // for the '.' separator
			}
		}
		int resultLength = typeNameLength + parameterNamesLength
				+ packageLength + enclosingNamesLength + 6;

		char[] result = new char[resultLength];
		int pos = 0;
		if (typeNameLength > 0) {
			System.arraycopy(methodName, 0, result, pos, typeNameLength);
			pos += typeNameLength;
		}
		result[pos++] = SEPARATOR;
		if (parameterNames != null && parameterNamesLength > 0) {
			for (int i = 0, length = parameterNames.length; i < length;) {
				char[] parameterName = parameterNames[i].toCharArray();
				int itsLength = parameterName.length;
				System.arraycopy(parameterName, 0, result, pos, itsLength);
				pos += itsLength;
				if (++i < length)
					result[pos++] = ',';
			}
		}
		result[pos++] = SEPARATOR;
		if (packageLength > 0) {
			System.arraycopy(packageName, 0, result, pos, packageLength);
			pos += packageLength;
		}
		result[pos++] = SEPARATOR;
		if (enclosingTypeNames != null && enclosingNamesLength > 0) {
			for (int i = 0, length = enclosingTypeNames.length; i < length;) {
				String enclosingName = enclosingTypeNames[i];
				int itsLength = enclosingName.length();
				enclosingName.getChars(0, itsLength, result, pos);
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

	public MethodDeclarationPattern(char[][] enclosingTypeNames,
			char[] simpleName, int matchRule, IDLTKLanguageToolkit toolkit) {
		this(simpleName, matchRule, toolkit);
		this.enclosingTypeNames = enclosingTypeNames;
	}

	MethodDeclarationPattern(int matchRule, IDLTKLanguageToolkit toolkit) {
		super(METHOD_DECL_PATTERN, matchRule, toolkit);
	}

	/*
	 * Method entries are encoded as: simlpleMethodName / packageName /
	 * enclosingTypeName / modifiers e.g. Object/java.lang//0 e.g.
	 * Cloneable/java.lang//512 e.g. LazyValue/javax.swing/UIDefaults/0 or for
	 * secondary types as: simlpleMethodName / packageName / enclosingTypeName /
	 * modifiers / S
	 */
	public void decodeIndexKey(char[] key) {
		int slash = CharOperation.indexOf(SEPARATOR, key, 0);
		this.simpleName = CharOperation.subarray(key, 0, slash);
		int start = ++slash;

		// read parameter names:
		if (key[start] == SEPARATOR) {
			this.parameterNames = CharOperation.NO_CHAR_CHAR;
		} else {
			slash = CharOperation.indexOf(SEPARATOR, key, start);
			if (start == slash) {
				this.parameterNames = CharOperation.NO_CHAR_CHAR;
			} else {
				this.parameterNames = CharOperation.splitOn(',', key, start,
						slash);
			}
		}
		start = ++slash;

		if (key[start] == SEPARATOR) {
			this.pkg = CharOperation.NO_CHAR;
		} else {
			slash = CharOperation.indexOf(SEPARATOR, key, start);
			this.pkg = internedPackageNames.add(CharOperation.subarray(key,
					start, slash));
		}
		// Continue key read by the end to decode modifiers
		int last = key.length - 1;
		this.secondary = key[last] == 'S';
		if (this.secondary) {
			last -= 2;
		}
		if (last > 0)
			this.modifiers = key[last - 1] + (key[last] << 16);
		else
			this.modifiers = 0;
		decodeModifiers();
		// Retrieve enclosing type names
		start = slash + 1;
		last -= 2; // position of ending slash
		if (start == last) {
			this.enclosingTypeNames = CharOperation.NO_CHAR_CHAR;
		} else {
			if (last == (start + 1) && key[start] == ZERO_CHAR) {
				this.enclosingTypeNames = ONE_ZERO_CHAR;
			} else {
				this.enclosingTypeNames = CharOperation.splitOn('$', key,
						start, last);
			}
		}
	}

	protected void decodeModifiers() {
		this.typeSuffix = TYPE_SUFFIX;
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

		if (enclosingTypeNames != null) {
			if (pattern.enclosingTypeNames == null
					|| pattern.enclosingTypeNames.length == 0
					|| !matchesName(
							enclosingTypeNames[enclosingTypeNames.length - 1],
							pattern.enclosingTypeNames[pattern.enclosingTypeNames.length - 1])) {
				return false;
			}
		}
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
				key = CharOperation
						.concat(this.simpleName, ONE_STAR, SEPARATOR);
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
