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
package org.eclipse.dltk.ast.declarations;

import org.eclipse.dltk.ast.parser.IModuleDeclaration;

/**
 * Compatibility wrapper of the parser result.
 */
public class ModuleDeclarationWrapper extends ModuleDeclaration {

	private final IModuleDeclaration target;

	public ModuleDeclarationWrapper(IModuleDeclaration target) {
		super(0);
		this.target = target;
	}

	public IModuleDeclaration getTarget() {
		return target;
	}

}
