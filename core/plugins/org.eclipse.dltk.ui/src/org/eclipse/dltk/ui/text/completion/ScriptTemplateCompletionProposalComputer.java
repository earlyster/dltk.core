/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.ui.text.completion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;

/**
 * An template completion proposal computer can generate template completion
 * proposals from a given TemplateEngine.
 * 
 * Subclasses must implement
 * {@link #createCompletionProcessor(ScriptContentAssistInvocationContext)}
 * 
 * @since 4.1
 */
public abstract class ScriptTemplateCompletionProposalComputer extends
		AbstractScriptCompletionProposalComputer implements
		IScriptCompletionProposalComputer {

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#
	 * computeCompletionProposals
	 * (org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List<ICompletionProposal> computeCompletionProposals(
			ContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (!(context instanceof ScriptContentAssistInvocationContext))
			return Collections.emptyList();

		final ScriptContentAssistInvocationContext scriptContext = (ScriptContentAssistInvocationContext) context;
		// ISourceModule unit = scriptContext.getSourceModule();
		// if (unit == null)
		// return Collections.emptyList();

		final TemplateCompletionProcessor processor = createCompletionProcessor(scriptContext);
		if (processor == null)
			return Collections.emptyList();

		final ICompletionProposal[] templateProposals = processor
				.computeCompletionProposals(scriptContext.getViewer(),
						scriptContext.getInvocationOffset());
		updateTemplateProposalRelevance(scriptContext, templateProposals);
		final List<ICompletionProposal> result = new ArrayList<ICompletionProposal>(
				templateProposals.length);
		Collections.addAll(result, templateProposals);
		return result;
	}

	/**
	 * Compute the engine used to retrieve completion proposals in the given
	 * context
	 * 
	 * @param context
	 *            the context where proposals will be made
	 * @return the engine or <code>null</code> if no engine available in the
	 *         context
	 */
	protected abstract TemplateCompletionProcessor createCompletionProcessor(
			ScriptContentAssistInvocationContext context);

	/*
	 * @see ICompletionProposalComputer#computeContextInformation(
	 * ContentAssistInvocationContext, IProgressMonitor)
	 */
	public List<IContextInformation> computeContextInformation(
			ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return Collections.emptyList();
	}

	/*
	 * @see ICompletionProposalComputer#getErrorMessage()
	 */
	public String getErrorMessage() {
		return null;
	}

	/*
	 * @see IScriptCompletionProposalComputer#sessionStarted()
	 */
	public void sessionStarted() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IScriptCompletionProposalComputer#sessionEnded()
	 */
	public void sessionEnded() {
	}

}
