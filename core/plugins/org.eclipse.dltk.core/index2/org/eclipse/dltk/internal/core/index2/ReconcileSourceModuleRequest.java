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

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;

/**
 * Request for reconciling source module. Obsolete source module is removed
 * first from the index, then new copy is inserted.
 * 
 * @author michael
 * 
 */
public class ReconcileSourceModuleRequest extends AddSourceModuleRequest {

	public ReconcileSourceModuleRequest(AbstractProjectIndexer indexer,
			ISourceModule sourceModule) {
		super(indexer, sourceModule);
	}

	protected void run() throws CoreException, IOException {
		projectIndexer.removeSourceModule(sourceModule);
		super.run();
	}

	public boolean equals(Object obj) {
		if (obj instanceof ReconcileSourceModuleRequest) {
			return super.equals(obj);
		}
		return false;
	}
}
