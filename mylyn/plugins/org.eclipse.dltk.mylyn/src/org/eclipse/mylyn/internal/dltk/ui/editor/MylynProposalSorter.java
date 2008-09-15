package org.eclipse.mylyn.internal.dltk.ui.editor;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.ui.text.completion.AbstractProposalSorter;
import org.eclipse.dltk.ui.text.completion.AbstractScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.CompletionProposalComparator;
import org.eclipse.dltk.ui.text.completion.ContentAssistInvocationContext;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;

public final class MylynProposalSorter extends AbstractProposalSorter {

	private final Comparator fComparator = new CompletionProposalComparator();

	private static final FocusedProposalSeparator PROPOSAL_SEPARATOR = new FocusedProposalSeparator();

	public int compare(ICompletionProposal p1, ICompletionProposal p2) {
		return fComparator.compare(p1, p2);
	}

	public void beginSorting(ContentAssistInvocationContext context,
			List proposals) {
		if (!ContextCorePlugin.getContextManager().isContextActive())
			return;

		final float interestingLevel = ContextCorePlugin.getDefault()
				.getCommonContextScaling().getInteresting();

		boolean hasInteresting = false;
		for (Iterator it = proposals.iterator(); it.hasNext();) {
			Object object = it.next();
			if (object instanceof AbstractScriptCompletionProposal) {
				final AbstractScriptCompletionProposal proposal = (AbstractScriptCompletionProposal) object;
				final IModelElement modelElement = proposal.getModelElement();
				if (modelElement != null) {
					if (modelElement.getElementType() == IModelElement.SCRIPT_PROJECT)
						continue;
					IInteractionElement interactionElement = ContextCorePlugin
							.getContextManager().getElement(
									modelElement.getHandleIdentifier());

					float interest = interactionElement.getInterest()
							.getValue();
					if (interest > interestingLevel) {
						proposal
								.setRelevance(InterestConstants.THRESHOLD_INTEREST
										+ (int) (interest * 10));
						hasInteresting = true;
					}
				}
			}
		}

		if (hasInteresting)
			proposals.add(PROPOSAL_SEPARATOR);

	}

	static class FocusedProposalSeparator extends ScriptCompletionProposal {
		public FocusedProposalSeparator() {
			super(Util.EMPTY_STRING, 0, 0, CommonImages
					.getImage(CommonImages.SEPARATOR_LIST),
					InterestConstants.LABEL_SEPARATOR,
					InterestConstants.THRESHOLD_INTEREST);
		}

		protected boolean isSmartTrigger(char trigger) {
			return false;
		}

	}
}
