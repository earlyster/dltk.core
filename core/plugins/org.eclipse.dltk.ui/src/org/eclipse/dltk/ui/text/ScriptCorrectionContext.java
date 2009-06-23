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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.editor.IScriptAnnotation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Default implementation of the {@link IScriptCorrectionContext}.
 * 
 * 
 * XXX This class will be moved to the internal package
 * 
 * @noinstantiate
 * @noextend
 */
public class ScriptCorrectionContext implements IScriptCorrectionContext {

	private List<ICompletionProposal> proposals = null;

	public void addProposal(ICompletionProposal proposal) {
		if (proposals == null) {
			proposals = new ArrayList<ICompletionProposal>();
		}
		proposals.add(proposal);
	}

	public void addResolution(IMarkerResolution resolution, IMarker marker) {
		addProposal(new MarkerResolutionProposal(resolution, marker));
	}

	public void addResolution(IAnnotationResolution resolution,
			IScriptAnnotation annotation) {
		addProposal(new AnnotationResolutionProposal(resolution, annotation));
	}

	public ICompletionProposal[] getProposals() {
		if (proposals != null) {
			return proposals.toArray(new ICompletionProposal[proposals.size()]);
		} else {
			return null;
		}
	}

	private final IQuickAssistInvocationContext invocationContext;
	private final ITextEditor editor;
	private final ISourceModule module;

	/**
	 * @param invocationContext
	 * @param module
	 */
	public ScriptCorrectionContext(
			IQuickAssistInvocationContext invocationContext,
			ITextEditor editor, ISourceModule module) {
		this.invocationContext = invocationContext;
		this.editor = editor;
		this.module = module;
	}

	public ITextEditor getEditor() {
		return editor;
	}

	public ISourceModule getModule() {
		return module;
	}

	public IScriptProject getProject() {
		return module.getScriptProject();
	}

	public IQuickAssistInvocationContext getInvocationContext() {
		return invocationContext;
	}

	private Map<String, Object> attributes = null;

	public Object getAttribute(String attributeName) {
		if (attributes == null) {
			return null;
		} else {
			return attributes.get(attributeName);
		}
	}

	public void setAttribute(String attributeName, Object value) {
		if (value != null) {
			if (attributes == null) {
				attributes = new HashMap<String, Object>();
			}
			attributes.put(attributeName, value);
		} else {
			if (attributes != null) {
				attributes.remove(attributeName);
			}
		}
	}

}
