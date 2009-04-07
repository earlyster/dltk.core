/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search;


/**
 * A <code>IRestrictedAccessTypeRequestor</code> collects search results from a
 * <code>searchAllTypeNames</code> query to a <code>SearchBasicEngine</code>
 * providing restricted access information when a type is accepted.
 */
public interface IRestrictedAccessMethodRequestor {

	public void acceptMethod(int modifiers, char[] packageName,
			char[] simpleMethodName, char[][] enclosingTypeNames,
			char[][] parameterNames, String path);

}
