/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.formatter;

import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.formatter.internal.ScriptFormattingContextProperties;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * Formatting strategy for java source code.
 * 
 * @since 3.0
 */
@SuppressWarnings("restriction")
public class ScriptFormattingStrategy extends ContextBasedFormattingStrategy {

	private final String natureId;

	private static class FormatJob {
		final IDocument document;
		final TypedPosition partition;
		final ISourceModule module;
		final IProject project;
		final String formatterId;

		/**
		 * @param document
		 * @param partition
		 * @param project
		 */
		public FormatJob(IDocument document, TypedPosition partition,
				ISourceModule module, IProject project, String formatterId) {
			this.document = document;
			this.partition = partition;
			this.module = module;
			this.project = project;
			this.formatterId = formatterId;
		}

	}

	/** Jobs to be formatted by this strategy */
	private final LinkedList<FormatJob> fJobs = new LinkedList<FormatJob>();

	/**
	 * Creates a new java formatting strategy.
	 */
	public ScriptFormattingStrategy(String natureId) {
		this.natureId = natureId;
	}

	/*
	 * @see ContextBasedFormattingStrategy#format()
	 */
	@Override
	public void format() {
		super.format();
		final FormatJob job = fJobs.removeFirst();
		BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(),
				new Runnable() {
					public void run() {
						doFormat(job);
					}
				});
	}

	/**
	 * @since 2.0
	 */
	protected void doFormat(final FormatJob job) {
		final IDocument document = job.document;
		final TypedPosition partition = job.partition;

		if (document != null && partition != null) {
			@SuppressWarnings("rawtypes")
			Map partitioners = null;
			try {
				int offset = partition.getOffset();

				final IScriptFormatterFactory formatterFactory = selectFormatterFactory(job);
				if (formatterFactory != null) {
					final String lineDelimiter = TextUtilities
							.getDefaultLineDelimiter(document);
					@SuppressWarnings("unchecked")
					final Map<String, String> prefs = getPreferences();
					final IScriptFormatter formatter = formatterFactory
							.createFormatter(lineDelimiter, prefs);
					if (job.project != null
							&& formatter instanceof IScriptFormatterExtension) {
						((IScriptFormatterExtension) formatter)
								.initialize(job.project);
					}
					if (job.module != null
							&& formatter instanceof IScriptFormatterExtension2) {
						((IScriptFormatterExtension2) formatter)
								.initialize(job.module);
					}
					final int indentationLevel = formatter
							.detectIndentationLevel(document, offset);
					final TextEdit edit = formatter.format(document.get(),
							offset, partition.getLength(), indentationLevel);
					if (edit != null) {
						if (edit.getChildrenSize() > 20)
							partitioners = TextUtilities
									.removeDocumentPartitioners(document);
						edit.apply(document);
					}
				}
			} catch (FormatterSyntaxProblemException e) {
				reportFormatError(NLS
						.bind(FormatterMessages.ScriptFormattingStrategy_unableToFormatSourceContainingSyntaxError,
								e.getMessage()));
			} catch (FormatterException e) {
				reportFormatError(NLS
						.bind(FormatterMessages.ScriptFormattingStrategy_unableToFormat,
								e.getMessage()));
			} catch (MalformedTreeException e) {
				DLTKUIPlugin
						.warn(FormatterMessages.ScriptFormattingStrategy_formattingError,
								e);
			} catch (BadLocationException e) {
				// Can only happen on concurrent document modification
				DLTKUIPlugin
						.warn(FormatterMessages.ScriptFormattingStrategy_formattingError,
								e);
			} catch (Exception e) {
				final String msg = NLS
						.bind(FormatterMessages.ScriptFormattingStrategy_unexpectedFormatterError,
								e.toString());
				DLTKUIPlugin.logErrorMessage(msg, e);
			} finally {
				if (partitioners != null)
					TextUtilities.addDocumentPartitioners(document,
							partitioners);
			}
		}
	}

	private void reportFormatError(String message) {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		WorkbenchWindow window = (WorkbenchWindow) workbench
				.getActiveWorkbenchWindow();
		if (window != null && window.getStatusLineManager() != null) {
			window.getStatusLineManager().setErrorMessage(message);
		}
		workbench.getDisplay().beep();
	}

	protected IScriptFormatterFactory selectFormatterFactory(FormatJob job) {
		IScriptFormatterFactory factory = (IScriptFormatterFactory) ScriptFormatterManager
				.getInstance().getContributionById(job.formatterId);
		if (factory != null) {
			return factory;
		}
		return ScriptFormatterManager.getSelected(natureId, job.project);
	}

	@Override
	public void formatterStarts(final IFormattingContext context) {
		super.formatterStarts(context);
		final IDocument document = (IDocument) context
				.getProperty(FormattingContextProperties.CONTEXT_MEDIUM);
		final TypedPosition partition = (TypedPosition) context
				.getProperty(FormattingContextProperties.CONTEXT_PARTITION);
		final ISourceModule module = (ISourceModule) context
				.getProperty(ScriptFormattingContextProperties.MODULE);
		final IProject project = (IProject) context
				.getProperty(ScriptFormattingContextProperties.CONTEXT_PROJECT);
		final String formatterId = (String) context
				.getProperty(ScriptFormattingContextProperties.CONTEXT_FORMATTER_ID);
		fJobs.addLast(new FormatJob(document, partition, module, project,
				formatterId));
	}

	@Override
	public void formatterStops() {
		super.formatterStops();
		fJobs.clear();
	}
}
