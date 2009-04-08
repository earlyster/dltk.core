/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.dltk.ui;

import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IPackageDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.corext.refactoring.tagging.ICommentProvider;
import org.eclipse.dltk.internal.ui.actions.SelectionConverter;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.text.folding.IElementCommentResolver;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.dltk.MylynStatusHandler;
import org.eclipse.mylyn.internal.dltk.search.DLTKImplementorsProvider;
import org.eclipse.mylyn.internal.dltk.search.DLTKReferencesProvider;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IWorkbenchPart;

public class DLTKEditingMonitor extends AbstractUserInteractionMonitor {

	protected IModelElement lastSelectedElement = null;

	protected IModelElement lastResolvedElement = null;

	protected ScriptEditor currentEditor;

	protected StructuredSelection currentSelection = null;

	public DLTKEditingMonitor() {
		super();
	}

	/**
	 * Only public for testing
	 */
	public void handleWorkbenchPartSelection(IWorkbenchPart part,
			ISelection selection, boolean contributeToContext) {
		try {
			IModelElement selectedElement = null;
			if (selection instanceof StructuredSelection) {
				StructuredSelection structuredSelection = (StructuredSelection) selection;

				if (structuredSelection.equals(currentSelection))
					return;
				currentSelection = structuredSelection;

				Object selectedObject = structuredSelection.getFirstElement();
				if (selectedObject instanceof IModelElement) {
					IModelElement checkedElement = checkIfAcceptedAndPromoteIfNecessary((IModelElement) selectedObject);
					if (checkedElement == null) {
						return;
					} else {
						selectedElement = checkedElement;
					}
				}
				if (selectedElement != null)
					super.handleElementSelection(part, selectedElement,
							contributeToContext);
			} else {
				if (selection instanceof ITextSelection
						&& part instanceof ScriptEditor) {
					currentEditor = (ScriptEditor) part;
					ITextSelection textSelection = (ITextSelection) selection;

					// first try to resolve if the user has clicked on a comment
					final ISourceModule module = EditorUtility
							.getEditorInputModelElement(currentEditor, false);
					if (module == null) {
						return;
					}
					final IElementCommentResolver resolver = (IElementCommentResolver) currentEditor
							.getAdapter(IElementCommentResolver.class);
					if (resolver == null) {
						return;
					}
					selectedElement = resolver.getElementByCommentPosition(
							module, textSelection.getOffset(), textSelection
									.getLength());

					// if user has clicked outside the comment, resolve the
					// desired element
					if (selectedElement == null)
						selectedElement = SelectionConverter
								.resolveEnclosingElement(currentEditor,
										textSelection);

					if (selectedElement instanceof IPackageDeclaration)
						return; // HACK: ignoring these selections
					IModelElement[] resolved = SelectionConverter
							.codeResolve(currentEditor);
					if (resolved != null && resolved.length == 1
							&& !resolved[0].equals(selectedElement)) {
						lastResolvedElement = resolved[0];
					}

					boolean selectionResolved = false;
					if (selectedElement instanceof IMethod
							&& lastSelectedElement instanceof IMethod) {
						if (lastResolvedElement != null
								&& lastSelectedElement != null
								&& lastResolvedElement.equals(selectedElement)
								&& !lastSelectedElement
										.equals(lastResolvedElement)) {
							super.handleNavigation(part, selectedElement,
									DLTKReferencesProvider.ID,
									contributeToContext);
							selectionResolved = true;
						} else if (lastSelectedElement != null
								&& lastSelectedElement
										.equals(lastResolvedElement)
								&& !lastSelectedElement.equals(selectedElement)) {
							super.handleNavigation(part, selectedElement,
									DLTKReferencesProvider.ID,
									contributeToContext);
							selectionResolved = true;
						}
					} else if (selectedElement != null
							&& lastSelectedElement != null
							&& !lastSelectedElement.equals(selectedElement)) {
						if (lastSelectedElement.getElementName().equals(
								selectedElement.getElementName())) {
							if (selectedElement instanceof IMethod
									&& lastSelectedElement instanceof IMethod) {
								super.handleNavigation(part, selectedElement,
										DLTKImplementorsProvider.ID,
										contributeToContext);
								selectionResolved = true;
							} else if (selectedElement instanceof IType
									&& lastSelectedElement instanceof IType) {
								super.handleNavigation(part, selectedElement,
										DLTKImplementorsProvider.ID,
										contributeToContext);
								selectionResolved = true;
							}
						}
					}
					if (selectedElement != null) {
						if (!selectionResolved
								&& selectedElement.equals(lastSelectedElement)) {
							super.handleElementEdit(part, selectedElement,
									contributeToContext);
						} else if (!selectedElement.equals(lastSelectedElement)) {
							super.handleElementSelection(part, selectedElement,
									contributeToContext);
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
			if (selectedElement != null)
				lastSelectedElement = selectedElement;
		} catch (ModelException e) {
			// ignore, fine to fail to resolve an element if the model is not
			// up-to-date
		} catch (Throwable t) {
			MylynStatusHandler.log(t,
					"Failed to update model based on selection.");
		}
	}

	/**
	 * @return null for elements that aren't modeled
	 */
	protected IModelElement checkIfAcceptedAndPromoteIfNecessary(
			IModelElement element) {
		// if (element instanceof IPackageDeclaration) return null;
		// if (element instanceof IImportContainer) {
		// return element.getParent();
		// } else if (element instanceof IImportDeclaration) {
		// return element.getParent().getParent();
		// } else {
		return element;
		// }
	}
}
