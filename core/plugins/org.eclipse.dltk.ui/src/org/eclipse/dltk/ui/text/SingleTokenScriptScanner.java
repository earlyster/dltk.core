/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/

package org.eclipse.dltk.ui.text;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;

public final class SingleTokenScriptScanner extends AbstractScriptScanner {

	private String[] fProperty;

	public SingleTokenScriptScanner(IColorManager manager,
			IPreferenceStore store, String property) {
		super(manager, store);
		fProperty = new String[] { property };
		initialize();
	}

	@Override
	protected String[] getTokenProperties() {
		return fProperty;
	}

	@Override
	protected List<IRule> createRules() {
		setDefaultReturnToken(getToken(fProperty[0]));
		return null;
	}
}
