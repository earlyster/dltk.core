/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.core;

import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.ISourceModuleInfoCache.ISourceModuleInfo;

public interface ISourceElementParser {
	/**
	 * Parses contents of the module with ast creation. Also it is recommended to
	 * use SourceParserUtils to put delcaration into cache, and retrieve it from
	 * it.
	 */
	void parseSourceModule(org.eclipse.dltk.compiler.env.ISourceModule module,
			ISourceModuleInfo mifo);

	void setRequestor(ISourceElementRequestor requestor);

	void setReporter(IProblemReporter reporter);
}
