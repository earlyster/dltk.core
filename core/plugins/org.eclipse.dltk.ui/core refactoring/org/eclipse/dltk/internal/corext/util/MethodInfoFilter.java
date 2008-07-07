/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.util;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.search.*;
import org.eclipse.dltk.internal.ui.util.StringMatcher;

public class MethodInfoFilter {

	private static class PatternMatcher {

		private String fPattern;
		private int fMatchKind;
		private StringMatcher fStringMatcher;

		private static final char END_SYMBOL = '<';
		private static final char ANY_STRING = '*';
		private static final char BLANK = ' ';

		public PatternMatcher(String pattern, boolean ignoreCase) {
			this(pattern, SearchPattern.R_EXACT_MATCH
					| SearchPattern.R_PREFIX_MATCH
					| SearchPattern.R_PATTERN_MATCH
					| SearchPattern.R_CAMELCASE_MATCH);
		}

		public PatternMatcher(String pattern, int allowedModes) {
			initializePatternAndMatchKind(pattern);
			fMatchKind = fMatchKind & allowedModes;
			if (fMatchKind == SearchPattern.R_PATTERN_MATCH) {
				fStringMatcher = new StringMatcher(fPattern, true, false);
			}
		}

		public String getPattern() {
			return fPattern;
		}

		public int getMatchKind() {
			return fMatchKind;
		}

		public boolean matches(String text) {
			switch (fMatchKind) {
			case SearchPattern.R_PATTERN_MATCH:
				return fStringMatcher.match(text);
			case SearchPattern.R_EXACT_MATCH:
				return fPattern.equalsIgnoreCase(text);
			case SearchPattern.R_CAMELCASE_MATCH:
				if (SearchPattern.camelCaseMatch(fPattern, text)) {
					return true;
				}
				// fall through to prefix match if camel case failed (bug
				// 137244)
			default:
				return Strings.startsWithIgnoreCase(text, fPattern);
			}
		}

		private void initializePatternAndMatchKind(String pattern) {
			int length = pattern.length();
			if (length == 0) {
				fMatchKind = SearchPattern.R_EXACT_MATCH;
				fPattern = pattern;
				return;
			}
			char last = pattern.charAt(length - 1);

			if (pattern.indexOf('*') != -1 || pattern.indexOf('?') != -1) {
				fMatchKind = SearchPattern.R_PATTERN_MATCH;
				switch (last) {
				case END_SYMBOL:
					fPattern = pattern.substring(0, length - 1);
					break;
				case BLANK:
					fPattern = pattern.trim();
					break;
				case ANY_STRING:
					fPattern = pattern;
					break;
				default:
					fPattern = pattern + ANY_STRING;
				}
				return;
			}

			if (last == END_SYMBOL) {
				fMatchKind = SearchPattern.R_EXACT_MATCH;
				fPattern = pattern.substring(0, length - 1);
				return;
			}

			if (last == BLANK) {
				fMatchKind = SearchPattern.R_EXACT_MATCH;
				fPattern = pattern.trim();
				return;
			}

			if (SearchUtils.isCamelCasePattern(pattern)) {
				fMatchKind = SearchPattern.R_CAMELCASE_MATCH;
				fPattern = pattern;
				return;
			}

			fMatchKind = SearchPattern.R_PREFIX_MATCH;
			fPattern = pattern;
		}
	}

	private String fText;
	private IDLTKSearchScope fSearchScope;
	private boolean fIsWorkspaceScope;
	private int fElementKind;

	private PatternMatcher fPackageMatcher;
	private PatternMatcher fNameMatcher;

	private static final int METHOD_MODIFIERS = 0;

	public MethodInfoFilter(String text, IDLTKSearchScope scope, int elementKind) {
		fText = text;
		fSearchScope = scope;
		fIsWorkspaceScope = fSearchScope.equals(SearchEngine
				.createWorkspaceScope(scope.getLanguageToolkit()));
		fElementKind = elementKind;

		//		int index= text.lastIndexOf("."); //$NON-NLS-1$
		// if (index == -1) {\
		if (DLTKCore.DEBUG) {
			System.err.println("TODO: Add check here..."); //$NON-NLS-1$
		}
		fNameMatcher = new PatternMatcher(text, true);
		// } else {
		// fPackageMatcher= new
		// PatternMatcher(evaluatePackagePattern(text.substring(0, index)),
		// true);
		// String name= text.substring(index + 1);
		// if (name.length() == 0)
		//				name= "*"; //$NON-NLS-1$
		// fNameMatcher= new PatternMatcher(name, true);
		// }
	}

	// /*
	// * Transforms o.e.j to o*.e*.j*
	// */
	// private String evaluatePackagePattern(String s) {
	// StringBuffer buf= new StringBuffer();
	// boolean hasWildCard= false;
	// for (int i= 0; i < s.length(); i++) {
	// char ch= s.charAt(i);
	// if (ch == '.') {
	// if (!hasWildCard) {
	// buf.append('*');
	// }
	// hasWildCard= false;
	// } else if (ch == '*' || ch =='?') {
	// hasWildCard= true;
	// }
	// buf.append(ch);
	// }
	// if (!hasWildCard) {
	// buf.append('*');
	// }
	// return buf.toString();
	// }

	public String getText() {
		return fText;
	}

	public boolean isSubFilter(String text) {
		if (!fText.startsWith(text))
			return false;

		return fText.indexOf('.', text.length()) == -1;
	}

	public boolean isCamelCasePattern() {
		return fNameMatcher.getMatchKind() == SearchPattern.R_CAMELCASE_MATCH;
	}

	public String getPackagePattern() {
		if (fPackageMatcher == null)
			return null;
		return fPackageMatcher.getPattern();
	}

	public String getNamePattern() {
		return fNameMatcher.getPattern();
	}

	public int getSearchFlags() {
		return fNameMatcher.getMatchKind();
	}

	public int getPackageFlags() {
		if (fPackageMatcher == null)
			return SearchPattern.R_EXACT_MATCH;

		return fPackageMatcher.getMatchKind();
	}

	public boolean matchesRawNamePattern(MethodNameMatch method) {
		return Strings.startsWithIgnoreCase(method.getSimpleMethodName(),
				fNameMatcher.getPattern());
	}

	public boolean matchesCachedResult(MethodNameMatch method) {
		if (!(matchesPackage(method) && matchesFilterExtension(method)))
			return false;
		return matchesName(method);
	}

	public boolean matchesHistoryElement(MethodNameMatch method) {
		if (!(matchesPackage(method) && matchesModifiers(method)
				&& matchesScope(method) && matchesFilterExtension(method)))
			return false;
		return matchesName(method);
	}

	public boolean matchesFilterExtension(MethodNameMatch method) {
		return true;
	}

	private boolean matchesName(MethodNameMatch method) {
		return fNameMatcher.matches(method.getSimpleMethodName());
	}

	private boolean matchesPackage(MethodNameMatch method) {
		if (fPackageMatcher == null)
			return true;
		return fPackageMatcher.matches(method.getMethodContainerName());
	}

	private boolean matchesScope(MethodNameMatch method) {
		if (fIsWorkspaceScope)
			return true;
		return fSearchScope.encloses(method.getMethod());
	}

	private boolean matchesModifiers(MethodNameMatch method) {
		if (fElementKind == IDLTKSearchConstants.METHOD)
			return true;
		int modifiers = method.getModifiers() & METHOD_MODIFIERS;
		switch (fElementKind) {
		case IDLTKSearchConstants.METHOD:
			return modifiers == 0;
		}
		return false;
	}
}
