/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.codeassist;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.DLTKCore;

public class AssistParser implements IAssistParser {

	private final IAssistParser parser;

	public AssistParser(IAssistParser parser) {
		this.parser = parser;
	}

	public ASTNode getAssistNodeParent() {
		return parser.getAssistNodeParent();
	}

	public ModuleDeclaration getModule() {
		return parser.getModule();
	}

	public void handleNotInElement(ASTNode unit, int position) {
		parser.handleNotInElement(unit, position);
	}

	public ModuleDeclaration parse(IModuleSource sourceModule) {
		return parser.parse(sourceModule);
	}

	public void parseBlockStatements(ASTNode node, ASTNode unit, int position) {
		parser.parseBlockStatements(node, unit, position);
	}

	public void setSource(ModuleDeclaration unit) {
		parser.setSource(unit);
	}

	/*
	 * Find the node (a field, a method or an initializer) at the given position
	 * and parse its block statements if it is a method or an initializer.
	 * Returns the node or null if not found
	 */
	public ASTNode parseBlockStatements(ModuleDeclaration unit, int position) {
		TypeDeclaration types[] = unit.getTypes();
		int length = types.length;
		for (int i = 0; i < length; i++) {
			TypeDeclaration type = types[i];
			if (type.sourceStart() <= position && type.sourceEnd() >= position) {
				parser.setSource(unit);
				return parseBlockStatements(type, unit, position);
			}
		}
		MethodDeclaration[] methods = unit.getFunctions();
		length = methods.length;
		for (int i = 0; i < length; i++) {
			MethodDeclaration method = methods[i];
			if (method.sourceStart() <= position
					&& method.sourceEnd() >= position) {
				parser.setSource(unit);
				return parseMethod(method, unit, position);
			}
		}

		ASTNode[] nodes = unit.getNonTypeOrMethodNode();
		length = nodes.length;
		for (int i = 0; i < length; i++) {
			ASTNode node = nodes[i];
			if (node.sourceStart() <= position && node.sourceEnd() >= position) {
				parser.setSource(unit);
				parser.parseBlockStatements(node, unit, position);
				return node;
			}
		}
		parser.handleNotInElement(unit, position);
		// Non type elements
		return null;
	}

	public ASTNode parseBlockStatements(TypeDeclaration type,
			ModuleDeclaration unit, int position) {
		// members
		TypeDeclaration[] memberTypes = type.getTypes();
		if (memberTypes != null) {
			int length = memberTypes.length;
			for (int i = 0; i < length; i++) {
				TypeDeclaration memberType = memberTypes[i];
				if (memberType.getNameStart() <= position
						&& memberType.getNameEnd() >= position) {
					parser.handleNotInElement(memberType, position);
				}
				if (memberType.sourceStart() > position)
					continue;
				if (memberType.sourceEnd() >= position) {
					return parseBlockStatements(memberType, unit, position);
				}
			}
		}
		// methods
		MethodDeclaration[] methods = type.getMethods();
		if (methods != null) {
			int length = methods.length;
			for (int i = 0; i < length; i++) {
				MethodDeclaration method = methods[i];
				ASTNode node = parseMethod(method, unit, position);
				if (node != null) {
					return node;
				}
			}
		}
		ASTNode[] nodes = type.getNonTypeOrMethodNode();
		int length = nodes.length;
		for (int i = 0; i < length; i++) {
			ASTNode node = nodes[i];
			if (node.sourceStart() <= position && node.sourceEnd() >= position) {
				parser.setSource(unit);
				parser.parseBlockStatements(node, type, position);
				return node;
			}
		}

		parser.handleNotInElement(type, position);
		if (DLTKCore.DEBUG) {
			System.err.println("TODO: Engine: Add fields support."); //$NON-NLS-1$
		}

		return null;
	}

	private ASTNode parseMethod(MethodDeclaration method,
			ModuleDeclaration unit, int position) {
		if (method != null) {
			if (method.sourceStart() > position)
				return null;
			if (method.sourceEnd() >= position) {
				parser.parseBlockStatements(method, unit, position);
				return method;
			}
		}
		return null;
	}

}
