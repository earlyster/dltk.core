/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search.matching;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.indexing.IIndexConstants;

public class QualifiedTypeDeclarationPattern extends TypeDeclarationPattern
		implements IIndexConstants {
	public char[] qualification;
	private PackageDeclarationPattern packagePattern;

	// PackageDeclarationPattern packagePattern;
	// public int packageIndex = -1;

	public QualifiedTypeDeclarationPattern(char[] qualification,
			char[] simpleName, char typeSuffix, int matchRule,
			IDLTKLanguageToolkit toolkit) {
		this(matchRule, toolkit);
		this.qualification = isCaseSensitive() ? qualification : CharOperation
				.toLowerCase(qualification);
		this.simpleName = (isCaseSensitive() || isCamelCase()) ? simpleName
				: CharOperation.toLowerCase(simpleName);
		this.typeSuffix = typeSuffix;
	}

	public QualifiedTypeDeclarationPattern(char[] qualification,
			int qualificationMatchRule, char[] simpleName, char typeSuffix,
			int matchRule, IDLTKLanguageToolkit toolkit) {
		this(qualification, simpleName, typeSuffix, matchRule, toolkit);
		this.packagePattern = new PackageDeclarationPattern(qualification,
				qualificationMatchRule, toolkit);
	}

	QualifiedTypeDeclarationPattern(int matchRule, IDLTKLanguageToolkit toolkit) {
		super(matchRule, toolkit);
	}

	public void decodeIndexKey(char[] key) {
		super.decodeIndexKey(key);
		this.qualification = CharOperation.concatWith(pkg, enclosingTypeNames,
				TYPE_SEPARATOR);
	}

	public SearchPattern getBlankPattern() {
		return new QualifiedTypeDeclarationPattern(R_EXACT_MATCH
				| R_CASE_SENSITIVE, getToolkit());
	}

	public boolean matchesDecodedKey(SearchPattern decodedPattern) {
		QualifiedTypeDeclarationPattern pattern = (QualifiedTypeDeclarationPattern) decodedPattern;
		switch (this.typeSuffix) {
		case TYPE_SUFFIX:
			switch (pattern.typeSuffix) {
			case TYPE_SUFFIX:
				break;
			default:
				return false;
			}
			break;
		case ANNOTATION_TYPE_SUFFIX:
			if (this.typeSuffix != pattern.typeSuffix)
				return false;
			break;
		}

		boolean matchesName = matchesName(this.simpleName, pattern.simpleName);
		if (matchesName) {
			if (this.qualification != null) {
				if (this.packagePattern != null) {
					return this.packagePattern.matchesName(this.qualification,
							pattern.qualification);
				}
				return pattern.matchesName(this.qualification,
						pattern.qualification);
			}
			return true;
		}
		return false;
	}

	protected StringBuffer print(StringBuffer output) {
		switch (this.typeSuffix) {
		case TYPE_SUFFIX:
			output.append("ClassDeclarationPattern: qualification<"); //$NON-NLS-1$
			break;
		case ANNOTATION_TYPE_SUFFIX:
			output.append("AnnotationTypeDeclarationPattern: qualification<"); //$NON-NLS-1$
			break;
		default:
			output.append("TypeDeclarationPattern: qualification<"); //$NON-NLS-1$
			break;
		}
		if (this.qualification != null)
			output.append(this.qualification);
		else
			output.append("*"); //$NON-NLS-1$
		output.append(">, type<"); //$NON-NLS-1$
		if (simpleName != null)
			output.append(simpleName);
		else
			output.append("*"); //$NON-NLS-1$
		output.append("> "); //$NON-NLS-1$
		return super.print(output);
	}
}
