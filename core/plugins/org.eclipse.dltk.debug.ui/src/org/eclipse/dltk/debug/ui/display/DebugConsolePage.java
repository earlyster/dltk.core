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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.dltk.console.ScriptConsoleConstants;
import org.eclipse.dltk.console.ui.ScriptConsole;
import org.eclipse.dltk.console.ui.internal.ScriptConsolePage;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.internal.debug.ui.ScriptEvaluationContextManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.texteditor.IUpdate;

public class DebugConsolePage extends ScriptConsolePage {

	private DebugEventListener debugEventListener = null;

	/**
	 * @param console
	 * @param view
	 * @param cfg
	 */
	public DebugConsolePage(ScriptConsole console, IConsoleView view,
			SourceViewerConfiguration cfg) {
		super(console, view, cfg);
	}

	protected IAction createTerminateConsoleAction() {
		return null;
	}

	private IAction runAction;
	private IAction resetOnLaunchAction;

	private boolean resetOnLaunch;

	protected void createActions() {
		super.createActions();
		final IActionBars actionBars = getSite().getActionBars();
		final IToolBarManager tbManager = actionBars.getToolBarManager();
		tbManager.appendToGroup(ScriptConsoleConstants.SCRIPT_GROUP,
				new OpenInputFieldAction(this));
		runAction = new RunInputFieldAction(this);
		tbManager.appendToGroup(ScriptConsoleConstants.SCRIPT_GROUP, runAction);
		resetOnLaunchAction = new ResetOnLaunchAction(this);
		resetOnLaunchAction.setChecked(resetOnLaunch);
		actionBars.getMenuManager().add(resetOnLaunchAction);
		updateActions();
	}

	private SashForm sash;
	private StyledText inputField;
	private boolean enabled = true;

	/**
	 * @param value
	 */
	private void setEnabled(final boolean value) {
		if (value != this.enabled) {
			this.enabled = value;
			if (inputField != null)
				inputField.setEditable(value);
			getViewer().setEditable(value);
			final Control control = getViewer().getControl();
			control.setBackground(value ? null : control.getDisplay()
					.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		}
	}

	@Override
	public Control getControl() {
		return sash != null ? sash : super.getControl();
	}

	/*
	 * @see TextConsolePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		sash = new SashForm(parent, SWT.VERTICAL | SWT.SMOOTH);
		inputField = new StyledText(sash, SWT.V_SCROLL | SWT.H_SCROLL);
		inputField.setEditable(true);
		super.createControl(sash);
		inputField.setFont(getViewer().getControl().getFont());
		inputField.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateActions();
			}

		});
		sash.setMaximizedControl(getViewer().getControl());
		setEnabled(isDebuggerAvailable());
		if (debugEventListener == null) {
			debugEventListener = new DebugEventListener();
			DebugPlugin.getDefault().addDebugEventListener(debugEventListener);
		}
		enableUpdateJob.schedule(500);
	}

	private boolean isDebuggerAvailable() {
		final IPageSite site = getSite();
		if (site == null) {
			return false;
		}
		final IWorkbenchPage page = site.getPage();
		if (page == null) {
			return false;
		}
		final IWorkbenchPart part = page.getActivePart();
		if (part == null) {
			return false;
		}
		final IScriptStackFrame frame = ScriptEvaluationContextManager
				.getEvaluationContext(part);
		if (frame != null) {
			final IScriptThread thread = frame.getScriptThread();
			if (thread != null) {
				return thread.isSuspended();
			}
		}
		return false;
	}

	/**
	 * @see org.eclipse.dltk.console.ui.internal.ScriptConsolePage#dispose()
	 */
	public void dispose() {
		if (debugEventListener != null) {
			DebugPlugin.getDefault().removeDebugEventListener(
					debugEventListener);
			debugEventListener = null;
		}
		super.dispose();
	}

	public boolean canExecuteInputField() {
		return sash != null && sash.getMaximizedControl() == null
				&& inputField.getText().length() != 0;
	}

	public void openInputField() {
		if (sash != null) {
			sash.setWeights(new int[] { 30, 70 });
			sash.setMaximizedControl(null);
			inputField.setFocus();
		}
		updateActions();
	}

	public void closeInputField() {
		if (sash != null) {
			final Control consoleControl = getControl();
			sash.setMaximizedControl(consoleControl);
			consoleControl.setFocus();
		}
		updateActions();
	}

	private void updateActions() {
		if (runAction instanceof IUpdate) {
			((IUpdate) runAction).update();
		}
	}

	public void executeInputField() {
		if (inputField != null) {
			final String input = inputField.getText();
			((ScriptConsole) getConsole()).executeCommand(input);
		}
	}

	private final Job enableUpdateJob = new Job("Enable update") { //$NON-NLS-1$

		protected IStatus run(IProgressMonitor monitor) {
			DLTKDebugUIPlugin.getStandardDisplay().asyncExec(new Runnable() {
				public void run() {
					setEnabled(isDebuggerAvailable());
				}
			});
			return Status.OK_STATUS;
		}

	};

	private final class DebugEventListener implements IDebugEventSetListener {
		public void handleDebugEvents(DebugEvent[] events) {
			enableUpdateJob.schedule(500);
			if (resetOnLaunch && isTargetCreate(events)) {
				DLTKDebugUIPlugin.getStandardDisplay().asyncExec(
						new Runnable() {
							public void run() {
								((DebugConsole) getConsole()).clearConsole();
							}
						});
			}
		}
	}

	private static boolean isTargetCreate(DebugEvent[] events) {
		for (int i = 0; i < events.length; ++i) {
			final DebugEvent event = events[i];
			if (event.getKind() == DebugEvent.CREATE
					&& event.getSource() instanceof IDebugTarget) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the resetOnLaunch
	 */
	public boolean isResetOnLaunch() {
		return resetOnLaunch;
	}

	/**
	 * @param resetOnLaunch
	 *            the resetOnLaunch to set
	 */
	public void setResetOnLaunch(boolean resetOnLaunch) {
		this.resetOnLaunch = resetOnLaunch;
	}
}
