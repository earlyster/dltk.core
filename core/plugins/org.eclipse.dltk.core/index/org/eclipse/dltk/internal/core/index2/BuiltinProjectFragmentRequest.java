/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.internal.core.index2;

import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.environment.IEnvironment;

/**
 * Request for indexing builtin (special) project fragment
 * 
 * @author michael
 * 
 */
public class BuiltinProjectFragmentRequest extends
		ExternalProjectFragmentRequest {

	public BuiltinProjectFragmentRequest(AbstractProjectIndexer indexer,
			IProjectFragment fragment, ProgressJob progressJob) {
		super(indexer, fragment, progressJob);
	}

	protected IEnvironment getEnvironment() {
		return null;
	}

	public boolean equals(Object obj) {
		if (obj instanceof BuiltinProjectFragmentRequest) {
			return super.equals(obj);
		}
		return false;
	}
}
