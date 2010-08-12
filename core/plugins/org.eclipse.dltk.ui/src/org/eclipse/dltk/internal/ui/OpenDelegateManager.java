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
package org.eclipse.dltk.internal.ui;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IOpenDelegate;
import org.eclipse.dltk.utils.LazyExtensionManager;

public class OpenDelegateManager extends LazyExtensionManager<IOpenDelegate> {

	private static final OpenDelegateManager MANAGER = new OpenDelegateManager();

	private OpenDelegateManager() {
		super(DLTKUIPlugin.PLUGIN_ID + ".openDelegate");
	}

	public static IOpenDelegate findFor(Object object) {
		for (IOpenDelegate factory : MANAGER) {
			if (factory.supports(object)) {
				return factory;
			}
		}
		return null;
	}

}
