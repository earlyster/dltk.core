/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.dltk.core.search.matching;

import java.util.Iterator;

import org.eclipse.dltk.ast.ASTListNode;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.references.TypeReference;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.search.IMatchLocatorParser;
import org.eclipse.dltk.internal.core.search.matching.MatchingNodeSet;

public abstract class MatchLocatorParser implements IMatchLocatorParser {
	private MatchLocator matchLocator;
	private PatternLocator patternLocator;

	private MatchingNodeSet nodeSet;

	public ModuleDeclaration parse(PossibleMatch possibleMatch) {
		ModuleDeclaration module = SourceParserUtil.getModuleDeclaration(
				(org.eclipse.dltk.core.ISourceModule) possibleMatch
						.getModelElement(), null);
		return module;
	}

	public void parseBodies(ModuleDeclaration unit) {
		try {
			unit.traverse(getMatchVisitor());
		} catch (Exception e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	public void setNodeSet(MatchingNodeSet nodeSet) {
		this.nodeSet = nodeSet;
	}

	protected MatchingNodeSet getNodeSet() {
		return nodeSet;
	}

	protected MatchLocatorParser(MatchLocator locator) {
		this.matchLocator = locator;
		this.patternLocator = locator.patternLocator;
	}

	protected MatchLocator getMatchLocator() {
		return matchLocator;
	}

	protected PatternLocator getPatternLocator() {
		return patternLocator;
	}

	public MethodDeclaration processMethod(MethodDeclaration m) {
		return m;
	}

	public TypeDeclaration processType(TypeDeclaration t) {
		return t;
	}

	protected void processStatement(ASTNode node, PatternLocator locator) {
		// empty implementation
	}

	protected MatchVisitor getMatchVisitor() {
		return new MatchVisitor();
	}

	private boolean patternProcessorInitialized = false;
	protected ISearchPatternProcessor patternProcessor = null;

	protected void initPatternProcessor() {
		if (patternProcessorInitialized) {
			return;
		}
		patternProcessorInitialized = true;
		patternProcessor = DLTKLanguageManager
				.getSearchPatternProcessor(matchLocator.scope
						.getLanguageToolkit());
	}

	protected void visitTypeDeclaration(TypeDeclaration t) {
		patternLocator.match(processType(t), nodeSet);
		final ASTListNode supers = t.getSuperClasses();
		if (supers != null) {
			for (Iterator i = supers.getChilds().iterator(); i.hasNext();) {
				final ASTNode superClass = (ASTNode) i.next();
				final TypeReference superRef = createSuperTypeReference(t,
						superClass);
				if (superRef != null) {
					patternLocator.match(superRef, nodeSet);
				}
			}
		}
	}

	protected TypeReference createSuperTypeReference(TypeDeclaration t,
			final ASTNode superClass) {
		String name = t.resolveSuperClassReference(superClass);
		if (name != null) {
			initPatternProcessor();
			if (patternProcessor != null) {
				name = patternProcessor.extractTypeChars(name);
			}
			// TODO create QualifiedTypeReference if needed
			return new TypeReference(superClass.sourceStart(),
					superClass.sourceEnd(), name);
		} else {
			return null;
		}
	}

	protected class MatchVisitor extends ASTVisitor {
		public boolean visitGeneral(ASTNode node) throws Exception {
			processStatement(node, getPatternLocator());
			return super.visitGeneral(node);
		}

		public boolean visit(MethodDeclaration m) throws Exception {
			getPatternLocator().match(processMethod(m), getNodeSet());
			return super.visit(m);
		}

		public boolean visit(TypeDeclaration t) throws Exception {
			visitTypeDeclaration(t);
			return super.visit(t);
		}
	}
}
