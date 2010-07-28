/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.text.completion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

public class ScriptMethodCompletionProposal extends
		LazyScriptCompletionProposal {
	/**
	 * Triggers for method proposals without parameters. Do not modify.
	 */
	protected final static char[] METHOD_TRIGGERS = new char[] { ';', ',', '.',
			'\t', '[' };

	/**
	 * Triggers for method proposals. Do not modify.
	 */
	protected final static char[] METHOD_WITH_ARGUMENTS_TRIGGERS = new char[] {
			'(', '-', ' ' };

	/** Triggers for method name proposals (static imports). Do not modify. */
	protected final static char[] METHOD_NAME_TRIGGERS = new char[] { ';' };

	private IRegion fSelectedRegion; // initialized by apply()

	private boolean fHasParameters;
	private boolean fHasParametersComputed = false;
	private int fContextInformationPosition;

	public ScriptMethodCompletionProposal(CompletionProposal proposal,
			ScriptContentAssistInvocationContext context) {
		super(proposal, context);
	}

	public static interface IReplacementBuffer {
		void addArgument(int offset, int length);

		void append(String text);

		String toString();

		int length();
	}

	private static class ReplacementBuffer implements IReplacementBuffer {
		private final List<IRegion> arguments = new ArrayList<IRegion>();

		public void addArgument(int offset, int length) {
			arguments.add(new Region(offset, length));
		}

		private final StringBuilder buffer = new StringBuilder();

		public void append(String text) {
			buffer.append(text);
		}

		public int length() {
			return buffer.length();
		}

		@Override
		public String toString() {
			return buffer.toString();
		}

	}

	private ReplacementBuffer replacementBuffer;

	@Override
	public void apply(IDocument document, char trigger, int offset) {
		if (trigger == ' ' || trigger == '(')
			trigger = '\0';
		super.apply(document, trigger, offset);

		int exit = getReplacementOffset() + getReplacementString().length();

		if (replacementBuffer != null && !replacementBuffer.arguments.isEmpty()
				&& getTextViewer() != null) {
			int baseOffset = getReplacementOffset() + getCursorPosition();
			try {
				LinkedModeModel model = new LinkedModeModel();
				for (IRegion region : replacementBuffer.arguments) {
					LinkedPositionGroup group = new LinkedPositionGroup();
					group.addPosition(new LinkedPosition(document, baseOffset
							+ region.getOffset(), region.getLength(),
							LinkedPositionGroup.NO_STOP));
					model.addGroup(group);
				}

				model.forceInstall();

				LinkedModeUI ui = new EditorLinkedModeUI(model, getTextViewer());
				ui.setExitPosition(getTextViewer(), exit, 0, Integer.MAX_VALUE);
				ui.setExitPolicy(new ExitPolicy(')', document));
				ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
				ui.enter();

				fSelectedRegion = ui.getSelectedRegion();

			} catch (BadLocationException e) {
			}
		} else {
			fSelectedRegion = new Region(exit, 0);
		}
	}

	protected boolean needsLinkedMode() {
		return false; // we do it our selfs
	}

	/**
	 * @see org.eclipse.dltk.ui.text.completion.AbstractScriptCompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	@Override
	public Point getSelection(IDocument document) {
		if (fSelectedRegion == null)
			return new Point(getReplacementOffset(), 0);

		return new Point(fSelectedRegion.getOffset(),
				fSelectedRegion.getLength());
	}

	@Override
	public CharSequence getPrefixCompletionText(IDocument document,
			int completionOffset) {
		if (hasArgumentList()) {
			String completion = String.valueOf(fProposal.getName());
			if (isCamelCaseMatching()) {
				String prefix = getPrefix(document, completionOffset);
				return getCamelCaseCompound(prefix, completion);
			}

			return completion;
		}
		return super.getPrefixCompletionText(document, completionOffset);
	}

	@Override
	protected IContextInformation computeContextInformation() {
		// no context information for METHOD_NAME_REF proposals (e.g. for static
		// imports)
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=94654
		if (fProposal.getKind() == CompletionProposal.METHOD_REF
				&& hasParameters()
				&& (getReplacementString().endsWith(RPAREN) || getReplacementString()
						.length() == 0)) {
			ProposalContextInformation contextInformation = new ProposalContextInformation(
					fProposal);
			if (fContextInformationPosition != 0
					&& fProposal.getCompletion().length() == 0)
				contextInformation
						.setContextInformationPosition(fContextInformationPosition);
			return contextInformation;
		}
		return super.computeContextInformation();
	}

	@Override
	protected char[] computeTriggerCharacters() {
		if (fProposal.getKind() == CompletionProposal.METHOD_NAME_REFERENCE)
			return METHOD_NAME_TRIGGERS;

		if (hasParameters())
			return METHOD_WITH_ARGUMENTS_TRIGGERS;

		return METHOD_TRIGGERS;
	}

	/**
	 * Returns <code>true</code> if the method being inserted has at least one
	 * parameter. Note that this does not say anything about whether the
	 * argument list should be inserted. This depends on the position in the
	 * document and the kind of proposal; see {@link #hasArgumentList() }.
	 * 
	 * @return <code>true</code> if the method has any parameters,
	 *         <code>false</code> if it has no parameters
	 */
	protected final boolean hasParameters() {
		if (!fHasParametersComputed) {
			fHasParametersComputed = true;
			String[] findParameterNames = fProposal.findParameterNames(null);
			fHasParameters = findParameterNames != null
					&& findParameterNames.length > 0;
		}
		return fHasParameters;
	}

	// private boolean computeHasParameters() throws IllegalArgumentException {
	// return Signature.getParameterCount(fProposal.getSignature()) > 0;
	// }

	/**
	 * Returns <code>true</code> if the argument list should be inserted by the
	 * proposal, <code>false</code> if not.
	 * 
	 * @return <code>true</code> when the proposal is not in javadoc nor within
	 *         an import and comprises the parameter list
	 */
	protected boolean hasArgumentList() {
		if (CompletionProposal.METHOD_NAME_REFERENCE == fProposal.getKind())
			return false;
		IPreferenceStore preferenceStore = DLTKUIPlugin.getDefault()
				.getPreferenceStore();
		boolean noOverwrite = preferenceStore
				.getBoolean(PreferenceConstants.CODEASSIST_INSERT_COMPLETION)
				^ isToggleEating();
		String completion = fProposal.getCompletion();
		return !isInScriptdoc()
				&& completion.length() > 0
				&& (noOverwrite || completion.charAt(completion.length() - 1) == ')');
	}

	/**
	 * Override {@link #computeReplacement(IReplacementBuffer)}
	 */
	@Override
	protected final String computeReplacementString() {
		replacementBuffer = new ReplacementBuffer();
		computeReplacement(replacementBuffer);
		return replacementBuffer.toString();
	}

	protected void computeReplacement(IReplacementBuffer buffer) {
		if (!hasArgumentList()) {
			buffer.append(super.computeReplacementString());
			return;
		}

		// we're inserting a method plus the argument list - respect formatter
		// preferences
		buffer.append(fProposal.getName());

		// FormatterPrefs prefs= getFormatterPrefs();
		// if (prefs.beforeOpeningParen)
		// buffer.append(SPACE);
		buffer.append(LPAREN);

		if (hasParameters()) {
			setCursorPosition(buffer.length());

			// if (prefs.afterOpeningParen)
			// buffer.append(SPACE);

			String[] parameterNames = fProposal.findParameterNames(null);
			int argumentOffset = 0;
			for (int i = 0; i < parameterNames.length; ++i) {
				if (i != 0) {
					buffer.append(COMMA);
					argumentOffset += 1;
				}
				buffer.append(parameterNames[i]);
				buffer.addArgument(argumentOffset, parameterNames[i].length());
				argumentOffset += parameterNames[i].length();
			}

			// don't add the trailing space, but let the user type it in himself
			// - typing the closing paren will exit
			// if (prefs.beforeClosingParen)
			// buffer.append(SPACE);
		} else {
			// if (prefs.inEmptyList)
			// buffer.append(SPACE);
		}

		buffer.append(RPAREN);
	}

	@Override
	protected ProposalInfo computeProposalInfo() {
		IScriptProject project = fInvocationContext.getProject();
		if (project != null)
			return new MethodProposalInfo(project, fProposal);
		return super.computeProposalInfo();
	}

	/**
	 * Overrides the default context information position. Ignored if set to
	 * zero.
	 * 
	 * @param contextInformationPosition
	 *            the replaced position.
	 */
	public void setContextInformationPosition(int contextInformationPosition) {
		fContextInformationPosition = contextInformationPosition;
	}

	@Override
	protected boolean isValidPrefix(String prefix) {
		if (super.isValidPrefix(prefix))
			return true;

		String word = getDisplayString();
		if (isInScriptdoc()) {
			int idx = word.indexOf("{@link "); //$NON-NLS-1$
			if (idx == 0) {
				word = word.substring(7);
			} else {
				idx = word.indexOf("{@value "); //$NON-NLS-1$
				if (idx == 0) {
					word = word.substring(8);
				}
			}
		}
		return isPrefix(prefix, word);
	}

}
