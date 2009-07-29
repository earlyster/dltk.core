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

/**
 * Provides language dependent implementations for indexing parser and element
 * resolver.
 * 
 * @author michael
 * 
 */
public interface IIndexerParticipant {

	/**
	 * Returns indexing parser for indexer.
	 * 
	 * @return
	 */
	public IIndexingParser getIndexingParser();

	/**
	 * Returns element resolver for indexer.
	 * 
	 * @return
	 */
	public IElementResolver getElementResolver();
}
