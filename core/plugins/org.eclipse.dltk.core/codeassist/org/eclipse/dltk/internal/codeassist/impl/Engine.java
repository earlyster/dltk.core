/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.codeassist.impl;

import java.util.Map;

import org.eclipse.dltk.codeassist.IAssistParser;
import org.eclipse.dltk.core.ISearchableEnvironment;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.internal.compiler.impl.ITypeRequestor;
import org.eclipse.dltk.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.dltk.internal.compiler.lookup.SourceModuleScope;

public abstract class Engine implements ITypeRequestor {
	public LookupEnvironment lookupEnvironment;

	protected ISearchableEnvironment nameEnvironment;

	protected SourceModuleScope unitScope;

	public AssistOptions options;

	protected static final int EXACT_RULE = SearchPattern.R_EXACT_MATCH
			| SearchPattern.R_CASE_SENSITIVE;

	public Engine(Map settings) {
		this.options = new AssistOptions(settings);
	}

	@Deprecated
	protected final IAssistParser getParser() {
		return null;
	}
}
