/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.launching;

import java.util.List;

import org.eclipse.dltk.core.IBuildpathEntry;

/**
 * @since 2.0
 */
public interface IInterpreterContainerExtension2 extends
		IInterpreterContainerExtension {

	/**
	 * This method is called to customize {@link IBuildpathEntry
	 * IBuildpathEntries} for the {@link IInterpreterInstall}.
	 * 
	 * It's called once per interpreter.
	 * 
	 * Entries are initialized from the {@link LibraryLocation}s returned by the
	 * interpreter.
	 */
	void preProcessEntries(IInterpreterInstall interpreter,
			List<IBuildpathEntry> entries);
}
