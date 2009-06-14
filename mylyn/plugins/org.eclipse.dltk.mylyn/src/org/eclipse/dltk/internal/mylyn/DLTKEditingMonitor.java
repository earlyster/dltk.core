/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.internal.mylyn;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IPackageDeclaration;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.mylyn.search.DLTKImplementorsProvider;
import org.eclipse.dltk.internal.mylyn.search.DLTKReferencesProvider;
import org.eclipse.dltk.internal.ui.actions.SelectionConverter;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Mik Kersten
 */
public class DLTKEditingMonitor extends AbstractUserInteractionMonitor {

	protected IModelElement lastSelectedElement = null;

	protected IModelElement lastResolvedElement = null;

	protected IEditorPart currentEditor;

	protected StructuredSelection currentSelection = null;

	public DLTKEditingMonitor() {
		super();
	}

	/**
	 * Only public for testing. Note: Two sequential selections on the same element are deemed to be an edit of the
	 * selection as this is the best guess that can be made. See bug 252306.
	 */
	@Override
	public void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection, boolean contributeToContext) {
		try {
			IModelElement selectedElement = null;
			if (selection instanceof StructuredSelection) {
				StructuredSelection structuredSelection = (StructuredSelection) selection;

				if (structuredSelection.equals(currentSelection)) {
					return;
				}
				currentSelection = structuredSelection;

				for (Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) {
					Object selectedObject = iterator.next();
					if (selectedObject instanceof IModelElement) {
						IModelElement checkedElement = checkIfAcceptedAndPromoteIfNecessary((IModelElement) selectedObject);
						if (checkedElement == null) {
							return;
						} else {
							selectedElement = checkedElement;
						}
					}
					if (selectedElement != null) {
						super.handleElementSelection(part, selectedElement, contributeToContext);
					}
				}
			} else {
				if (selection instanceof TextSelection && part instanceof IEditorPart) {
					currentEditor = (IEditorPart) part;
					TextSelection textSelection = (TextSelection) selection;
					selectedElement = SelectionConverter.resolveEnclosingElement(currentEditor, textSelection);
					if (selectedElement instanceof IPackageDeclaration) {
						// HACK: ignoring these selections
						return;
					}
					IModelElement[] resolved = SelectionConverter.codeResolve(currentEditor);
					if (resolved != null && resolved.length == 1 && !resolved[0].equals(selectedElement)) {
						lastResolvedElement = resolved[0];
					}

					boolean selectionResolved = false;
					if (selectedElement instanceof IMethod && lastSelectedElement instanceof IMethod) {
						// navigation between two elements
						if (lastResolvedElement != null && lastSelectedElement != null
								&& lastResolvedElement.equals(selectedElement)
								&& !lastSelectedElement.equals(lastResolvedElement)) {
							super.handleNavigation(part, selectedElement, DLTKReferencesProvider.ID,
									contributeToContext);
							selectionResolved = true;
						} else if (lastSelectedElement != null && lastSelectedElement.equals(lastResolvedElement)
								&& !lastSelectedElement.equals(selectedElement)) {
							super.handleNavigation(part, selectedElement, DLTKReferencesProvider.ID,
									contributeToContext);
							selectionResolved = true;
						}
					} else if (selectedElement != null && lastSelectedElement != null
							&& !lastSelectedElement.equals(selectedElement)) {
						if (lastSelectedElement.getElementName().equals(selectedElement.getElementName())) {
							// navigation between two elements
							if (selectedElement instanceof IMethod && lastSelectedElement instanceof IMethod) {
								super.handleNavigation(part, selectedElement, DLTKImplementorsProvider.ID,
										contributeToContext);
								selectionResolved = true;
							} else if (selectedElement instanceof IType && lastSelectedElement instanceof IType) {
								super.handleNavigation(part, selectedElement, DLTKImplementorsProvider.ID,
										contributeToContext);
								selectionResolved = true;
							}
						}
					}
					if (selectedElement != null) {
						// selection of an element
						if (!selectionResolved && selectedElement.equals(lastSelectedElement)) {
							super.handleElementEdit(part, selectedElement, contributeToContext);
						} else if (!selectedElement.equals(lastSelectedElement)) {
							super.handleElementSelection(part, selectedElement, contributeToContext);
						}
					}

					IModelElement checkedElement = checkIfAcceptedAndPromoteIfNecessary(selectedElement);
					if (checkedElement == null) {
						return;
					} else {
						selectedElement = checkedElement;
					}
				}
			}
			if (selectedElement != null) {
				lastSelectedElement = selectedElement;
			}
		} catch (ModelException e) {
			// ignore, fine to fail to resolve an element if the model is not up-to-date
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, DLTKUiBridgePlugin.ID_PLUGIN,
					"Failed to update model based on selection", t)); //$NON-NLS-1$
		}
	}

	/**
	 * @return null for elements that aren't modeled
	 */
	protected IModelElement checkIfAcceptedAndPromoteIfNecessary(IModelElement element) {
		// if (element instanceof IPackageDeclaration) return null;
//		if (element instanceof IImportContainer) {
//			return element.getParent();
//		} else if (element instanceof IImportDeclaration) {
//			return element.getParent().getParent();
//		} else {
		return element;
//		}
	}
}
