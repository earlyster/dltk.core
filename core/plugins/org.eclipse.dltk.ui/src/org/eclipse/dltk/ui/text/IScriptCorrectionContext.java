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
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.editor.IScriptAnnotation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.texteditor.ITextEditor;

public interface IScriptCorrectionContext {

	/**
	 * Returns the {@link IQuickAssistInvocationContext} object originally
	 * passed to the correction processor.
	 * 
	 * @return
	 */
	IQuickAssistInvocationContext getInvocationContext();

	/**
	 * Returns the {@link ITextEditor} for the current quick-assist call.
	 * 
	 * @return
	 */
	ITextEditor getEditor();

	/**
	 * Returns the {@link ISourceModule} for the current quick-assist call.
	 * 
	 * @return
	 */
	ISourceModule getModule();

	/**
	 * Returns the {@link IScriptProject} for the current quick-assist call
	 * 
	 * @return
	 */
	IScriptProject getProject();

	/**
	 * Adds the specified proposal to the results of the current quick-assist
	 * call.
	 * 
	 * @param proposal
	 */
	void addProposal(ICompletionProposal proposal);

	/**
	 * @param resolution
	 */
	void addResolution(IAnnotationResolution resolution,
			IScriptAnnotation annotation);

	/**
	 * @param resolution
	 */
	void addResolution(IMarkerResolution resolution, IMarker marker);

	/**
	 * Returns all proposals collected so far, or <code>null</code> if there are
	 * no proposals.
	 * 
	 * @return
	 */
	ICompletionProposal[] getProposals();

	/**
	 * Returns the attribute from the specified name. If there is no attribute
	 * with the specified name <code>null</code> is returned.
	 * 
	 * @param attributeName
	 * @return
	 */
	Object getAttribute(String attributeName);

	/**
	 * Sets the attribute with the specified name to the specified value. If
	 * value is <code>null</code> attribute is just removed.
	 * 
	 * @param attributeName
	 * @param value
	 */
	void setAttribute(String attributeName, Object value);

}
