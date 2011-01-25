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
package org.eclipse.dltk.core.keyword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.utils.LazyExtensionManager;

/**
 * The implementation of the keyword manager. Please create you own instance
 * specifying the required nature.
 * 
 * @since 3.0
 */
public class KeywordManager extends LazyExtensionManager<IKeywordProvider> {

	private final String natureId;

	public KeywordManager(String natureId) {
		super(DLTKCore.PLUGIN_ID + ".keywords");
		this.natureId = natureId;
	}

	@Override
	protected Descriptor<IKeywordProvider> createDescriptor(
			IConfigurationElement confElement) {
		if (!natureId.equals(confElement.getAttribute("nature")))
			return null;
		return super.createDescriptor(confElement);
	}

	public String[] getKeywords(IKeywordCategory category, ISourceModule module) {
		List<String> result = new ArrayList<String>();
		for (IKeywordProvider provider : this) {
			String[] keywords = provider.getKeywords(category, module);
			if (keywords != null) {
				Collections.addAll(result, keywords);
			}
		}
		return result.toArray(new String[result.size()]);
	}

}
