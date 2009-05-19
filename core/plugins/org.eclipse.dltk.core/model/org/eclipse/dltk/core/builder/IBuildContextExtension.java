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
package org.eclipse.dltk.core.builder;

public interface IBuildContextExtension extends IBuildContext {

	void setLineTracker(ISourceLineTracker tracker);

	/**
	 * Tests if lineTracker is already created, so next call to
	 * {@link #getLineTracker()} would return it without additional work done.
	 * 
	 * @return
	 */
	boolean isLineTrackerCreated();

}
