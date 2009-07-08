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
package org.eclipse.dltk.ui.text;

import org.eclipse.dltk.compiler.task.ITodoTaskPreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * @since 2.0
 */
public class ScriptMultilineCommentScanner extends ScriptCommentScanner {

	/**
	 * @param manager
	 * @param store
	 * @param comment
	 * @param todoTag
	 * @param preferences
	 * @param initializeAutomatically
	 */
	public ScriptMultilineCommentScanner(IColorManager manager,
			IPreferenceStore store, String comment, String todoTag,
			ITodoTaskPreferences preferences, boolean initializeAutomatically) {
		super(manager, store, comment, todoTag, preferences,
				initializeAutomatically);
	}

	@Override
	protected int skipCommentChars() {
		return 0;
	}

	/**
	 * FIXME Standard implementation copied (Alex)
	 */
	@Override
	public IToken nextToken() {
		fTokenOffset = fOffset;
		fColumn = UNDEFINED;
		if (fRules != null) {
			for (int i = 0; i < fRules.length; i++) {
				IToken token = (fRules[i].evaluate(this));
				if (!token.isUndefined())
					return token;
			}
		}
		if (read() == EOF)
			return Token.EOF;
		return fDefaultReturnToken;
	}

}
