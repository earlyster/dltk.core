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
package org.eclipse.dltk.ui.text;

import org.eclipse.core.resources.IMarker;
import org.eclipse.dltk.ui.editor.IScriptAnnotation;

/**
 * Quick assist processor for quick fixes and quick assists.
 * 
 * Implementations are contributed via
 * <code>org.eclipse.dltk.ui.correctionProcessor</code> extension point.
 */
public interface IScriptCorrectionProcessor {

	/**
	 * Tells whether this processor has a fix for the given annotation.
	 * <p>
	 * <strong>Note:</strong> This test must be fast and optimistic i.e. it is
	 * OK to return <code>true</code> even though there might be no quick fix.
	 * </p>
	 * 
	 * @param annotation
	 *            the annotation
	 * @return <code>true</code> if the assistant has a fix for the given
	 *         annotation
	 */
	boolean canFix(IScriptAnnotation annotation);

	boolean canFix(IMarker marker);

	void computeQuickAssistProposals(IScriptAnnotation annotation,
			IScriptCorrectionContext context);

	void computeQuickAssistProposals(IMarker marker,
			IScriptCorrectionContext context);

}
