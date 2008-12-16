/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.debug.ui.display;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.dltk.console.IScriptInterpreter;
import org.eclipse.dltk.console.ui.ScriptConsole;
import org.eclipse.dltk.console.ui.internal.ScriptConsolePage;
import org.eclipse.dltk.dbgp.IDbgpProperty;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.internal.debug.ui.ScriptEvaluationContextManager;
import org.eclipse.dltk.utils.TextUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleView;

public class DebugConsole extends ScriptConsole {

	/**
	 * @param consoleName
	 * @param consoleType
	 */
	public DebugConsole(String consoleName, String consoleType,
			IScriptInterpreter interpreter) {
		super(consoleName, consoleType);
		setInterpreter(interpreter);
		setContentAssistProcessor(new IContentAssistProcessor() {

			public String getErrorMessage() {
				return null;
			}

			public IContextInformationValidator getContextInformationValidator() {
				return null;
			}

			public char[] getContextInformationAutoActivationCharacters() {
				return null;
			}

			public char[] getCompletionProposalAutoActivationCharacters() {
				return null;
			}

			public IContextInformation[] computeContextInformation(
					ITextViewer viewer, int offset) {
				return null;
			}

			public ICompletionProposal[] computeCompletionProposals(
					ITextViewer viewer, int offset) {
				IScriptThread thread = getScriptThread();
				if (thread == null)
					return null;
				// TODO should we try to cache this tree?
				SortedMap tree;
				try {
					IDbgpProperty[] contextProperties = thread.getDbgpSession()
							.getCoreCommands().getContextProperties(0);
					tree = getTree(contextProperties);
				} catch (DbgpException ex) {
					return null;
				}

				SortedMap hit = null;
				String text = viewer.getDocument().get();
				int lastIndex = text.lastIndexOf("=>");
				if (lastIndex != -1)
					text = text.substring(lastIndex + 2);
				if (text.endsWith("."))
					text += " ";
				String[] segments = TextUtils.split(text, '.');
				int beginOffset = 0;
				if (text.equals("") || segments.length == 0) {
					hit = tree;
				} else {
					SortedMap map = tree;
					String fullName = "";
					int lastSegment = 0;
					while (lastSegment != segments.length - 1) {
						for (; lastSegment < segments.length - 1; lastSegment++) {
							fullName += segments[lastSegment];
							SortedMap segmentMap = (SortedMap) map
									.get(segments[lastSegment]);
							if (segmentMap == null) {
								break;
							}
							fullName += ".";
							map = segmentMap;
						}
						if (lastSegment < segments.length - 1) {
							try {
								IDbgpProperty property = thread
										.getDbgpSession().getCoreCommands()
										.getProperty(fullName);
								if (property != null) {
									map.put(property.getName(),
											getTree(property
													.getAvailableChildren()));
									SortedMap segmentMap = (SortedMap) map
											.get(segments[lastSegment]);
									if (segmentMap == null) {
										break;
									}
									fullName += ".";
									map = segmentMap;
									lastSegment++;
								} else {
									return null;
								}
							} catch (DbgpException ex) {
								return null;
							}
						}
					}

					String lastSegmentString = segments[lastSegment];
					if (lastSegmentString.equals(" ")) {
						hit = map;
					} else {
						hit = (SortedMap) map.get(lastSegmentString);
					}
					if (hit == null) {
						hit = new TreeMap();
						beginOffset = lastSegmentString.length();
						Iterator iterator = map.entrySet().iterator();
						while (iterator.hasNext()) {
							Map.Entry entry = (Entry) iterator.next();
							if (((String) entry.getKey())
									.startsWith(lastSegmentString)) {
								hit.put(entry.getKey(), entry.getKey());
							}
						}
					}
				}
				ICompletionProposal[] proposals = new ICompletionProposal[hit
						.size()];
				Iterator iterator = hit.keySet().iterator();
				int i = 0;
				while (iterator.hasNext()) {
					String proposal = (String) iterator.next();
					proposals[i++] = new CompletionProposal(proposal
							.substring(beginOffset), offset, 0, proposal
							.length(), null, proposal, null, null);
				}
				return proposals;
			}

			private SortedMap getTree(IDbgpProperty[] properties) {
				SortedMap map = new TreeMap();
				for (int i = 0; i < properties.length; i++) {
					IDbgpProperty property = properties[i];
					map.put(property.getName(), getTree(property
							.getAvailableChildren()));
				}
				return map.size() == 0 ? null : map;
			}

			private IScriptThread getScriptThread() {
				final IWorkbenchWindow site = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				if (site == null) {
					return null;
				}
				final IWorkbenchPage page = site.getActivePage();
				if (page == null) {
					return null;
				}
				final IWorkbenchPart part = page.getActivePart();
				if (part == null) {
					return null;
				}
				final IScriptStackFrame frame = ScriptEvaluationContextManager
						.getEvaluationContext(part);
				if (frame != null) {
					final IScriptThread thread = frame.getScriptThread();
					if (thread != null) {
						return thread;
					}
				}
				return null;
			}
		});
		getHistory().restoreState(getPreferences().getString(CONSOLE_HISTORY));
	}

	private static final String CONSOLE_HISTORY = "debug.console.history";

	public void dispose() {
		getPreferences().setValue(CONSOLE_HISTORY, getHistory().saveState());
		DLTKDebugUIPlugin.getDefault().savePluginPreferences();
		super.dispose();
	}

	private IPreferenceStore getPreferences() {
		return DLTKDebugUIPlugin.getDefault().getPreferenceStore();
	}

	protected ScriptConsolePage createPage(IConsoleView view,
			SourceViewerConfiguration cfg) {
		return new DebugConsolePage(this, view, cfg);
	}

}
