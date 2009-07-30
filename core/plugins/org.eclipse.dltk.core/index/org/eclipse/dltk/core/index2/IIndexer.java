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
package org.eclipse.dltk.core.index2;

import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.index2.search.ISearchEngine;

/**
 * Abstract source module indexer
 * 
 * @author michael
 * @since 2.0
 * 
 */
public interface IIndexer {

	/**
	 * Request to index document
	 */
	public void indexDocument(ISourceModule sourceModule);

	/**
	 * Removes container path from index
	 * 
	 * @param containerPath
	 *            Container path
	 */
	public void removeContainer(IPath containerPath);

	/**
	 * Removes document from index
	 * 
	 * @param containerPath
	 *            Container path
	 * @param relativePath
	 *            Document path relative to the container path
	 */
	public void removeDocument(IPath containerPath, String relativePath);

	/**
	 * Returns existing container documents in index
	 * 
	 * @param containerPath
	 *            Container path
	 * @return map where the key is a document path relative to the container
	 *         path, value - last update timestamp
	 */
	public Map<String, Long> getDocuments(IPath containerPath);

	/**
	 * Creates search engine applicable for this indexer type
	 * 
	 * @return search engine
	 */
	public ISearchEngine createSearchEngine();
}
