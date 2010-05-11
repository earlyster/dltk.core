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
package org.eclipse.dltk.ui.text.completion;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.utils.NatureExtensionManager;

public class CompletionProposalLabelProviderRegistry {

	public static CompletionProposalLabelProvider create(String natureId) {
		final NatureExtensionManager manager = new NatureExtensionManager(
				DLTKUIPlugin.PLUGIN_ID + ".completion",
				CompletionProposalLabelProvider.class) {
			@Override
			protected boolean isValidElement(IConfigurationElement element) {
				return "proposalLabelProvider".equals(element.getName());
			}
		};
		Object[] instances = manager.getInstances(natureId);
		if (instances != null && instances.length != 0) {
			return (CompletionProposalLabelProvider) instances[0];
		} else {
			return new CompletionProposalLabelProvider();
		}
	}
}
