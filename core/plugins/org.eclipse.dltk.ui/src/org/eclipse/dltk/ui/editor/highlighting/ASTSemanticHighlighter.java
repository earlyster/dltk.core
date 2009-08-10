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
package org.eclipse.dltk.ui.editor.highlighting;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceParserUtil;

/**
 * Abstract base class for the semantic highlighters operating on the AST tree.
 */
public abstract class ASTSemanticHighlighter extends
		AbstractSemanticHighlighter {

	@Override
	protected boolean doHighlighting(
			org.eclipse.dltk.compiler.env.ISourceModule code) throws Exception {
		final ModuleDeclaration module = parseCode(code);
		if (module != null) {
			module.traverse(createVisitor(code));
			return true;
		}
		return false;
	}

	/**
	 * @param code
	 * @return
	 * @throws ModelException
	 */
	protected ModuleDeclaration parseCode(
			org.eclipse.dltk.compiler.env.ISourceModule code)
			throws ModelException {
		if (code instanceof ISourceModule) {
			return parseSourceModule((ISourceModule) code);
		} else {
			return parseSourceCode(code);
		}
	}

	protected ModuleDeclaration parseSourceCode(
			org.eclipse.dltk.compiler.env.ISourceModule code)
			throws ModelException {
		if (code instanceof ISourceModule) {
			return SourceParserUtil.getModuleDeclaration((ISourceModule) code);
		}
		return SourceParserUtil.getModuleDeclaration(code.getFileName(), code
				.getContentsAsCharArray(), getNature(), null, null);
	}

	protected ModuleDeclaration parseSourceModule(
			final ISourceModule sourceModule) {
		return SourceParserUtil.getModuleDeclaration(sourceModule);
	}

	protected abstract String getNature();

	protected abstract ASTVisitor createVisitor(
			org.eclipse.dltk.compiler.env.ISourceModule sourceCode)
			throws ModelException;

}
