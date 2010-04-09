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

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Folding block provider. Implementations should be contributed to
 * <code>org.eclipse.dltk.ui.folding/blockProvider</code> extension point.
 */
public interface IFoldingBlockProvider {

	void initializePreferences(IPreferenceStore preferenceStore);

	void setRequestor(IFoldingBlockRequestor requestor);

	/**
	 * Compute foldable blocks and report them to the requestor provided via
	 * separate call. If current folding operation should be interrupted (e.g.
	 * because of unrecoverable syntax error) then provider should throw
	 * {@link AbortFoldingException}
	 * 
	 * @param content
	 * @throws AbortFoldingException
	 */
	void computeFoldableBlocks(IFoldingContent content);

}
