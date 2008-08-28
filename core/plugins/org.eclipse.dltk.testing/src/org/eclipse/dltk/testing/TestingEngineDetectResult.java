/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.testing;

import org.eclipse.core.runtime.IStatus;

public class TestingEngineDetectResult {

	private final ITestingEngine engine;
	private final IStatus status;

	/**
	 * @param engine
	 * @param status
	 */
	public TestingEngineDetectResult(ITestingEngine engine, IStatus status) {
		this.engine = engine;
		this.status = status;
	}

	/**
	 * @return the engine
	 */
	public ITestingEngine getEngine() {
		return engine;
	}

	/**
	 * @return the status
	 */
	public IStatus getStatus() {
		return status;
	}

}
