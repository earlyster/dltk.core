/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.codeassist.RelevanceConstants;
import org.eclipse.dltk.compiler.problem.IProblem;

/**
 * Abstract base class for a completion requestor which is passed completion
 * proposals as they are generated in response to a code assist request.
 * <p>
 * This class is intended to be subclassed by clients.
 * </p>
 * <p>
 * The code assist engine normally invokes methods on completion requestors in
 * the following sequence:
 * 
 * <pre>
 * requestor.beginReporting();
 * requestor.acceptContext(context);
 * requestor.accept(proposal_1);
 * requestor.accept(proposal_2);
 * ...
 * requestor.endReporting();
 * </pre>
 * 
 * If, however, the engine is unable to offer completion proposals for whatever
 * reason, <code>completionFailure</code> is called with a problem object
 * describing why completions were unavailable. In this case, the sequence of
 * calls is:
 * 
 * <pre>
 * requestor.beginReporting();
 * requestor.acceptContext(context);
 * requestor.completionFailure(problem);
 * requestor.endReporting();
 * </pre>
 * 
 * In either case, the bracketing <code>beginReporting</code>
 * <code>endReporting</code> calls are always made as well as
 * <code>acceptContext</code> call.
 * </p>
 * <p>
 * The class was introduced in 3.0 as a more evolvable replacement for the
 * <code>ICompletionRequestor</code> interface.
 * </p>
 * 
 * @see ICodeAssist
 * 
 */
public abstract class CompletionRequestor {

	/**
	 * The set of CompletionProposal kinds that this requestor ignores;
	 * <code>0</code> means the set is empty. 1 << completionProposalKind
	 */
	private int ignoreSet = 0;

	/**
	 * Creates a new completion requestor. The requestor is interested in all
	 * kinds of completion proposals; none will be ignored.
	 */
	public CompletionRequestor() {
		// do nothing
	}

	public static final int ALL = 1 << 31;
	private static final int ALL_BITSET = Integer.MAX_VALUE;

	/**
	 * Returns whether the given kind of completion proposal is ignored.
	 * 
	 * @param completionProposalKind
	 *            one of the kind constants declared on
	 *            <code>CompletionProposal</code> or {@link #ALL}
	 * @return <code>true</code> if the given kind of completion proposal is
	 *         ignored by this requestor, and <code>false</code> if it is of
	 *         interest
	 * @see #setIgnored(int, boolean)
	 * @see CompletionProposal#getKind()
	 */
	public final boolean isIgnored(int completionProposalKind) {
		if (completionProposalKind == ALL) {
			return this.ignoreSet == ALL_BITSET;
		}
		if (completionProposalKind < CompletionProposal.FIRST_KIND
				|| completionProposalKind > CompletionProposal.LAST_KIND) {
			throw new IllegalArgumentException(
					"Unknown kind of completion proposal: " + completionProposalKind); //$NON-NLS-1$
		}
		return 0 != (this.ignoreSet & (1 << completionProposalKind));
	}

	/**
	 * Sets whether the given kind of completion proposal is ignored.
	 * 
	 * @param completionProposalKind
	 *            one of the kind constants declared on
	 *            <code>CompletionProposal</code> or {@link #ALL}
	 * @param ignore
	 *            <code>true</code> if the given kind of completion proposal is
	 *            ignored by this requestor, and <code>false</code> if it is of
	 *            interest
	 * @see #isIgnored(int)
	 * @see CompletionProposal#getKind()
	 */
	public final void setIgnored(int completionProposalKind, boolean ignore) {
		if (completionProposalKind == ALL) {
			this.ignoreSet = ignore ? ALL_BITSET : 0;
			return;
		}
		if (completionProposalKind < CompletionProposal.FIRST_KIND
				|| completionProposalKind > CompletionProposal.LAST_KIND) {
			throw new IllegalArgumentException(
					"Unknown kind of completion proposal: " + completionProposalKind); //$NON-NLS-1$
		}
		if (ignore) {
			this.ignoreSet |= (1 << completionProposalKind);
		} else {
			this.ignoreSet &= ~(1 << completionProposalKind);
		}
	}

	/**
	 * Pro forma notification sent before reporting a batch of completion
	 * proposals.
	 * <p>
	 * The default implementation of this method does nothing. Clients may
	 * override.
	 * </p>
	 */
	public void beginReporting() {
		// do nothing
	}

	/**
	 * Pro forma notification sent after reporting a batch of completion
	 * proposals.
	 * <p>
	 * The default implementation of this method does nothing. Clients may
	 * override.
	 * </p>
	 */
	public void endReporting() {
		// do nothing
	}

	/**
	 * Notification of failure to produce any completions. The problem object
	 * explains what prevented completing.
	 * <p>
	 * The default implementation of this method does nothing. Clients may
	 * override to receive this kind of notice.
	 * </p>
	 * 
	 * @param problem
	 *            the problem object
	 */
	public void completionFailure(IProblem problem) {
		// default behavior is to ignore
	}

	/**
	 * Proposes a completion. Has no effect if the kind of proposal is being
	 * ignored by this requestor. Callers should consider checking
	 * {@link #isIgnored(int)} before avoid creating proposal objects that would
	 * only be ignored.
	 * <p>
	 * Similarly, implementers should check {@link #isIgnored(int)
	 * isIgnored(proposal.getKind())} and ignore proposals that have been
	 * declared as uninteresting. The proposal object passed is only valid for
	 * the duration of completion operation.
	 * 
	 * @param proposal
	 *            the completion proposal
	 * @exception IllegalArgumentException
	 *                if the proposal is null
	 */
	public abstract void accept(CompletionProposal proposal);

	/**
	 * Propose the context in which the completion occurs.
	 * <p>
	 * This method is called one and only one time before any call to
	 * {@link #accept(CompletionProposal)}. The default implementation of this
	 * method does nothing. Clients may override.
	 * </p>
	 * 
	 * @param context
	 *            the completion context
	 * 
	 * 
	 */
	public void acceptContext(CompletionContext context) {
		// do nothing
	}

	/**
	 * Checks if the current call is made to display context information.
	 * 
	 * @return
	 */
	public boolean isContextInformationMode() {
		return false;
	}

	/**
	 * Interface for the filtering out or changing the relevance of the
	 * completion proposals.
	 * 
	 * @since 4.1
	 */
	public static interface CompletionProposalFilter {
		int DEFAULT = 0;
		int IGNORE = -1000;
		int DISCOURAGED = -50;

		/**
		 * Evaluates the relevance of specified proposal. Possible return values
		 * are:
		 * <ul>
		 * <li>{@link #DEFAULT} to continue without any changes
		 * <li>{@link #IGNORE} to skip the proposal
		 * <li>{@link #DISCOURAGED} to mark the proposal as
		 * <em>not recommended</em>
		 * <li>constants from {@link RelevanceConstants} to increase the
		 * relevance of the proposal
		 * </ul>
		 */
		int evaluate(CompletionProposal proposal);
	}

	private CompletionProposalFilter[] filters;

	/**
	 * Adds the given filter to this requestor.
	 * 
	 * @since 4.1
	 */
	public void addFilter(CompletionProposalFilter filter) {
		Assert.isNotNull(filter);
		if (filters == null) {
			filters = new CompletionProposalFilter[] { filter };
		} else {
			final CompletionProposalFilter[] newFilters = new CompletionProposalFilter[filters.length + 1];
			System.arraycopy(filters, 0, newFilters, 0, filters.length);
			newFilters[filters.length] = filter;
			filters = newFilters;
		}
	}

	/**
	 * Returns the result of filtering for the given completion proposal.
	 * 
	 * @see #addFilter(CompletionProposalFilter)
	 * @since 4.1
	 */
	protected int evaluateFilters(CompletionProposal completionProposal) {
		int result = CompletionProposalFilter.DEFAULT;
		if (filters != null) {
			try {
				for (CompletionProposalFilter filter : filters) {
					int value = filter.evaluate(completionProposal);
					if (value == CompletionProposalFilter.IGNORE) {
						return value;
					}
					if (value > 0 && value > result || value < 0
							&& value < result) {
						result = value;
					}
				}
			} catch (RuntimeException e) {
				DLTKCore.error(
						"Error while evaluating CompletionProposalFilter, continue without filters",
						e);
				filters = null;
				result = CompletionProposalFilter.DEFAULT;
			}
		}
		return result;
	}
}
