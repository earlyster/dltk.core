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
 * Request for removing source module from the index. All elements related to
 * the source module must be removed as well.
 * 
 * @author michael
 * 
 */
public abstract class RemoveSourceModuleRequest extends AbstractIndexRequest {

	private final ISourceModule sourceModule;

	public RemoveSourceModuleRequest(AbstractProjectIndexer indexer,
			ISourceModule sourceModule) {
		super(indexer);
		this.sourceModule = sourceModule;
	}

	protected String getName() {
		return sourceModule.getElementName();
	}

	protected void run() throws CoreException, IOException {
		projectIndexer.removeSourceModule(sourceModule);
	}

	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((sourceModule == null) ? 0 : sourceModule.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RemoveSourceModuleRequest other = (RemoveSourceModuleRequest) obj;
		if (sourceModule == null) {
			if (other.sourceModule != null)
				return false;
		} else if (!sourceModule.equals(other.sourceModule))
			return false;
		return true;
	}
}
