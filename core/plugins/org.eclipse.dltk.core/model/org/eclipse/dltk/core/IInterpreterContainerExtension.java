/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core;

import java.util.List;

public interface IInterpreterContainerExtension {
	/**
	 * This method could modify set of entries.
	 */
	void processEntres(IScriptProject project, List<IBuildpathEntry> entries);
}
