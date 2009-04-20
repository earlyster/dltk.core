/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.core.search;

/**
 * This class is a default implementation of type name requestor. It's useful
 * when we don't need to do anything with search results, like when warming up
 * the search engine.
 */
public final class NopTypeNameRequestor extends TypeNameRequestor {

	public void acceptType(int modifiers, char[] packageName,
			char[] simpleTypeName, char[][] enclosingTypeNames,
			char[][] superTypes, String path) {
	}

}
