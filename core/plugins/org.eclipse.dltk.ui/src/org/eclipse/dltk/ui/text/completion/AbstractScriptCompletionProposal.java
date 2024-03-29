/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.text.completion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.text.hover.DocumentationHover;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;
import org.osgi.framework.Bundle;

@SuppressWarnings("restriction")
public abstract class AbstractScriptCompletionProposal implements
		IScriptCompletionProposal, ICompletionProposalExtension,
		ICompletionProposalExtension2, ICompletionProposalExtension3,
		ICompletionProposalExtension5 {

	/**
	 * A class to simplify tracking a reference position in a document.
	 */
	static final class ReferenceTracker {

		/** The reference position category name. */
		private static final String CATEGORY = "reference_position"; //$NON-NLS-1$
		/** The position updater of the reference position. */
		private final IPositionUpdater fPositionUpdater = new DefaultPositionUpdater(
				CATEGORY);
		/** The reference position. */
		private final Position fPosition = new Position(0);

		/**
		 * Called before document changes occur. It must be followed by a call
		 * to postReplace().
		 * 
		 * @param document
		 *            the document on which to track the reference position.
		 * 
		 */
		public void preReplace(IDocument document, int offset)
				throws BadLocationException {
			fPosition.setOffset(offset);
			try {
				document.addPositionCategory(CATEGORY);
				document.addPositionUpdater(fPositionUpdater);
				document.addPosition(CATEGORY, fPosition);

			} catch (BadPositionCategoryException e) {
				// should not happen
				DLTKUIPlugin.log(e);
			}
		}

		/**
		 * Called after the document changed occurred. It must be preceded by a
		 * call to preReplace().
		 * 
		 * @param document
		 *            the document on which to track the reference position.
		 */
		public int postReplace(IDocument document) {
			try {
				document.removePosition(CATEGORY, fPosition);
				document.removePositionUpdater(fPositionUpdater);
				document.removePositionCategory(CATEGORY);

			} catch (BadPositionCategoryException e) {
				// should not happen
				DLTKUIPlugin.log(e);
			}
			return fPosition.getOffset();
		}
	}

	protected static final class ExitPolicy implements IExitPolicy {

		final char fExitCharacter;
		private final IDocument fDocument;

		public ExitPolicy(char exitCharacter, IDocument document) {
			fExitCharacter = exitCharacter;
			fDocument = document;
		}

		public ExitFlags doExit(LinkedModeModel environment, VerifyEvent event,
				int offset, int length) {

			if (event.character == fExitCharacter) {
				if (environment.anyPositionContains(offset))
					return new ExitFlags(ILinkedModeListener.UPDATE_CARET,
							false);
				else
					return new ExitFlags(ILinkedModeListener.UPDATE_CARET, true);
			}

			switch (event.character) {
			case ';':
				return new ExitFlags(ILinkedModeListener.NONE, true);
			case SWT.CR:
				// when entering an anonymous class as a parameter, we don't
				// want
				// to jump after the parenthesis when return is pressed
				if (offset > 0) {
					try {
						if (fDocument.getChar(offset - 1) == '{')
							return new ExitFlags(ILinkedModeListener.EXIT_ALL,
									true);
					} catch (BadLocationException e) {
					}
				}
				// fall through
			default:
				return null;
			}
		}

	}

	private String fDisplayString;
	private String fReplacementString;
	private int fReplacementOffset;
	private int fReplacementLength;
	private int fCursorPosition;
	private Image fImage;
	private IContextInformation fContextInformation;
	private ProposalInfo fProposalInfo;
	private char[] fTriggerCharacters;
	private String fSortString;
	private int fRelevance;
	private boolean fIsInDoc;

	private StyleRange fRememberedStyleRange;
	private boolean fToggleEating;
	private ITextViewer fTextViewer;

	/**
	 * The control creator.
	 */
	private IInformationControlCreator fCreator;

	/**
	 * The style sheet (css).
	 */
	private static String fgCSSStyles;

	protected AbstractScriptCompletionProposal() {
	}

	public char[] getTriggerCharacters() {
		return fTriggerCharacters;
	}

	/**
	 * Sets the trigger characters.
	 * 
	 * @param triggerCharacters
	 *            The set of characters which can trigger the application of
	 *            this completion proposal
	 */
	public void setTriggerCharacters(char[] triggerCharacters) {
		fTriggerCharacters = triggerCharacters;
	}

	/**
	 * Sets the proposal info.
	 * 
	 * @param proposalInfo
	 *            The additional information associated with this proposal or
	 *            <code>null</code>
	 */
	public void setProposalInfo(ProposalInfo proposalInfo) {
		fProposalInfo = proposalInfo;
	}

	/**
	 * Returns the additional proposal info, or <code>null</code> if none
	 * exists.
	 * 
	 * @return the additional proposal info, or <code>null</code> if none exists
	 */
	public ProposalInfo getProposalInfo() {
		return fProposalInfo;
	}

	/**
	 * Sets the cursor position relative to the insertion offset. By default
	 * this is the length of the completion string (Cursor positioned after the
	 * completion)
	 * 
	 * @param cursorPosition
	 *            The cursorPosition to set
	 */
	public void setCursorPosition(int cursorPosition) {
		Assert.isTrue(cursorPosition >= 0);
		fCursorPosition = cursorPosition;
	}

	protected int getCursorPosition() {
		return fCursorPosition;
	}

	public final void apply(IDocument document) {
		// not used any longer
		apply(document, (char) 0, getReplacementOffset()
				+ getReplacementLength());
	}

	public void apply(IDocument document, char trigger, int offset) {
		try {
			// patch replacement length
			int delta = offset
					- (getReplacementOffset() + getReplacementLength());
			if (delta > 0)
				setReplacementLength(getReplacementLength() + delta);

			boolean isSmartTrigger = isSmartTrigger(trigger);

			if (!isSmartTrigger && trigger != (char) 0) {
				ensureTriggerAtCursorLocation(trigger);
			}
			String replacement = getReplacementString();

			// reference position just at the end of the document change.
			int referenceOffset = getReplacementOffset()
					+ getReplacementLength();
			final ReferenceTracker referenceTracker = new ReferenceTracker();
			referenceTracker.preReplace(document, referenceOffset);

			replace(document, getReplacementOffset(), getReplacementLength(),
					replacement);
			postReplace(document);

			referenceOffset = referenceTracker.postReplace(document);
			setReplacementOffset(referenceOffset
					- (replacement == null ? 0 : replacement.length()));

			// PR 47097
			if (isSmartTrigger)
				handleSmartTrigger(document, trigger, referenceOffset);

		} catch (BadLocationException x) {
			// ignore
		}
	}

	protected void postReplace(IDocument document) throws BadLocationException {
	}

	/**
	 * @since 3.0
	 */
	protected void ensureTriggerAtCursorLocation(char trigger) {
		String replacement = getReplacementString();

		// fix for PR #5533. Assumes that no eating takes place.
		if (getCursorPosition() > 0
				&& getCursorPosition() <= replacement.length()
				&& replacement.charAt(getCursorPosition() - 1) != trigger) {
			StringBuilder buffer = new StringBuilder(replacement);
			buffer.insert(getCursorPosition(), trigger);
			setReplacementString(buffer.toString());
			setCursorPosition(getCursorPosition() + 1);
		}
	}

	protected boolean isSmartTrigger(char trigger) {
		return false;
	}

	/**
	 * @param document
	 * @param trigger
	 * @param referenceOffset
	 * @throws BadLocationException
	 */
	protected void handleSmartTrigger(IDocument document, char trigger,
			int referenceOffset) throws BadLocationException {
	}

	protected final void replace(IDocument document, int offset, int length,
			String string) throws BadLocationException {
		if (!document.get(offset, length).equals(string))
			document.replace(offset, length, string);
	}

	public void apply(ITextViewer viewer, char trigger, int stateMask,
			int offset) {

		IDocument document = viewer.getDocument();
		if (fTextViewer == null)
			fTextViewer = viewer;

		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=96059
		// don't apply the proposal if for some reason we're not valid any
		// longer
		if (!isInDoc() && !validate(document, offset, null)) {
			setCursorPosition(offset - getReplacementOffset());
			if (trigger != '\0') {
				try {
					document.replace(offset, 0, String.valueOf(trigger));
					setCursorPosition(getCursorPosition() + 1);
					applyAutoClose(trigger, document);
				} catch (BadLocationException x) {
					// ignore
				}
			}
			return;
		}

		// don't eat if not in preferences, XOR with modifier key 1 (Ctrl)
		// but: if there is a selection, replace it!
		Point selection = viewer.getSelectedRange();
		fToggleEating = (stateMask & SWT.MOD1) != 0;
		int newLength = selection.x + selection.y - getReplacementOffset();
		if ((insertCompletion() ^ fToggleEating) && newLength >= 0)
			setReplacementLength(newLength);

		apply(document, trigger, offset);
		fToggleEating = false;
	}

	protected void applyAutoClose(char trigger, IDocument document)
			throws BadLocationException {
		if (trigger == '(' && autocloseBrackets()) {
			document.replace(getReplacementOffset() + getCursorPosition(), 0,
					")"); //$NON-NLS-1$
			setUpLinkedMode(document, ')');
		}
	}

	/**
	 * Returns <code>true</code> if the proposal is within javadoc,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if the proposal is within javadoc,
	 *         <code>false</code> otherwise
	 */
	@Deprecated
	protected final boolean isInScriptdoc() {
		return isInDoc();
	}

	/**
	 * Returns <code>true</code> if the proposal is within javadoc,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if the proposal is within javadoc,
	 *         <code>false</code> otherwise
	 */
	protected boolean isInDoc() {
		return fIsInDoc;
	}

	/**
	 * Sets the javadoc attribute.
	 * 
	 * @param isInDoc
	 *            <code>true</code> if the proposal is within javadoc
	 */
	protected void setInDoc(boolean isInDoc) {
		fIsInDoc = isInDoc;
	}

	/*
	 * @see ICompletionProposal#getSelection
	 */
	public Point getSelection(IDocument document) {
		return new Point(getReplacementOffset() + getCursorPosition(), 0);
	}

	/*
	 * @see ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return fContextInformation;
	}

	/**
	 * Sets the context information.
	 * 
	 * @param contextInformation
	 *            The context information associated with this proposal
	 */
	public void setContextInformation(IContextInformation contextInformation) {
		fContextInformation = contextInformation;
	}

	/*
	 * @see ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		return fDisplayString;
	}

	/*
	 * @see ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		final Object info = getAdditionalProposalInfo(new NullProgressMonitor());
		return info != null ? info.toString() : null;
	}

	/*
	 * @seeorg.eclipse.jface.text.contentassist.ICompletionProposalExtension5#
	 * getAdditionalProposalInfo(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		if (getProposalInfo() != null) {
			String info = getProposalInfo().getInfo(monitor);
			if (info != null && info.length() > 0) {
				StringBuffer buffer = new StringBuffer();
				HTMLPrinter.insertPageProlog(buffer, 0, getCSSStyles());
				buffer.append(info);
				HTMLPrinter.addPageEpilog(buffer);
				info = buffer.toString();
			}
			return info;
		}
		return null;
	}

	/**
	 * Returns the style sheet URL.
	 * 
	 * 
	 */
	protected String getCSSStyles() {
		if (fgCSSStyles == null) {
			Bundle bundle = Platform.getBundle(DLTKUIPlugin.getPluginId());
			URL url = bundle.getEntry("/DocumentationHoverStyleSheet.css"); //$NON-NLS-1$
			if (url != null) {
				try {
					url = FileLocator.toFileURL(url);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(url.openStream()));
					StringBuffer buffer = new StringBuffer(200);
					String line = reader.readLine();
					while (line != null) {
						buffer.append(line);
						buffer.append('\n');
						line = reader.readLine();
					}
					fgCSSStyles = buffer.toString();
				} catch (IOException ex) {
					DLTKUIPlugin.log(ex);
				}
			}
		}
		String css = fgCSSStyles;
		if (css != null) {
			FontData fontData = JFaceResources.getFontRegistry().getFontData(
					PreferenceConstants.APPEARANCE_DOCUMENTATION_FONT)[0];
			css = HTMLPrinter.convertTopLevelFont(css, fontData);
		}
		return css;
	}

	/*
	 * @see ICompletionProposalExtension#getContextInformationPosition()
	 */
	public int getContextInformationPosition() {
		if (getContextInformation() == null)
			return getReplacementOffset() - 1;
		return getReplacementOffset() + getCursorPosition();
	}

	/**
	 * Gets the replacement offset.
	 * 
	 * @return Returns a int
	 */
	public int getReplacementOffset() {
		return fReplacementOffset;
	}

	/**
	 * Sets the replacement offset.
	 * 
	 * @param replacementOffset
	 *            The replacement offset to set
	 */
	public void setReplacementOffset(int replacementOffset) {
		Assert.isTrue(replacementOffset >= 0);
		fReplacementOffset = replacementOffset;
	}

	public int getPrefixCompletionStart(IDocument document, int completionOffset) {
		return getReplacementOffset();
	}

	/**
	 * Gets the replacement length.
	 * 
	 * @return Returns a int
	 */
	public int getReplacementLength() {
		return fReplacementLength;
	}

	/**
	 * Sets the replacement length.
	 * 
	 * @param replacementLength
	 *            The replacementLength to set
	 */
	public void setReplacementLength(int replacementLength) {
		Assert.isTrue(replacementLength >= 0);
		fReplacementLength = replacementLength;
	}

	/**
	 * Gets the replacement string.
	 * 
	 * @return Returns a String
	 */
	public String getReplacementString() {
		return fReplacementString;
	}

	/**
	 * Sets the replacement string.
	 * 
	 * @param replacementString
	 *            The replacement string to set
	 */
	public void setReplacementString(String replacementString) {
		Assert.isNotNull(replacementString);
		fReplacementString = replacementString;
	}

	public CharSequence getPrefixCompletionText(IDocument document,
			int completionOffset) {
		if (!isCamelCaseMatching())
			return getReplacementString();

		String prefix = getPrefix(document, completionOffset);
		return getCamelCaseCompound(prefix, getReplacementString());
	}

	public Image getImage() {
		return fImage;
	}

	/**
	 * Sets the image.
	 * 
	 * @param image
	 *            The image to set
	 */
	public void setImage(Image image) {
		fImage = image;
	}

	public boolean isValidFor(IDocument document, int offset) {
		return validate(document, offset, null);
	}

	public boolean validate(IDocument document, int offset, DocumentEvent event) {

		if (offset < getReplacementOffset())
			return false;

		boolean validated = isValidPrefix(getPrefix(document, offset));

		if (validated && event != null) {
			// adapt replacement range to document change
			int delta = (event.fText == null ? 0 : event.fText.length())
					- event.fLength;
			final int newLength = Math.max(getReplacementLength() + delta, 0);
			setReplacementLength(newLength);
		}

		return validated;
	}

	/**
	 * Checks whether <code>prefix</code> is a valid prefix for this proposal.
	 * Usually, while code completion is in progress, the user types and edits
	 * the prefix in the document in order to filter the proposal list. From
	 * {@link #validate(IDocument, int, DocumentEvent) }, the current prefix in
	 * the document is extracted and this method is called to find out whether
	 * the proposal is still valid.
	 * <p>
	 * The default implementation checks if <code>prefix</code> is a prefix of
	 * the proposal's {@link #getDisplayString() display string} using the
	 * {@link #isPrefix(String, String) } method.
	 * </p>
	 * 
	 * @param prefix
	 *            the current prefix in the document
	 * @return <code>true</code> if <code>prefix</code> is a valid prefix of
	 *         this proposal
	 */
	protected boolean isValidPrefix(String prefix) {
		/*
		 * See http://dev.eclipse.org/bugs/show_bug.cgi?id=17667 why we do not
		 * use the replacement string. String word= fReplacementString;
		 */
		return isPrefix(prefix, getDisplayString());
	}

	/**
	 * Gets the proposal's relevance.
	 * 
	 * @return Returns a int
	 */
	public int getRelevance() {
		return fRelevance;
	}

	/**
	 * Sets the proposal's relevance.
	 * 
	 * @param relevance
	 *            The relevance to set
	 */
	public void setRelevance(int relevance) {
		fRelevance = relevance;
	}

	/**
	 * Returns the text in <code>document</code> from
	 * {@link #getReplacementOffset()} to <code>offset</code>. Returns the empty
	 * string if <code>offset</code> is before the replacement offset or if an
	 * exception occurs when accessing the document.
	 * 
	 * 
	 */
	protected String getPrefix(IDocument document, int offset) {
		try {
			int length = offset - getReplacementOffset();
			if (length > 0)
				return document.get(getReplacementOffset(), length);
		} catch (BadLocationException x) {
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * Case insensitive comparison of <code>prefix</code> with the start of
	 * <code>string</code>. Returns <code>false</code> if <code>prefix</code> is
	 * longer than <code>string</code>
	 * 
	 * 
	 */
	protected boolean isPrefix(String prefix, String string) {
		if (prefix == null || string == null
				|| prefix.length() > string.length())
			return false;
		String start = string.substring(0, prefix.length());
		return start.equalsIgnoreCase(prefix)
				|| isCamelCaseMatching()
				&& CharOperation.camelCaseMatch(prefix.toCharArray(),
						string.toCharArray());
	}

	/**
	 * Matches <code>prefix</code> against <code>string</code> and replaces the
	 * matched region by prefix. Case is preserved as much as possible. This
	 * method returns <code>string</code> if camel case completion is disabled.
	 * Examples when camel case completion is enabled:
	 * <ul>
	 * <li>getCamelCompound("NuPo", "NullPointerException") ->
	 * "NuPointerException"</li>
	 * <li>getCamelCompound("NuPoE", "NullPointerException") -> "NuPoException"</li>
	 * <li>getCamelCompound("hasCod", "hashCode") -> "hasCode"</li>
	 * </ul>
	 * 
	 * @param prefix
	 *            the prefix to match against
	 * @param string
	 *            the string to match
	 * @return a compound of prefix and any postfix taken from
	 *         <code>string</code>
	 * 
	 */
	protected final String getCamelCaseCompound(String prefix, String string) {
		if (prefix.length() > string.length())
			return string;

		// a normal prefix - no camel case logic at all
		String start = string.substring(0, prefix.length());
		if (start.equalsIgnoreCase(prefix))
			return string;

		final char[] patternChars = prefix.toCharArray();
		final char[] stringChars = string.toCharArray();

		for (int i = 1; i <= stringChars.length; i++)
			if (CharOperation.camelCaseMatch(patternChars, 0,
					patternChars.length, stringChars, 0, i))
				return prefix + string.substring(i);

		// Not a camel case match at all.
		// This should not happen -> stay with the default behavior
		return string;
	}

	/**
	 * Returns true if camel case matching is enabled.
	 */
	protected boolean isCamelCaseMatching() {
		String value = DLTKCore.getOption(DLTKCore.CODEASSIST_CAMEL_CASE_MATCH);
		return DLTKCore.ENABLED.equals(value);
	}

	protected IPreferenceStore getPreferenceStore() {
		return DLTKUIPlugin.getDefault().getPreferenceStore();
	}

	protected boolean insertCompletion() {
		return getPreferenceStore().getBoolean(
				PreferenceConstants.CODEASSIST_INSERT_COMPLETION);
	}

	protected Color getForegroundColor(StyledText text) {

		RGB rgb = PreferenceConverter.getColor(getPreferenceStore(),
				PreferenceConstants.CODEASSIST_REPLACEMENT_FOREGROUND);
		ScriptTextTools textTools = getTextTools();
		if (textTools == null) {
			return null;
		}
		return textTools.getColorManager().getColor(rgb);
	}

	protected ScriptTextTools getTextTools() {
		return null;
	}

	protected Color getBackgroundColor(StyledText text) {

		RGB rgb = PreferenceConverter.getColor(getPreferenceStore(),
				PreferenceConstants.CODEASSIST_REPLACEMENT_BACKGROUND);
		ScriptTextTools textTools = getTextTools();
		if (textTools != null)
			return textTools.getColorManager().getColor(rgb);
		return null;
	}

	private void repairPresentation(ITextViewer viewer) {
		if (fRememberedStyleRange != null) {
			if (viewer instanceof ITextViewerExtension2) {
				// attempts to reduce the redraw area
				ITextViewerExtension2 viewer2 = (ITextViewerExtension2) viewer;

				if (viewer instanceof ITextViewerExtension5) {

					ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
					IRegion modelRange = extension
							.widgetRange2ModelRange(new Region(
									fRememberedStyleRange.start,
									fRememberedStyleRange.length));
					if (modelRange != null)
						viewer2.invalidateTextPresentation(
								modelRange.getOffset(), modelRange.getLength());

				} else {
					viewer2.invalidateTextPresentation(
							fRememberedStyleRange.start
									+ viewer.getVisibleRegion().getOffset(),
							fRememberedStyleRange.length);
				}

			} else
				viewer.invalidateTextPresentation();
		}
	}

	private void updateStyle(ITextViewer viewer) {

		StyledText text = viewer.getTextWidget();
		if (text == null || text.isDisposed())
			return;

		int widgetCaret = text.getCaretOffset();

		int modelCaret = 0;
		if (viewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
			modelCaret = extension.widgetOffset2ModelOffset(widgetCaret);
		} else {
			IRegion visibleRegion = viewer.getVisibleRegion();
			modelCaret = widgetCaret + visibleRegion.getOffset();
		}

		if (modelCaret >= getReplacementOffset() + getReplacementLength()) {
			repairPresentation(viewer);
			return;
		}

		int offset = widgetCaret;
		int length = getReplacementOffset() + getReplacementLength()
				- modelCaret;

		Color foreground = getForegroundColor(text);
		Color background = getBackgroundColor(text);

		StyleRange range = text.getStyleRangeAtOffset(offset);
		int fontStyle = range != null ? range.fontStyle : SWT.NORMAL;

		repairPresentation(viewer);
		fRememberedStyleRange = new StyleRange(offset, length, foreground,
				background, fontStyle);
		if (range != null) {
			fRememberedStyleRange.strikeout = range.strikeout;
			fRememberedStyleRange.underline = range.underline;
		}

		// http://dev.eclipse.org/bugs/show_bug.cgi?id=34754
		try {
			text.setStyleRange(fRememberedStyleRange);
		} catch (IllegalArgumentException x) {
			// catching exception as offset + length might be outside of the
			// text widget
			fRememberedStyleRange = null;
		}
	}

	public void selected(ITextViewer viewer, boolean smartToggle) {
		if (!insertCompletion() ^ smartToggle)
			updateStyle(viewer);
		else {
			repairPresentation(viewer);
			fRememberedStyleRange = null;
		}
	}

	public void unselected(ITextViewer viewer) {
		repairPresentation(viewer);
		fRememberedStyleRange = null;
	}

	public IInformationControlCreator getInformationControlCreator() {
		Shell shell = DLTKUIPlugin.getActiveWorkbenchShell();
		if (shell == null || !BrowserInformationControl.isAvailable(shell))
			return null;
		if (fCreator == null) {
			DocumentationHover.PresenterControlCreator presenterControlCreator = new DocumentationHover.PresenterControlCreator(
					getSite());
			fCreator = new DocumentationHover.HoverControlCreator(
					presenterControlCreator, true);
		}
		return fCreator;
	}

	private IWorkbenchSite getSite() {
		final IWorkbenchPage page = DLTKUIPlugin.getActivePage();
		if (page != null) {
			final IWorkbenchPart part = page.getActivePart();
			if (part != null)
				return part.getSite();
		}
		return null;
	}

	public String getSortString() {
		return fSortString;
	}

	public void setSortString(String string) {
		fSortString = string;
	}

	protected ITextViewer getTextViewer() {
		return fTextViewer;
	}

	protected boolean isToggleEating() {
		return fToggleEating;
	}

	/**
	 * Sets up a simple linked mode at {@link #getCursorPosition()} and an exit
	 * policy that will exit the mode when <code>closingCharacter</code> is
	 * typed and an exit position at <code>getCursorPosition() + 1</code>.
	 * 
	 * @param document
	 *            the document
	 * @param closingCharacter
	 *            the exit character
	 */
	protected void setUpLinkedMode(IDocument document, char closingCharacter) {
		if (getTextViewer() != null && autocloseBrackets()) {
			int offset = getReplacementOffset() + getCursorPosition();
			int exit = getReplacementOffset() + getReplacementString().length();
			try {
				LinkedPositionGroup group = new LinkedPositionGroup();
				group.addPosition(new LinkedPosition(document, offset, 0,
						LinkedPositionGroup.NO_STOP));

				LinkedModeModel model = new LinkedModeModel();
				model.addGroup(group);
				model.forceInstall();

				LinkedModeUI ui = new EditorLinkedModeUI(model, getTextViewer());
				ui.setSimpleMode(true);
				ui.setExitPolicy(new ExitPolicy(closingCharacter, document));
				ui.setExitPosition(getTextViewer(), exit, 0, Integer.MAX_VALUE);
				ui.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
				ui.enter();
			} catch (BadLocationException x) {
				DLTKUIPlugin.log(x);
			}
		}
	}

	protected boolean autocloseBrackets() {
		return getPreferenceStore().getBoolean(
				PreferenceConstants.EDITOR_CLOSE_BRACKETS);
	}

	protected void setDisplayString(String string) {
		fDisplayString = string;
	}

	@Override
	public String toString() {
		return getDisplayString();
	}

	/**
	 * Returns the script element proposed by the receiver, possibly
	 * <code>null</code>.
	 * 
	 * @return the script element proposed by the receiver, possibly
	 *         <code>null</code>
	 */
	public IModelElement getModelElement() {
		if (getProposalInfo() != null)
			try {
				return getProposalInfo().getModelElement();
			} catch (ModelException x) {
				DLTKUIPlugin.log(x);
			}

		return null;
	}
}
