/*******************************************************************************
 * Copyright (c) 2011 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.text.spelling;

import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.ui.DLTKUIPlugin;

public enum SpellingProblems implements IProblemIdentifier {
	SPELLING_PROBLEM;

	public String contributor() {
		return DLTKUIPlugin.PLUGIN_ID;
	}

}
