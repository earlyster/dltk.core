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
package org.eclipse.dltk.ui.text.folding;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.utils.NatureExtensionManager;

public class FoldingProviderManager {

	public static IFoldingStructureProvider getStructureProvider(String natureId) {
		final NatureExtensionManager structureProviders = new NatureExtensionManager(
				FOLDING_EXT_POINT, IFoldingStructureProvider.class) {
			@Override
			protected boolean isValidElement(IConfigurationElement element) {
				return "structureProvider".equals(element.getName());
			}
		};
		final Object[] instances = structureProviders.getInstances(natureId);
		if (instances != null && instances.length != 0) {
			return (IFoldingStructureProvider) instances[0];
		} else {
			return null;
		}
	}

	public static IFoldingBlockProvider[] getBlockProviders(String natureId) {
		final NatureExtensionManager blockProviders = new NatureExtensionManager(
				FOLDING_EXT_POINT, IFoldingBlockProvider.class) {
			@Override
			protected boolean isValidElement(IConfigurationElement element) {
				return "blockProvider".equals(element.getName());
			}
		};
		return (IFoldingBlockProvider[]) blockProviders.getInstances(natureId);
	}

	private static final String FOLDING_EXT_POINT = DLTKUIPlugin.PLUGIN_ID
			+ ".folding";

}
