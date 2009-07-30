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
import org.eclipse.dltk.core.index2.IIndexer;

/**
 * Request to add source module to the index
 * 
 * @author michael
 * 
 */
public class AddSourceModuleRequest extends AbstractIndexRequest {

	protected final ISourceModule sourceModule;

	public AddSourceModuleRequest(AbstractProjectIndexer indexer,
			ISourceModule sourceModule, ProgressJob progressJob) {
		super(indexer, progressJob);
		this.sourceModule = sourceModule;
	}

	protected String getName() {
		return sourceModule.getElementName();
	}

	protected void run() throws CoreException, IOException {
		IIndexer indexer = IndexerManager.getIndexer();
		if (indexer == null) {
			return;
		}
		reportToProgress(sourceModule);
		indexer.indexDocument(sourceModule);
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
		AddSourceModuleRequest other = (AddSourceModuleRequest) obj;
		if (sourceModule == null) {
			if (other.sourceModule != null)
				return false;
		} else if (!sourceModule.equals(other.sourceModule))
			return false;
		return true;
	}
}
