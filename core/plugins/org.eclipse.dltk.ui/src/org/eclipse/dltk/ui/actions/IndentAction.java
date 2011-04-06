/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.actions;

import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.core.ScriptUtils;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.formatter.FormatterException;
import org.eclipse.dltk.ui.formatter.IScriptFormatter;
import org.eclipse.dltk.ui.formatter.IScriptFormatterExtension;
import org.eclipse.dltk.ui.formatter.IScriptFormatterFactory;
import org.eclipse.dltk.ui.formatter.ScriptFormatterManager;
import org.eclipse.dltk.ui.text.util.AutoEditUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension3;
import org.eclipse.ui.texteditor.TextEditorAction;

/**
 * Indents a line or range of lines in a script document to its correct
 * position.
 * 
 * @since 2.0
 */
public class IndentAction extends TextEditorAction {

	/**
	 * Whether this is the action invoked by TAB. When <code>true</code>,
	 * indentation behaves differently to accommodate normal TAB operation.
	 */
	private final boolean fIsTabAction;

	/**
	 * Creates a new instance.
	 * 
	 * @param bundle
	 *            the resource bundle
	 * @param prefix
	 *            the prefix to use for keys in <code>bundle</code>
	 * @param editor
	 *            the text editor
	 * @param isTabAction
	 *            whether the action should insert tabs if over the indentation
	 */
	public IndentAction(ResourceBundle bundle, String prefix,
			ITextEditor editor, boolean isTabAction) {
		super(bundle, prefix, editor);
		fIsTabAction = isTabAction;
	}

	private IProject getProject() {
		final IModelElement input = EditorUtility.getEditorInputModelElement(
				getTextEditor(), false);
		if (input != null) {
			final IScriptProject scriptProject = input.getScriptProject();
			if (scriptProject != null) {
				return scriptProject.getProject();
			}
		}
		return null;
	}

	/*
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		// update has been called by the framework
		if (!isEnabled() || !validateEditorInputState())
			return;
		ITextSelection selection = getSelection();
		final IDocument document = getDocument();
		if (document != null) {
			try {
				final int offset = selection.getOffset();
				final int length = selection.getLength();
				final int startLine = document.getLineOfOffset(offset);
				int lastLine = document.getLineOfOffset(offset + length);
				if (lastLine > startLine) {
					if (document.getLineOffset(lastLine) == offset + length) {
						--lastLine;
					}
				}
				if (fIsTabAction) {
					if (startLine != lastLine) {
						// don't support multiple lines for now
						return;
					}
					final String line = getLine(document, startLine);
					String indent = getIndent(line);
					final int lineStart = document.getLineOffset(startLine);
					if (offset >= lineStart
							&& offset <= lineStart + indent.length()) {
						final String prevIndent = getPrevIndent(document,
								startLine);
						if (prevIndent != null
								&& !indent.startsWith(prevIndent)) {
							// current indent is less then previous line indent
							document.replace(lineStart, indent.length(),
									prevIndent);
							selectAndReveal(lineStart + prevIndent.length(), 0);
							return;
						}
						if (lineStart + indent.length() == offset) {
							// if we are right before the text start then just
							// insert a tab
							String tab = getTabEquivalent(indent);
							document.replace(offset, 0, tab);
							selectAndReveal(offset + tab.length(), 0);
							return;
						}
						// move caret to the text
						selectAndReveal(lineStart + indent.length(), 0);
					}
					return;
				}
				final IProject project = getProject();
				final IScriptFormatterFactory factory = ScriptFormatterManager
						.getSelected(ScriptUtils.getNatureId(getTextEditor()),
								project);
				if (factory != null) {
					Map<String, String> preferences = factory
							.retrievePreferences(new PreferencesLookupDelegate(
									project));
					preferences = factory.changeToIndentingOnly(preferences);
					final String lineDelimiter = TextUtilities
							.getDefaultLineDelimiter(document);
					final IScriptFormatter formatter = factory.createFormatter(
							lineDelimiter, preferences);
					if (project != null
							&& formatter instanceof IScriptFormatterExtension) {
						((IScriptFormatterExtension) formatter)
								.initialize(project);
					}
					final Position end = new Position(offset + length);
					document.addPosition(end);
					if (indentLines(document, startLine, lastLine, formatter)) {
						if (startLine != lastLine) {
							getTextEditor().selectAndReveal(offset,
									end.getOffset() - offset);
						} else {
							final int newOffset = document
									.getLineOffset(startLine)
									+ getIndent(getLine(document, startLine))
											.length();
							getTextEditor().selectAndReveal(newOffset, 0);
						}
					}
					document.removePosition(end);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (FormatterException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean indentLines(final IDocument document, int startLine,
			int lastLine, final IScriptFormatter formatter)
			throws BadLocationException, FormatterException {
		final int startOffset = document.getLineOffset(startLine);
		final IRegion lastLineRegion = document.getLineInformation(lastLine);
		final int lastOffset = lastLineRegion.getOffset()
				+ lastLineRegion.getLength();
		int level = formatter.detectIndentationLevel(document, startOffset);
		final String source = document.get();
		final TextEdit edit = formatter.format(source, startOffset, lastOffset
				- startOffset, level);
		if (edit == null) {
			return false;
		}
		final Document copyDoc = new Document(source);
		edit.apply(copyDoc);
		if (document.getNumberOfLines() != copyDoc.getNumberOfLines()) {
			return false;
		}
		boolean changed = false;
		final IRewriteTarget target = (IRewriteTarget) getTextEditor()
				.getAdapter(IRewriteTarget.class);
		if (target != null)
			target.beginCompoundChange();
		try {
			for (int i = startLine; i <= lastLine; ++i) {
				final String indent1 = getIndent(getLine(copyDoc, i));
				final String indent2 = getIndent(getLine(document, i));
				if (!indent1.equals(indent2)) {
					document.replace(document.getLineOffset(i),
							indent2.length(), indent1);
					changed = true;
				}
			}
		} finally {
			if (target != null)
				target.endCompoundChange();
		}
		return changed;
	}

	private static String getLine(IDocument document, int line)
			throws BadLocationException {
		final IRegion lineRegion = document.getLineInformation(line);
		return document.get(lineRegion.getOffset(), lineRegion.getLength());
	}

	private static String getIndent(String line) {
		int i = 0;
		while (i < line.length()
				&& (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
			++i;
		}
		return line.substring(0, i);
	}

	/**
	 * Returns the editor's selection provider.
	 * 
	 * @return the editor's selection provider or <code>null</code>
	 */
	private ISelectionProvider getSelectionProvider() {
		ITextEditor editor = getTextEditor();
		if (editor != null) {
			return editor.getSelectionProvider();
		}
		return null;
	}

	/*
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	@Override
	public void update() {
		super.update();
		if (isEnabled())
			if (fIsTabAction)
				setEnabled(canModifyEditor() && isSmartMode()
						&& isValidSelection());
			else
				setEnabled(canModifyEditor() && !getSelection().isEmpty());
	}

	/**
	 * Returns if the current selection is valid, i.e. whether it is empty and
	 * the caret in the whitespace at the start of a line, or covers multiple
	 * lines.
	 * 
	 * @return <code>true</code> if the selection is valid for an indent
	 *         operation
	 */
	private boolean isValidSelection() {
		ITextSelection selection = getSelection();
		if (selection.isEmpty())
			return false;
		int offset = selection.getOffset();
		int length = selection.getLength();
		IDocument document = getDocument();
		if (document == null)
			return false;
		try {
			IRegion firstLine = document.getLineInformationOfOffset(offset);
			int lineOffset = firstLine.getOffset();
			// either the selection has to be empty and the caret in the WS at
			// the line start
			// or the selection has to extend over multiple lines
			if (length == 0)
				return document.get(lineOffset, offset - lineOffset).trim()
						.length() == 0;
			else
				// return lineOffset + firstLine.getLength() < offset + length;
				return false; // only enable for empty selections for now
		} catch (BadLocationException e) {
		}
		return false;
	}

	/**
	 * Returns the smart preference state.
	 * 
	 * @return <code>true</code> if smart mode is on, <code>false</code>
	 *         otherwise
	 */
	private boolean isSmartMode() {
		ITextEditor editor = getTextEditor();
		if (editor instanceof ITextEditorExtension3)
			return ((ITextEditorExtension3) editor).getInsertMode() == ITextEditorExtension3.SMART_INSERT;
		return false;
	}

	/**
	 * Selects the given range on the editor.
	 * 
	 * @param newOffset
	 *            the selection offset
	 * @param newLength
	 *            the selection range
	 */
	private void selectAndReveal(int newOffset, int newLength) {
		Assert.isTrue(newOffset >= 0);
		Assert.isTrue(newLength >= 0);
		ITextEditor editor = getTextEditor();
		if (editor instanceof ScriptEditor) {
			ISourceViewer viewer = ((ScriptEditor) editor).getViewer();
			if (viewer != null)
				viewer.setSelectedRange(newOffset, newLength);
		} else
			// this is too intrusive, but will never get called anyway
			getTextEditor().selectAndReveal(newOffset, newLength);
	}

	/**
	 * Returns a tab equivalent, either as a tab character or as spaces,
	 * depending on the editor and formatter preferences.
	 * 
	 * @return a string representing one tab in the editor, never
	 *         <code>null</code>
	 */
	private String getTabEquivalent(String indent) {
		final ITextEditor editor = getTextEditor();
		if (!(editor instanceof ScriptEditor)) {
			return "\t";
		}
		final IPreferenceStore prefs = ((ScriptEditor) editor)
				.getScriptPreferenceStore();
		String tab;
		if (CodeFormatterConstants.SPACE.equals(prefs
				.getString(CodeFormatterConstants.FORMATTER_TAB_CHAR))) {
			final int tabSize = prefs
					.getInt(CodeFormatterConstants.FORMATTER_TAB_SIZE);
			int wsLen = whiteSpaceLength(indent, tabSize);
			tab = AutoEditUtils.getNSpaces(tabSize - (wsLen % tabSize));
		} else
			tab = "\t"; //$NON-NLS-1$
		return tab;
	}

	/**
	 * Returns the size in characters of a string. All characters count one,
	 * tabs count the editor's preference for the tab display
	 * 
	 * @param indent
	 *            the string to be measured.
	 * @param project
	 *            the project to retrieve the indentation settings from,
	 *            <b>null</b> for workspace settings
	 * @return the size in characters of a string
	 */
	private static int whiteSpaceLength(String indent, int tabSize) {
		if (indent == null)
			return 0;
		else {
			int size = 0;
			int l = indent.length();
			for (int i = 0; i < l; i++)
				size += indent.charAt(i) == '\t' ? tabSize : 1;
			return size;
		}
	}

	private String getPrevIndent(IDocument document, int n)
			throws BadLocationException {
		for (; --n >= 0;) {
			final String prevLine = getLine(document, n);
			if (prevLine.trim().length() != 0) {
				// TODO adjust indent e.g. after "{"
				return getIndent(prevLine);
			}
		}
		return null;
	}

	/**
	 * Returns the document currently displayed in the editor, or
	 * <code>null</code> if none can be obtained.
	 * 
	 * @return the current document or <code>null</code>
	 */
	private IDocument getDocument() {
		ITextEditor editor = getTextEditor();
		if (editor != null) {
			IDocumentProvider provider = editor.getDocumentProvider();
			IEditorInput input = editor.getEditorInput();
			if (provider != null && input != null)
				return provider.getDocument(input);
		}
		return null;
	}

	/**
	 * Returns the selection on the editor or an invalid selection if none can
	 * be obtained. Returns never <code>null</code>.
	 * 
	 * @return the current selection, never <code>null</code>
	 */
	private ITextSelection getSelection() {
		ISelectionProvider provider = getSelectionProvider();
		if (provider != null) {
			ISelection selection = provider.getSelection();
			if (selection instanceof ITextSelection)
				return (ITextSelection) selection;
		}
		// null object
		return TextSelection.emptySelection();
	}

}
