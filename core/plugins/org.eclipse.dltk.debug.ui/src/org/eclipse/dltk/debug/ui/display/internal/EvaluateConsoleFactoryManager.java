/*******************************************************************************
 * Copyright (c) 2011 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.debug.ui.display.internal;

import org.eclipse.dltk.debug.ui.display.IEvaluateConsoleFactory;
import org.eclipse.dltk.utils.SimpleExtensionManager;

public class EvaluateConsoleFactoryManager extends
		SimpleExtensionManager<IEvaluateConsoleFactory> {

	private EvaluateConsoleFactoryManager() {
		super(IEvaluateConsoleFactory.class, "org.eclipse.dltk.debug.console");
	}

	private static EvaluateConsoleFactoryManager manager = null;

	public static synchronized EvaluateConsoleFactoryManager getManager() {
		if (manager == null) {
			manager = new EvaluateConsoleFactoryManager();
		}
		return manager;
	}

}
