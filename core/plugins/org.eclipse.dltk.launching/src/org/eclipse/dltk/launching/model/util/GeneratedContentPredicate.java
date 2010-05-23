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
package org.eclipse.dltk.launching.model.util;

import org.eclipse.dltk.core.Predicate;
import org.eclipse.dltk.launching.model.InterpreterGeneratedContent;
import org.eclipse.emf.ecore.EObject;

public class GeneratedContentPredicate implements Predicate<EObject> {

	private final String key;

	public GeneratedContentPredicate(String key) {
		this.key = key;
	}

	public boolean evaluate(EObject t) {
		return t instanceof InterpreterGeneratedContent
				&& key.equals(((InterpreterGeneratedContent) t).getKey());
	}

}
