/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.launching.sourcelookup;

/**
 * @since 2.0
 */
public interface IProjectLookupResult {

	/**
	 * Returns the matching elements - {@link org.eclipse.core.resources.IFile}
	 * or {@link org.eclipse.dltk.core.ISourceModule}
	 * 
	 * @return
	 */
	public Object[] toArray();

	/**
	 * Returns the number of matching elements
	 * 
	 * @return
	 */
	public int size();

}
