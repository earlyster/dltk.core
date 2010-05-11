/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.codeassist;

import org.eclipse.dltk.core.CompletionProposal;

public class InternalCompletionProposal extends CompletionProposal {

	/**
	 * @param kind
	 * @param completionLocation
	 */
	public InternalCompletionProposal(int kind, int completionLocation) {
		super(kind, completionLocation);
	}

}
