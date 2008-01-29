package org.eclipse.dltk.validators.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.validators.internal.core.ValidatorUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.IPatternMatchListener;

public abstract class AbstractValidateSelectionWithConsole implements IObjectActionDelegate {

	public static final String DLTK_VALIDATORS_CONSOLE = "DLTK Validators output";

	protected abstract void invoceValidationFor(final OutputStream out, final List elements,
			final List resources, IProgressMonitor monitor);

	ISelection selection;

	public AbstractValidateSelectionWithConsole() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		IConsoleManager consoleManager = ConsolePlugin.getDefault()
				.getConsoleManager();
		IOConsole ioConsole = new IOConsole(DLTK_VALIDATORS_CONSOLE, null);
		IPatternMatchListener[] listeners = ValidatorConsoleTrackerManager
				.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			ioConsole.addPatternMatchListener(listeners[i]);
		}
		consoleManager.addConsoles(new IConsole[] { ioConsole });
		consoleManager.showConsoleView(ioConsole);
		IOConsoleOutputStream newOutputStream = ioConsole.newOutputStream();
		if (this.selection == null) {
			return;
		}
		processSelectionToElements(newOutputStream, selection);
		try {
			newOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void processSelectionToElements(final OutputStream out, ISelection selection) {
		final List elements = new ArrayList();
		final List resources = new ArrayList();
		if (this.selection != null
				&& this.selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) this.selection;
			Iterator iterator = sel.iterator();
			for (; iterator.hasNext();) {
				Object o = iterator.next();
				ValidatorUtils.processResourcesToElements(o, elements,
						resources);
			}
		}
	
		// ValidatorRuntime.executeAllValidatorsWithConsole(out, elements,
		// resources);
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell());
		try {
			dialog.run(true, true, new IRunnableWithProgress() {
	
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					invoceValidationFor(out, elements, resources, monitor);
				}
	
			});
		} catch (InvocationTargetException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}