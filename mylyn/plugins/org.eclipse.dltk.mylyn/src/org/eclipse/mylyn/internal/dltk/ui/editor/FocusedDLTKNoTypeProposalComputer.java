package org.eclipse.mylyn.internal.dltk.ui.editor;

/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/


import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.dltk.ui.text.completion.ContentAssistInvocationContext;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;

public class FocusedDLTKNoTypeProposalComputer extends ScriptCompletionProposalComputer {

	public FocusedDLTKNoTypeProposalComputer() {
		FocusedDLTKProposalProcessor.getDefault().addMonitoredComputer(this);
	}

//	@Override
//	protected CompletionProposalCollector createCollector(JavaContentAssistInvocationContext context) {
//		return super.createCollector(context);
//	}

	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		List proposals = super.computeCompletionProposals(context, monitor);
		return FocusedDLTKProposalProcessor.getDefault().projectInterestModel(this, proposals);
	}

	protected TemplateCompletionProcessor createTemplateProposalComputer(
		ScriptContentAssistInvocationContext arg0) {
	// TODO Auto-generated method stub
	return null;
}
}
