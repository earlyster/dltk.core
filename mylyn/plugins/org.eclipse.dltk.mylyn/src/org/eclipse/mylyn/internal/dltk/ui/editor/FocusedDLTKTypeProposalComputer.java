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
import org.eclipse.dltk.ui.text.completion.ContentAssistInvocationContext;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;


public class FocusedDLTKTypeProposalComputer extends ScriptCompletionProposalComputer {

	public FocusedDLTKTypeProposalComputer() {
		FocusedDLTKProposalProcessor.getDefault().addMonitoredComputer(this);
	}

	
	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		List proposals = super.computeScriptCompletionProposals(context.getInvocationOffset(), (ScriptContentAssistInvocationContext) context, monitor);
		return FocusedDLTKProposalProcessor.getDefault().projectInterestModel(this, proposals);
	}

	
	protected TemplateCompletionProcessor createTemplateProposalComputer(
			ScriptContentAssistInvocationContext arg0) {
		// TODO Auto-generated method stub
		//return FocusedDLTKProposalProcessor.getDefault();
		return null;
	}
}
