/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search.matching;

import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.ast.expressions.MethodCallExpression;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISearchFactory;
import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.matching.MatchLocator;
import org.eclipse.dltk.core.search.matching.PatternLocator;

public class MethodLocator extends PatternLocator {
	protected MethodPattern pattern;
	protected boolean isDeclarationOfReferencedMethodsPattern;

	public MethodLocator(MethodPattern pattern) {
		super(pattern);
		this.pattern = pattern;
		this.isDeclarationOfReferencedMethodsPattern = this.pattern instanceof DeclarationOfReferencedMethodsPattern;
	}

	/*
	 * Clear caches
	 */
	protected void clear() {
	}

	public void initializePolymorphicSearch(MatchLocator locator) {
	}

	public int match(MethodDeclaration node, MatchingNodeSet nodeSet) {
		if (!this.pattern.findDeclarations)
			return IMPOSSIBLE_MATCH;
		// Verify method name
		if (!matchesName(this.pattern.selector, node.getName().toCharArray()))
			return IMPOSSIBLE_MATCH;
		// Verify parameters types
		if (this.pattern.parameterSimpleNames != null) {
			int length = this.pattern.parameterSimpleNames.length;
			List args = node.getArguments();
			int argsLength = args == null ? 0 : args.size();
			if (length != argsLength)
				return IMPOSSIBLE_MATCH;
		}
		// check type names

		String declaringType = node.getDeclaringTypeName();
		if (checkTypeName(declaringType)) {
			return INACCURATE_MATCH;
		}
		// Verify type arguments (do not reject if pattern has no argument as it
		// can be an erasure match)
		if (this.pattern.hasMethodArguments()) {
			// if (node.typeParameters == null || node.typeParameters.length !=
			// this.pattern.methodArguments.length)
			// return IMPOSSIBLE_MATCH;
		}
		// Method declaration may match pattern
		return nodeSet.addMatch(node, ACCURATE_MATCH);
	}

	// public int match(TypeDeclaration node, MatchingNodeSet nodeSet) - SKIP IT
	// public int match(TypeReference node, MatchingNodeSet nodeSet) - SKIP IT
	public int matchContainer() {
		if (this.pattern.findReferences) {
			// need to look almost everywhere to find in javadocs and static
			// import
			return ALL_CONTAINER;
		}
		return COMPILATION_UNIT_CONTAINER | CLASS_CONTAINER | METHOD_CONTAINER;
	}

	public SearchMatch newDeclarationMatch(ASTNode reference,
			IModelElement element, int accuracy, 
			MatchLocator locator) {
		return super.newDeclarationMatch(reference, element, accuracy, 
				locator);
	}

	protected int referenceType() {
		return IModelElement.METHOD;
	}

	public String toString() {
		return "Locator for " + this.pattern.toString(); //$NON-NLS-1$
	}

	public int match(CallExpression node, MatchingNodeSet nodeSet) {
		if (!this.pattern.findReferences)
			return IMPOSSIBLE_MATCH;

		if (this.pattern.selector == null)
			return nodeSet.addMatch(node, POSSIBLE_MATCH);

		if (this.pattern.declaringSimpleName != null
				&& node instanceof MethodCallExpression) {
			MethodCallExpression mce = (MethodCallExpression) node;
			String declaringType = mce.getDeclaringTypeName();
			if (checkTypeName(declaringType)) {
				return INACCURATE_MATCH;
			}
		}

		if (matchesName(this.pattern.selector, node.getName().toCharArray()))
			return nodeSet.addMatch(node, ACCURATE_MATCH);

		return IMPOSSIBLE_MATCH;
	}

	private boolean checkTypeName(String declaringType) {
		IDLTKLanguageToolkit toolkit = this.pattern.getToolkit();
		ISearchFactory factory = DLTKLanguageManager.getSearchFactory(toolkit
				.getNatureId());
		ISearchPatternProcessor processor = factory
				.createSearchPatternProcessor();
		if (processor != null) {
			if (this.pattern.declaringSimpleName != null) {
				char[] delimeter = processor.getDelimiterReplacementString()
						.toCharArray();
				char[] typeName = CharOperation.concatWithSeparator(
						this.pattern.declaringQualificationName,
						this.pattern.declaringSimpleName, delimeter);
				typeName = CharOperation.replace(typeName, new char[] { '$' },
						delimeter);
				if (declaringType != null) {
					char[] declaringTypeName = declaringType.toCharArray();
					if (!matchesName(typeName, declaringTypeName)) {
						return true;
					}
				} else {
					return true;
				}
			}
		}

		return false;
	}
}
