/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.console.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.dltk.console.IScriptInterpreter;
import org.eclipse.dltk.console.ScriptConsoleHistory;
import org.eclipse.dltk.console.ScriptConsolePrompt;
import org.eclipse.dltk.console.ui.internal.ICommandHandler;
import org.eclipse.dltk.console.ui.internal.ScriptConsoleInput;
import org.eclipse.dltk.console.ui.internal.ScriptConsolePage;
import org.eclipse.dltk.console.ui.internal.ScriptConsoleSession;
import org.eclipse.dltk.console.ui.internal.ScriptConsoleViewer;
import org.eclipse.dltk.console.ui.internal.ScriptConsoleViewer.ConsoleDocumentListener;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.IConsoleDocumentPartitioner;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.part.IPageBookViewPage;

public class ScriptConsole extends TextConsole implements ICommandHandler {
	private ILaunch launch = null;
	private ILaunchesListener2 listener = null;

	private class ScriptConsoleLaunchListener implements ILaunchesListener2 {
		public void launchesTerminated(ILaunch[] launches) {
			if (terminated) {
				return;
			}
			for (int i = 0; i < launches.length; i++) {
				if (launches[i].equals(launch)) {
					final ScriptConsoleViewer consoleViewer = (ScriptConsoleViewer) page
							.getViewer();
					page.getControl().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (consoleViewer != null) {
								consoleViewer.disableProcessing();
								appendInvitation(consoleViewer);
								updateText(
										consoleViewer,
										Messages.ScriptConsole_processTerminated,
										false);
								consoleViewer.setEditable(false);
							}
						}
					});
				}
			}
		}

		public void launchesAdded(ILaunch[] launches) {
		}

		public void launchesChanged(ILaunch[] launches) {
		}

		public void launchesRemoved(ILaunch[] launches) {
		}
	};

	private final class InitialStreamReader implements Runnable {
		private final IScriptInterpreter interpreter;

		private InitialStreamReader(IScriptInterpreter interpreter) {
			this.interpreter = interpreter;
		}

		public void run() {
			// We need to be sure what page is already created
			while (page == null || (page != null && page.getViewer() == null)) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}
			final ScriptConsoleViewer viewer = (ScriptConsoleViewer) page
					.getViewer();
			InputStream stream = interpreter.getInitialOutputStream();
			if (stream == null) {
				return;
			}
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(stream));
			Thread readerThread = new Thread(new Runnable() {
				public void run() {
					boolean first = true;
					while (!terminated) {
						String readLine;
						try {
							readLine = reader.readLine();
							if (readLine != null) {
								updateText(viewer, readLine, first);
								first = false;
							} else {
								break;
							}
						} catch (IOException e) {
							if (DLTKCore.DEBUG) {
								e.printStackTrace();
							}
							break;
						}
					}
					if (!first) {
						/*
						 * output invitation only if there was some output -
						 * initially invitation is printed from the clear()
						 * called from the ScriptConsoleViewer constructor
						 */
						appendInvitation(viewer);
					}
					enableEdit(viewer);
				}

			});
			readerThread.start();
		}

	}

	protected void appendInvitation(final ScriptConsoleViewer viewer) {
		Control control = viewer.getControl();
		if (control == null) {
			return;
		}
		control.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					viewer.disableProcessing();
					getDocumentListener().appendDelimeter();
					getDocumentListener().appendInvitation();
					viewer.enableProcessing();
				} catch (BadLocationException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	protected void enableEdit(final ScriptConsoleViewer viewer) {
		Control control = viewer.getControl();
		if (control == null) {
			return;
		}
		control.getDisplay().asyncExec(new Runnable() {
			public void run() {
				viewer.setEditable(true);
			}
		});
	}

	private void updateText(final ScriptConsoleViewer viewer,
			final String text, final boolean clean) {
		Control control = viewer.getControl();
		if (control == null) {
			return;
		}
		control.getDisplay().asyncExec(new Runnable() {
			public void run() {
				viewer.disableProcessing();
				IDocument document = getDocument();
				try {
					if (clean) {
						document.replace(0, document.getLength(), text);
						getDocumentListener().appendDelimeter();
					} else {
						document.replace(document.getLength(), 0, text);
						getDocumentListener().appendDelimeter();
					}
					IDocumentPartitioner partitioner = viewer.getDocument()
							.getDocumentPartitioner();
					if (partitioner instanceof ScriptConsolePartitioner) {
						ScriptConsolePartitioner scriptConsolePartitioner = (ScriptConsolePartitioner) partitioner;
						scriptConsolePartitioner.clearRanges();
						viewer.getTextWidget().redraw();
					}
				} catch (BadLocationException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
				viewer.enableProcessing();
			}
		});
	}

	private ScriptConsolePage page;

	private ScriptConsolePartitioner partitioner;

	private IContentAssistProcessor processor;

	private ITextHover hover;

	private IScriptInterpreter interpreter;

	private ScriptConsoleSession session;

	private ListenerList consoleListeners;

	private ScriptConsolePrompt prompt;

	private ScriptConsoleHistory history;

	private boolean terminated = false;

	protected IConsoleDocumentPartitioner getPartitioner() {
		return partitioner;
	}

	public ScriptConsolePage getPage() {
		return page;
	}

	public ScriptConsole(String consoleName, String consoleType,
			ImageDescriptor image) {
		super(consoleName, consoleType, image, true);

		this.consoleListeners = new ListenerList(ListenerList.IDENTITY);
		this.prompt = new ScriptConsolePrompt("=>", "->"); //$NON-NLS-1$ //$NON-NLS-2$
		this.history = new ScriptConsoleHistory();

		this.session = new ScriptConsoleSession();
		addListener(this.session);

		partitioner = new ScriptConsolePartitioner();
		getDocument().setDocumentPartitioner(partitioner);
		partitioner.connect(getDocument());
	}

	public ScriptConsole(String consoleName, String consoleType) {
		this(consoleName, consoleType, null);
	}

	public IScriptConsoleSession getSession() {
		return session;
	}

	public void addListener(IScriptConsoleListener listener) {
		consoleListeners.add(listener);
	}

	public void removeListener(IScriptConsoleListener listener) {
		consoleListeners.remove(listener);
	}

	protected void setContentAssistProcessor(IContentAssistProcessor processor) {
		this.processor = processor;
	}

	protected void setInterpreter(final IScriptInterpreter interpreter) {
		this.interpreter = interpreter;
		interpreter.addInitialListenerOperation(new InitialStreamReader(
				interpreter));
	}

	public void setPrompt(ScriptConsolePrompt prompt) {
		this.prompt = prompt;
	}

	public ScriptConsolePrompt getPrompt() {
		return prompt;
	}

	public ScriptConsoleHistory getHistory() {
		return history;
	}

	protected void setTextHover(ITextHover hover) {
		this.hover = hover;
	}

	private ConsoleDocumentListener documentListener;

	public ConsoleDocumentListener getDocumentListener() {
		if (documentListener == null) {
			documentListener = new ConsoleDocumentListener(this, this
					.getPrompt(), this.getHistory());
			documentListener.setDocument(getDocument());

		}

		return documentListener;
	}

	public IPageBookViewPage createPage(IConsoleView view) {
		SourceViewerConfiguration cfg = new ScriptConsoleSourceViewerConfiguration(
				processor, hover);
		page = createPage(view, cfg);
		return page;
	}

	protected ScriptConsolePage createPage(IConsoleView view,
			SourceViewerConfiguration cfg) {
		return new ScriptConsolePage(this, view, cfg);
	}

	public void clearConsole() {
		page.clearConsolePage();
	}

	public IScriptConsoleInput getInput() {
		return new ScriptConsoleInput(page);
	}

	public int getState() {
		return interpreter.getState();
	}

	public String handleCommand(String userInput) throws IOException {
		if (this.interpreter == null && this.interpreter.isValid()) {
			return ""; //$NON-NLS-1$
		}
		Object[] listeners = consoleListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((IScriptConsoleListener) listeners[i]).userRequest(userInput);
		}

		interpreter.exec(userInput);

		String output = interpreter.getOutput();

		if (interpreter.getState() == IScriptInterpreter.WAIT_NEW_COMMAND) {
			prompt.setMode(true);
		} else {
			prompt.setMode(false);
		}

		for (int i = 0; i < listeners.length; i++) {
			((IScriptConsoleListener) listeners[i]).interpreterResponse(output);
		}

		return output;
	}

	public void terminate() {
		terminated = true;
		try {
			interpreter.close();
		} catch (IOException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	public void dispose() {
		partitioner.clearRanges();

		terminate();
		if (listener != null) {
			DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(
					listener);
		}

		super.dispose();
	}

	public void setLaunch(ILaunch launch) {
		this.launch = launch;
		if (this.listener == null) {
			this.listener = new ScriptConsoleLaunchListener();
			DebugPlugin.getDefault().getLaunchManager().addLaunchListener(
					listener);
		}
	}
}
