package org.eclipse.dltk.validators.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.validators.internal.core.ValidatorUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.IPatternMatchListener;

public abstract class AbstractValidateJob extends Job {

	protected abstract void invokeValidationFor(final OutputStream out,
			final List elements, final List resources, IProgressMonitor monitor);

	public AbstractValidateJob(String jobName) {
		super(jobName);
	}

	private final Set selectionElements = new HashSet();
	private final Set selectionResources = new HashSet();

	public void run(IStructuredSelection selection) {
		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			ValidatorUtils.processResourcesToElements(o, selectionElements,
					selectionResources);
		}
		setRule(buildSchedulingRule());
		setUser(true);
		schedule();
	}

	private ISchedulingRule buildSchedulingRule() {
		final Set resources = new HashSet(selectionResources);
		for (Iterator i = selectionElements.iterator(); i.hasNext();) {
			final ISourceModule module = (ISourceModule) i.next();
			resources.add(module.getResource());
		}
		final ISchedulingRule[] rules = new ISchedulingRule[resources.size()];
		resources.toArray(rules);
		return MultiRule.combine(rules);
	}

	protected IStatus run(IProgressMonitor monitor) {
		IOConsoleOutputStream newOutputStream = null;
		try {
			if (isConsoleRequired()) {
				IConsoleManager consoleManager = ConsolePlugin.getDefault()
						.getConsoleManager();
				IOConsole ioConsole = new IOConsole(getConsoleName(), null);
				IPatternMatchListener[] listeners = ValidatorConsoleTrackerManager
						.getListeners();
				for (int i = 0; i < listeners.length; i++) {
					ioConsole.addPatternMatchListener(listeners[i]);
				}
				consoleManager.addConsoles(new IConsole[] { ioConsole });
				consoleManager.showConsoleView(ioConsole);
				newOutputStream = ioConsole.newOutputStream();
			}
			invokeValidationFor(newOutputStream, new ArrayList(
					selectionElements), new ArrayList(selectionResources),
					monitor);
		} finally {
			try {
				if (newOutputStream != null) {
					newOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Status.OK_STATUS;
	}

	protected String getConsoleName() {
		final String message = Messages.AbstractValidateSelectionWithConsole_dltkValidatorOutput;
		return NLS.bind(message, getName());
	}

	protected boolean isConsoleRequired() {
		return true;
	}
}
