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
package org.eclipse.dltk.core;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.compiler.problem.IProblemReporter;

/**
 * The optional interface to be implemented by {@link ISourceElementParser} if
 * operates on the AST model build by {@link ISourceParser} and wishes to share
 * AST cache.
 */
public interface ISourceElementParserAST extends ISourceElementParser {

	/**
	 * @return
	 */
	IProblemReporter getReporter();

	/**
	 * This entry to call when caching is used.
	 * 
	 * @param module
	 *            the {@link ISourceModule} being parsed
	 * @param declaration
	 *            the {@link ModuleDeclaration} parsed
	 * @param content
	 *            source module content if module was just parsed or
	 *            <code>null</code> if {@link ModuleDeclaration} was retrieved
	 *            from cache (if the module content is needed it could be
	 *            retrieved from {@link ISourceModule}).
	 */
	void processModule(ISourceModule module, ModuleDeclaration declaration,
			char[] content);

}
