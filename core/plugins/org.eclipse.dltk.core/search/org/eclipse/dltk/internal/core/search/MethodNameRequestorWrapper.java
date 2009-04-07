/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search;

import org.eclipse.dltk.core.search.BasicSearchEngine;
import org.eclipse.dltk.core.search.MethodNameRequestor;
import org.eclipse.dltk.core.search.TypeNameRequestor;

/**
 * Wrapper used to link {@link IRestrictedAccessTypeRequestor} with
 * {@link TypeNameRequestor}. This wrapper specifically allows usage of internal
 * method
 * {@link BasicSearchEngine#searchAllTypeNames(char[] packageName, int packageMatchRule, char[] typeName, int typeMatchRule, int searchFor, org.eclipse.dltk.core.search.IDLTKSearchScope scope, IRestrictedAccessTypeRequestor nameRequestor, int waitingPolicy, org.eclipse.core.runtime.IProgressMonitor monitor) }
 * . from API method
 * {@link org.eclipse.dltk.core.search.SearchEngine#searchAllTypeNames(char[] packageName, char[] typeName, int matchRule, int searchFor, org.eclipse.dltk.core.search.IDLTKSearchScope scope, MethodNameRequestor nameRequestor, int waitingPolicy, org.eclipse.core.runtime.IProgressMonitor monitor) }
 * .
 */
public class MethodNameRequestorWrapper implements
		IRestrictedAccessMethodRequestor {
	MethodNameRequestor requestor;

	public MethodNameRequestorWrapper(MethodNameRequestor requestor) {
		this.requestor = requestor;
	}

	public void acceptMethod(int modifiers, char[] packageName,
			char[] simpleMethodName, char[][] enclosingTypeNames,
			char[][] parameterNames, String path) {
		this.requestor.acceptMethod(modifiers, packageName, simpleMethodName,
				enclosingTypeNames, parameterNames, path);
	}
}
