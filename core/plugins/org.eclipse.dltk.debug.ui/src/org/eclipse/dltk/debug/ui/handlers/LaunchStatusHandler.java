/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.debug.ui.handlers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.launching.ILaunchStatusHandler;
import org.eclipse.dltk.launching.ILaunchStatusHandlerExtension;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class LaunchStatusHandler implements ILaunchStatusHandler,
		ILaunchStatusHandlerExtension {

	private IDebugTarget debugTarget;
	private final Object lock = new Object();
	private boolean disposed = false;
	private LaunchStatusDialog dialog = null;

	public void initialize(IDebugTarget target, IProgressMonitor monitor) {
		if (Display.getCurrent() != null) {
			throw new IllegalStateException(getClass().getSimpleName()
					+ " should be initialized in background threads only");
		}
		this.debugTarget = target;
	}

	private boolean isDialogCreated() {
		synchronized (lock) {
			return dialog != null;
		}
	}

	private boolean isDisposed() {
		synchronized (lock) {
			return disposed;
		}
	}

	public void updateElapsedTime(final long elapsedTime) {
		if (isDisposed()) {
			return;
		}
		asyncExec(new Runnable() {
			public void run() {
				if (!isDialogCreated()) {
					createDialog();
				}
				final LaunchStatusDialog d = dialog;
				if (d != null)
					d.updateElapsedTime(elapsedTime);
			}
		});
	}

	/**
	 * @param runnable
	 */
	private void asyncExec(Runnable runnable) {
		DLTKDebugUIPlugin.getStandardDisplay().asyncExec(runnable);
	}

	protected void createDialog() {
		if (isDialogCreated()) {
			return;
		}
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window == null
				&& PlatformUI.getWorkbench().getWorkbenchWindowCount() > 0) {
			window = PlatformUI.getWorkbench().getWorkbenchWindows()[0];
		}
		dialog = new LaunchStatusDialog(window != null ? window.getShell()
				: null, this);
		final ILaunch launch = debugTarget.getLaunch();
		if (launch != null) {
			final ILaunchConfiguration configuration = launch
					.getLaunchConfiguration();
			if (configuration != null) {
				dialog.setLaunchName(configuration.getName());
			}
		}
		final IProcess process = debugTarget.getProcess();
		String cmdLine = process != null ? process
				.getAttribute(IProcess.ATTR_CMDLINE) : null;
		dialog.setCommandLine(cmdLine);
		dialog.open();
	}

	protected void disposeDialog() {
		if (isDialogCreated()) {
			dialog.close();
			dialog = null;
		}
	}

	public void dispose() {
		if (isDisposed()) {
			return;
		}
		synchronized (lock) {
			disposed = true;
		}
		if (isDialogCreated()) {
			asyncExec(new Runnable() {
				public void run() {
					disposeDialog();
				}
			});
		}
	}

	private boolean canceled = false;

	/**
	 * @since 2.0
	 */
	public boolean isCanceled() {
		synchronized (lock) {
			return canceled;
		}
	}

	/**
	 * @since 2.0
	 */
	public void setCanceled(boolean value) {
		synchronized (lock) {
			canceled = value;
		}
	}

}
