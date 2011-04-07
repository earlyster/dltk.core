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

public class TypeDeclarationPattern extends DLTKSearchPattern implements
		IIndexConstants {
	public char[] simpleName;
	public char[] pkg;
	public char[][] enclosingTypeNames;
	public char[][] superTypes;
	// set to CLASS_SUFFIX for only matching classes
	// set to INTERFACE_SUFFIX for only matching interfaces
	// set to ENUM_SUFFIX for only matching enums
	// set to ANNOTATION_TYPE_SUFFIX for only matching annotation types
	// set to TYPE_SUFFIX for matching both classes and interfaces
	public char typeSuffix;
	public int modifiers;
	public boolean secondary = false;
	protected static char[][] CATEGORIES = { TYPE_DECL };

	/*
	 * Create index key for type declaration pattern: key = typeName /
	 * packageName / enclosingTypeName / modifiers or for secondary types key =
	 * typeName / packageName / enclosingTypeName / modifiers / 'S'
	 */
	public static char[] createIndexKey(int modifiers, String typeName,
			String[] packageName, String[] enclosingTypeNames,
			char[][] superTypes) {
		// , char typeSuffix) {
		int typeNameLength = typeName == null ? 0 : typeName.length();
		int packageLength = indexKeyLength(packageName);
		int enclosingNamesLength = indexKeyLength(enclosingTypeNames);
		int superTypesLength = 0;
		if (superTypes != null) {
			for (int i = 0, length = superTypes.length; i < length;) {
				superTypesLength += superTypes[i].length;
				if (++i < length)
					superTypesLength++; // for the '.' separator
			}
		}
		int resultLength = typeNameLength + packageLength
				+ enclosingNamesLength + superTypesLength + 6;

		char[] result = new char[resultLength];
		int pos = 0;
		if (typeNameLength > 0) {
			typeName.getChars(0, typeNameLength, result, pos);
			pos += typeNameLength;
		}
		result[pos++] = SEPARATOR;
		pos = encodeNames(packageName, packageLength, result, pos);
		result[pos++] = SEPARATOR;
		pos = encodeNames(enclosingTypeNames, enclosingNamesLength, result, pos);
		result[pos++] = SEPARATOR;
		if (superTypes != null && superTypesLength > 0) {
			for (int i = 0, length = superTypes.length; i < length;) {
				char[] superType = superTypes[i];
				int itsLength = superType.length;
				System.arraycopy(superType, 0, result, pos, itsLength);
				pos += itsLength;
				if (++i < length)
					result[pos++] = ',';
			}
		}
		result[pos++] = SEPARATOR;
		result[pos++] = (char) modifiers;
		result[pos] = (char) (modifiers >> 16);
		return result;
	}

	public TypeDeclarationPattern(char[] pkg, char[][] enclosingTypeNames,
			char[][] superTypes, char[] simpleName, char typeSuffix,
			int matchRule, IDLTKLanguageToolkit toolkit) {
		this(matchRule, toolkit);
		this.pkg = isCaseSensitive() ? pkg : CharOperation.toLowerCase(pkg);
		if (isCaseSensitive() || enclosingTypeNames == null) {
			this.enclosingTypeNames = enclosingTypeNames;
		} else {
			this.enclosingTypeNames = CharOperation
					.toLowerCase(enclosingTypeNames);
		}
		this.simpleName = (isCaseSensitive() || isCamelCase()) ? simpleName
				: CharOperation.toLowerCase(simpleName);
		this.typeSuffix = typeSuffix;
		this.superTypes = superTypes;
	}

	TypeDeclarationPattern(int matchRule, IDLTKLanguageToolkit toolkit) {
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
		int start = ++slash;
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
		start = ++slash;
		last -= 2; // position of ending slash
		if (start == last) {
			this.enclosingTypeNames = CharOperation.NO_CHAR_CHAR;
		} else {
			slash = CharOperation.indexOf(SEPARATOR, key, start);
			if (start == slash) {
				this.enclosingTypeNames = CharOperation.NO_CHAR_CHAR;
			} else if (slash == start + 1 && key[start] == ZERO_CHAR) {
				enclosingTypeNames = ONE_ZERO_CHAR;
			} else {
				this.enclosingTypeNames = CharOperation.splitOn('$', key,
						start, slash);
			}
		}

		// retrieve super types:
		start = ++slash;
		if (start == last) {
			this.superTypes = CharOperation.NO_CHAR_CHAR;
		} else {
			if (last == (start + 1) && key[start] == ZERO_CHAR) {
				this.superTypes = ONE_ZERO_CHAR;
			} else {
				this.superTypes = CharOperation.splitOn(',', key, start, last);
			}
		}
	}

	protected void decodeModifiers() {
		this.typeSuffix = TYPE_SUFFIX;
	}

	public SearchPattern getBlankPattern() {
		return new TypeDeclarationPattern(R_EXACT_MATCH | R_CASE_SENSITIVE,
				getToolkit());
	}

	public char[][] getIndexCategories() {
		return CATEGORIES;
	}

	public boolean matchesDecodedKey(SearchPattern decodedPattern) {
		TypeDeclarationPattern pattern = (TypeDeclarationPattern) decodedPattern;
		switch (this.typeSuffix) {
		case ANNOTATION_TYPE_SUFFIX:
			if (this.typeSuffix != pattern.typeSuffix)
				return false;
			break;
		case TYPE_SUFFIX:
			switch (pattern.typeSuffix) {
			case TYPE_SUFFIX:
				break;
			default:
				return false;
			}
			break;
		}
		if (!matchesName(this.simpleName, pattern.simpleName))
			return false;
		// check package - exact match only
		if (this.pkg != null
				&& !CharOperation.equals(this.pkg, pattern.pkg,
						isCaseSensitive()))
			return false;
		// check enclosingTypeNames - exact match only
		if (this.enclosingTypeNames != null) {
			if (this.enclosingTypeNames.length == 0)
				return pattern.enclosingTypeNames.length == 0;
			if (this.enclosingTypeNames.length == 1
					&& pattern.enclosingTypeNames.length == 1)
				return CharOperation.equals(this.enclosingTypeNames[0],
						pattern.enclosingTypeNames[0], isCaseSensitive());
			if (pattern.enclosingTypeNames == ONE_ZERO_CHAR)
				return true; // is a local or anonymous type
			return CharOperation.equals(this.enclosingTypeNames,
					pattern.enclosingTypeNames, isCaseSensitive());
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
			if (this.simpleName != null) {
				matchRule |= R_PREFIX_MATCH;
				key = this.pkg == null ? CharOperation.append(this.simpleName,
						SEPARATOR) : CharOperation.concat(this.simpleName,
						SEPARATOR, this.pkg, SEPARATOR, CharOperation.NO_CHAR);
				break; // do a prefix query with the simpleName and
				// possibly the pkg
			}
			matchRule |= R_PATTERN_MATCH;
			// fall thru to encode the key and do a pattern query
		case R_PATTERN_MATCH:
			if (this.pkg == null) {
				if (this.simpleName == null) {
					switch (this.typeSuffix) {
					case TYPE_SUFFIX:
					case ANNOTATION_TYPE_SUFFIX:
						break;
					}
				} else if (this.simpleName[this.simpleName.length - 1] != '*') {
					key = CharOperation.concat(this.simpleName, ONE_STAR,
							SEPARATOR);
				}
				break; // do a pattern query with the current encoded key
			}
			// must decode to check enclosingTypeNames due to the encoding
			// of local types
			key = CharOperation
					.concat(this.simpleName == null ? ONE_STAR
							: this.simpleName, SEPARATOR, this.pkg, SEPARATOR,
							ONE_STAR);
			break;
		case R_REGEXP_MATCH:
			// TODO (frederic) implement regular expression match
			break;
		}
		return index.query(getIndexCategories(), key, matchRule); // match
		// rule is
		// irrelevant
		// when the
		// key is
		// null
	}

	protected StringBuffer print(StringBuffer output) {
		switch (this.typeSuffix) {
		case TYPE_SUFFIX:
			output.append("TypeDeclarationPattern: pkg<"); //$NON-NLS-1$
			break;
		case ANNOTATION_TYPE_SUFFIX:
			output.append("AnnotationTypeDeclarationPattern: pkg<"); //$NON-NLS-1$
			break;
		default:
			output.append("TypeDeclarationPattern: pkg<"); //$NON-NLS-1$
			break;
		}
		if (pkg != null)
			output.append(pkg);
		else
			output.append("*"); //$NON-NLS-1$
		output.append(">, enclosing<"); //$NON-NLS-1$
		if (enclosingTypeNames != null) {
			for (int i = 0; i < enclosingTypeNames.length; i++) {
				output.append(enclosingTypeNames[i]);
				if (i < enclosingTypeNames.length - 1)
					output.append('$');
			}
		} else {
			output.append("*"); //$NON-NLS-1$
		}
		output.append(">, type<"); //$NON-NLS-1$
		if (simpleName != null)
			output.append(simpleName);
		else
			output.append("*"); //$NON-NLS-1$
		output.append(">"); //$NON-NLS-1$
		return super.print(output);
	}
}
