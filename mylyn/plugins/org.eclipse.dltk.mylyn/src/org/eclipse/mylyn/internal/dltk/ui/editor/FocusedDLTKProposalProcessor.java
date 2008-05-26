/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.dltk.ui.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.ui.text.completion.AbstractScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;

/**
 * TODO: parametrize relevance levels (requires JDT changes, bug 119063)
 * 
 * @author Mik Kersten
 */
public class FocusedDLTKProposalProcessor {

	static final int THRESHOLD_INTEREST = 10000;

	private static final int THRESHOLD_IMPLICIT_INTEREST = THRESHOLD_INTEREST * 2;

	private static final int RELEVANCE_IMPLICIT_INTEREST = 300;

	private static final String IDENTIFIER_THIS = "this";

	public static final String LABEL_SEPARATOR = " -------------------------------------------- ";

	public static final FocusedProposalSeparator PROPOSAL_SEPARATOR = new FocusedProposalSeparator();

	private List monitoredProposalComputers = new ArrayList();

	private List alreadyComputedProposals = new ArrayList();

	private List alreadyContainSeparator = new ArrayList();

	private List containsSingleInterestingProposal = new ArrayList();

	private static FocusedDLTKProposalProcessor INSTANCE = new FocusedDLTKProposalProcessor();

	private FocusedDLTKProposalProcessor() {
	}

	public static FocusedDLTKProposalProcessor getDefault() {
		return INSTANCE;
	}

	public void addMonitoredComputer(
			IScriptCompletionProposalComputer proposalComputer) {
		monitoredProposalComputers.add(proposalComputer);
	}

	public List projectInterestModel(
			IScriptCompletionProposalComputer proposalComputer, List proposals) {
		try {
			if (!ContextCorePlugin.getContextManager().isContextActive()) {
				return proposals;
			} else {
				boolean hasInterestingProposals = false;
				// for (Object object : proposals) {
				for (ListIterator it = proposals.listIterator(); it.hasNext();) {
					Object object = it.next();
					if (object instanceof AbstractScriptCompletionProposal) {
						boolean foundInteresting = boostRelevanceWithInterest((AbstractScriptCompletionProposal) object);
						if (!hasInterestingProposals && foundInteresting) {
							hasInterestingProposals = true;
						}
					}
				}

				// NOTE: this annoying state needs to be maintainted to ensure
				// the
				// separator is added only once, and not added for single
				// proposals
				if (containsSingleInterestingProposal.size() > 0
						&& proposals.size() > 0) {
					proposals
							.add(FocusedDLTKProposalProcessor.PROPOSAL_SEPARATOR);
				} else if (hasInterestingProposals
						&& alreadyContainSeparator.isEmpty()) {
					if (proposals.size() == 1) {
						containsSingleInterestingProposal.add(proposalComputer);
					} else {
						proposals
								.add(FocusedDLTKProposalProcessor.PROPOSAL_SEPARATOR);
						alreadyContainSeparator.add(proposalComputer);
					}
				}

				alreadyComputedProposals.add(proposalComputer);
				if (alreadyComputedProposals.size() == monitoredProposalComputers
						.size()) {
					alreadyComputedProposals.clear();
					alreadyContainSeparator.clear();
					containsSingleInterestingProposal.clear();
				}

				return proposals;
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR,
					ContextUiPlugin.ID_PLUGIN,
					"Failed to project interest onto propsals", t));
			return proposals;
		}
	}

	private boolean boostRelevanceWithInterest(
			AbstractScriptCompletionProposal proposal) {
		boolean hasInteresting = false;
		IModelElement modelElement = proposal.getModelElement();
		if (modelElement != null) {
			IInteractionElement interactionElement = ContextCorePlugin
					.getContextManager().getElement(
							modelElement.getHandleIdentifier());
			float interest = interactionElement.getInterest().getValue();
			// if (interest >
			// InteractionContextManager.getCommonContextScaling()
			// .getInteresting()) {
			if (interest > ContextCorePlugin.getDefault()
					.getCommonContextScaling().getInteresting()) {
				// TODO: losing precision here, only going to one decimal place
				proposal.setRelevance(THRESHOLD_INTEREST
						+ (int) (interest * 10));
				hasInteresting = true;
			}
		} else if (isImplicitlyInteresting(proposal)) {
			proposal.setRelevance(THRESHOLD_IMPLICIT_INTEREST
					+ proposal.getRelevance());
			hasInteresting = true;
		}
		return hasInteresting;
	}

	public boolean isImplicitlyInteresting(
			AbstractScriptCompletionProposal proposal) {
		return proposal.getRelevance() > RELEVANCE_IMPLICIT_INTEREST
				&& !IDENTIFIER_THIS.equals(proposal.getDisplayString());
	}

	static class FocusedProposalSeparator extends ScriptCompletionProposal {
		public FocusedProposalSeparator() {
			super("", 0, 0, ContextUiImages
					.getImage(ContextUiImages.QUALIFY_NAMES), LABEL_SEPARATOR,
					FocusedDLTKProposalProcessor.THRESHOLD_INTEREST);

		}

		protected boolean isSmartTrigger(char arg0) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
