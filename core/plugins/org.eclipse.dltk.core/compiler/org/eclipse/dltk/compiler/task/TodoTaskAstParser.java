/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.compiler.task;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.expressions.Literal;
import org.eclipse.dltk.core.DLTKCore;

public class TodoTaskAstParser extends TodoTaskRangeParser {

	/**
	 * @param preferences
	 */
	public TodoTaskAstParser(ITodoTaskPreferences preferences) {
		super(preferences);
	}

	public void initialize(ModuleDeclaration ast) {
		resetRanges();
		if (ast != null) {
			setCheckRanges(true);
			final ASTVisitor visitor = new ASTVisitor() {

				public boolean visitGeneral(ASTNode node) throws Exception {
					if (isSimpleNode(node)) {
						excludeRange(node.sourceStart(), node.sourceEnd());
					}
					return true;
				}

			};
			try {
				ast.traverse(visitor);
			} catch (Exception e) {
				DLTKCore.error("Unexpected error", e); //$NON-NLS-1$
			}
		} else {
			setCheckRanges(false);
		}
	}

	protected boolean isSimpleNode(ASTNode node) {
		return node instanceof Literal;
	}

}
