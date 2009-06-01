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
package org.eclipse.dltk.core.mixin;

import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;

public class MixinModelRegistry {

	public static void register(MixinModel model) {
		synchronized (models) {
			models.put(model, Boolean.TRUE);
		}
	}

	public static void unregister(MixinModel model) {
		synchronized (models) {
			models.remove(model);
		}
	}

	private static final Map<MixinModel, Object> models = new IdentityHashMap<MixinModel, Object>();

	/**
	 * @param toolkit
	 * @param module
	 */
	public static void removeSourceModule(IDLTKLanguageToolkit toolkit,
			ISourceModule module) {
		synchronized (models) {
			for (final MixinModel model : models.keySet()) {
				if (toolkit.getNatureId().equals(model.getNature())) {
					model.remove(module);
				}
			}
		}

	}

	public static void clearKeysCache(IDLTKLanguageToolkit toolkit) {
		synchronized (models) {
			for (final MixinModel model : models.keySet()) {
				if (toolkit.getNatureId().equals(model.getNature())) {
					model.clearKeysCache();
				}
			}
		}

	}

}
