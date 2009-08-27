/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.eclipse.dltk.core;

import java.util.List;

/**
 * This interface could be used to extend generic structure model building.
 */
public interface IModelProvider {
	/**
	 * Called for each model element buildStructure.
	 * 
	 * Can remove some elements from children's set.
	 * 
	 * Any new elements need to implement @see:IModelElementMemento to handle
	 * inner element references.
	 */
	void provideModelChanges(IModelElement parentElement,
			List<IModelElement> children);

	/**
	 * Used for performance reasons.
	 * 
	 * Should return true if provider provides some elements at selected level.
	 */
	boolean isModelChangesProvidedFor(IModelElement modelElement, String name);
}
