/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core;

/**
 * Represents an archive fragment in a project
 * 
 * @since 2.0
 */
public interface IArchiveProjectFragment extends IProjectFragment {

	/**
	 * Retrieves the archive provided in this fragment
	 * 
	 * @return IArchive
	 */
	public abstract IArchive getArchive();

}
