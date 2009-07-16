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

import org.eclipse.dltk.core.ISourceModule;

/**
 * Parses source module and creates index while parsing
 * 
 * @author michael
 * 
 */
public interface IIndexingParser {

	/**
	 * Create index while parsing
	 * 
	 * @param module
	 *            Source module
	 * @param requestor
	 *            Indexing requestor (handler)
	 */
	public void parseSourceModule(ISourceModule module,
			IIndexingRequestor requestor);
}
