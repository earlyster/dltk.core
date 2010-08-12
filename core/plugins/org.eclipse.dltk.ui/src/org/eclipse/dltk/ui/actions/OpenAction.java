/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelStatusConstants;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.internal.ui.actions.ActionMessages;
import org.eclipse.dltk.internal.ui.actions.ActionUtil;
import org.eclipse.dltk.internal.ui.actions.OpenActionUtil;
import org.eclipse.dltk.internal.ui.actions.SelectionConverter;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.util.ExceptionHandler;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.IEditorStatusLine;

/**
 * This action opens a Script editor on a Script element or file.
 * <p>
 * The action is applicable to selections containing elements of type
 * <code>ISourceModule</code>, <code>IMember</code> or <code>IFile</code>.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class OpenAction extends SelectionDispatchAction {

	private IEditorPart fEditor;

	/**
	 * Creates a new <code>OpenAction</code>. The action requires that the
	 * selection provided by the site's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code> .
	 * 
	 * @param site
	 *            the site providing context information for this action
	 */
	public OpenAction(IWorkbenchSite site) {
		super(site);
		setText(ActionMessages.OpenAction_label);
		setToolTipText(ActionMessages.OpenAction_tooltip);
		setDescription(ActionMessages.OpenAction_description);
		if (DLTKCore.DEBUG) {
			System.err.println("Add help support here..."); //$NON-NLS-1$
		}

		// PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
		// IScriptHelpContextIds.OPEN_ACTION);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call
	 * this constructor.
	 * 
	 * @param editor
	 *            the Script editor
	 */
	public OpenAction(IEditorPart editor) {
		this(editor.getEditorSite());
		fEditor = editor;
		setText(ActionMessages.OpenAction_declaration_label);
		setEnabled(EditorUtility.getEditorInputModelElement(fEditor, false) != null);
	}

	/*
	 * (non-Javadoc) Method declared on SelectionDispatchAction.
	 */
	@Override
	public void selectionChanged(ITextSelection selection) {
	}

	/*
	 * (non-Javadoc) Method declared on SelectionDispatchAction.
	 */
	@Override
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(checkEnabled(selection));
	}

	private boolean checkEnabled(IStructuredSelection selection) {
		if (selection.isEmpty())
			return false;
		for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (!checkElement(element)) {
				return false;
			}
		}
		return true;
	}

	protected boolean checkElement(Object element) {
		if ((element instanceof ISourceReference)
				|| ((element instanceof IAdaptable) && (((IAdaptable) element)
						.getAdapter(ISourceReference.class) != null)))
			return true;
		if ((element instanceof IFile)
				|| ((element instanceof IAdaptable) && (((IAdaptable) element)
						.getAdapter(IFile.class) != null)))
			return true;
		if ((element instanceof IStorage)
				|| ((element instanceof IAdaptable) && (((IAdaptable) element)
						.getAdapter(IStorage.class) != null)))
			return true;
		return false;
	}

	/**
	 * This method allows alternative editor implementations to override the
	 * resolution logic.
	 * 
	 * @return
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	protected Object[] resolveElements() throws InvocationTargetException,
			InterruptedException {
		return SelectionConverter.resolveForked(fEditor, false);
	}

	/*
	 * (non-Javadoc) Method declared on SelectionDispatchAction.
	 */
	@Override
	public void run(ITextSelection selection) {
		if (!isProcessable())
			return;
		final Object[] elements;
		try {
			elements = resolveElements();
		} catch (InvocationTargetException e) {
			showError(e);
			return;
		} catch (InterruptedException e) {
			return;
		}
		selectAndOpen(elements);
	}

	public void selectAndOpen(Object[] elements) {
		elements = filterElements(elements);
		if (elements == null || elements.length == 0) {
			IEditorStatusLine statusLine = null;
			if (fEditor != null)
				statusLine = (IEditorStatusLine) fEditor
						.getAdapter(IEditorStatusLine.class);
			if (statusLine != null)
				statusLine.setMessage(true,
						ActionMessages.OpenAction_error_messageBadSelection,
						null);
			getShell().getDisplay().beep();
			return;
		}
		Object element = elements[0];
		if (elements.length > 1) {
			element = OpenActionUtil.select(elements, getShell(),
					getDialogTitle(), ActionMessages.OpenAction_select_element);
			if (element == null)
				return;
		}
		if (element instanceof IModelElement) {
			int type = ((IModelElement) element).getElementType();
			if (type == IModelElement.SCRIPT_PROJECT
					|| type == IModelElement.PROJECT_FRAGMENT
					|| type == IModelElement.SCRIPT_FOLDER)
				element = EditorUtility.getEditorInputModelElement(fEditor,
						false);
		}
		run(new Object[] { element });
	}

	private Object[] filterElements(Object[] elements) {
		if (elements == null)
			return null;

		Map<Object, Object> uniqueElements = new HashMap<Object, Object>();
		for (int i = 0; i < elements.length; i++) {
			Object element = elements[i];
			if (element instanceof IModelElement) {
				final IModelElement module = ((IModelElement) element)
						.getAncestor(IModelElement.SOURCE_MODULE);
				if (module != null) {
					if (!uniqueElements.containsKey(module)) {
						uniqueElements.put(module, element);
					}
				}
			} else {
				uniqueElements.put(element, element);
			}
		}
		return uniqueElements.values().toArray();
	}

	private boolean isProcessable() {
		if (fEditor != null) {
			IModelElement je = EditorUtility.getEditorInputModelElement(
					fEditor, false);
			if (je instanceof ISourceModule
					&& !ScriptModelUtil.isPrimary((ISourceModule) je))
				return true; // can process non-primary working copies
		}
		return ActionUtil.isProcessable(getShell(), fEditor);
	}

	/*
	 * (non-Javadoc) Method declared on SelectionDispatchAction.
	 */
	@Override
	public void run(IStructuredSelection selection) {
		if (!checkEnabled(selection))
			return;
		run(selection.toArray());
	}

	/**
	 * Note: this method is for internal use only. Clients should not call this
	 * method.
	 * 
	 * @param elements
	 *            the elements to process
	 */
	public void run(Object[] elements) {
		if (elements == null)
			return;
		for (int i = 0; i < elements.length; i++) {
			Object element = elements[i];
			try {
				element = getElementToOpen(element);
				boolean activateOnOpen = fEditor != null ? true : OpenStrategy
						.activateOnOpen();
				OpenActionUtil.open(element, activateOnOpen);
			} catch (ModelException e) {
				DLTKUIPlugin.log(new Status(IStatus.ERROR,
						DLTKUIPlugin.PLUGIN_ID,
						IModelStatusConstants.INTERNAL_ERROR,
						ActionMessages.OpenAction_error_message, e));

				ErrorDialog.openError(getShell(), getDialogTitle(),
						ActionMessages.OpenAction_error_messageProblems,
						e.getStatus());

			} catch (PartInitException x) {

				String name = null;

				if (element instanceof IModelElement) {
					name = ((IModelElement) element).getElementName();
				} else if (element instanceof IStorage) {
					name = ((IStorage) element).getName();
				} else if (element instanceof IResource) {
					name = ((IResource) element).getName();
				}

				if (name != null) {
					MessageDialog
							.openError(
									getShell(),
									ActionMessages.OpenAction_error_messageProblems,
									NLS.bind(
											ActionMessages.OpenAction_error_messageArgs,
											name, x.getMessage()));
				}
			}
		}
	}

	/**
	 * Note: this method is for internal use only. Clients should not call this
	 * method.
	 * 
	 * @param object
	 *            the element to open
	 * @return the real element to open
	 * @throws ModelException
	 *             if an error occurs while accessing the Script model
	 */
	public Object getElementToOpen(Object object) throws ModelException {
		Object target = null;

		if (((object instanceof ISourceReference) != true)
				&& ((object instanceof IModelElement) != true)
				&& ((object instanceof IFile) != true)
				&& (object instanceof IAdaptable)) {
			IAdaptable adaptable = (IAdaptable) object;

			target = adaptable.getAdapter(ISourceReference.class);
			if (target == null) {
				target = adaptable.getAdapter(IModelElement.class);
				if (target == null) {
					target = adaptable.getAdapter(IFile.class);
					if (target == null) {
						target = adaptable.getAdapter(IStorage.class);
					}
				}
			}
		}

		if (target == null) {
			target = object;
		}

		return target;
	}

	private String getDialogTitle() {
		return ActionMessages.OpenAction_error_title;
	}

	private void showError(InvocationTargetException e) {
		ExceptionHandler.handle(e, getShell(), getDialogTitle(),
				ActionMessages.OpenAction_error_message);
	}
}
