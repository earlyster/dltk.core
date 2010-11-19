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
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.core.ScriptUtils;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.ui.formatter.FormatterException;
import org.eclipse.dltk.ui.formatter.IScriptFormatter;
import org.eclipse.dltk.ui.formatter.IScriptFormatterExtension;
import org.eclipse.dltk.ui.formatter.IScriptFormatterFactory;
import org.eclipse.dltk.ui.formatter.ScriptFormatterManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

/**
 * Indents a line or range of lines in a script document to its correct
 * position.
 * 
 * @since 2.0
 */
public class IndentAction extends TextEditorAction {

	/**
	 * Creates a new instance.
	 * 
	 * @param bundle
	 *            the resource bundle
	 * @param prefix
	 *            the prefix to use for keys in <code>bundle</code>
	 * @param editor
	 *            the text editor
	 */
	public IndentAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
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
				int offset = selection.getOffset();
				int length = selection.getLength();
				int startLine = document.getLineOfOffset(offset);
				int lastLine = document.getLineOfOffset(offset + length);
				if (lastLine > startLine) {
					if (document.getLineOffset(lastLine) == offset + length) {
						--lastLine;
					}
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
			setEnabled(canModifyEditor() && !getSelection().isEmpty());
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
