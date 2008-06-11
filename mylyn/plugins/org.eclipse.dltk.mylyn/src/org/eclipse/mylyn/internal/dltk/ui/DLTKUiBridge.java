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
/*
 * Created on Apr 6, 2005
 */
package org.eclipse.mylyn.internal.dltk.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.actions.SelectionConverter;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.dltk.DLTKStructureBridge;
import org.eclipse.mylyn.internal.dltk.MylynStatusHandler;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class DLTKUiBridge extends AbstractContextUiBridge {

	private Field scriptOutlineField = null;


	public DLTKUiBridge() {
		try {
			scriptOutlineField = ScriptOutlinePage.class
					.getDeclaredField("fOutlineViewer");
			scriptOutlineField.setAccessible(true);
		} catch (Exception e) {
			MylynStatusHandler.fail(e,
					"could not get install Mylyn on Outline viewer", true);
		}
	}

	public void open(IInteractionElement node) {
		// get the element and open it in an editor
		IModelElement modelElement = DLTKCore
				.create(node.getHandleIdentifier());
		if (modelElement == null || !modelElement.exists())
			return;
		try {
			IEditorPart part = EditorUtility.openInEditor(modelElement);
			EditorUtility.revealInEditor(part, modelElement);
		} catch (Throwable t) {
			MylynStatusHandler.fail(t, "Could not open editor for: " + node,
					true);
		}
	}

	// private boolean explorerLinked =
	// PreferenceConstants.getPreferenceStore().getBoolean(
	// PreferenceConstants.LINK_PACKAGES_TO_EDITOR);

	// public void setContextCapturePaused(boolean paused) {
	// PackageExplorerPart explorer =
	// PackageExplorerPart.getFromActivePerspective();
	// if (paused) {
	// explorerLinked = PreferenceConstants.getPreferenceStore().getBoolean(
	// PreferenceConstants.LINK_PACKAGES_TO_EDITOR);
	// if (explorerLinked) { // causes delayed selection
	// if (explorer != null)
	// explorer.setLinkingEnabled(false);
	// }
	// } else {
	// if (explorer != null)
	// explorer.setLinkingEnabled(true);
	// PreferenceConstants.getPreferenceStore().setValue(PreferenceConstants.
	// LINK_PACKAGES_TO_EDITOR,
	// explorerLinked);
	// if (explorer != null) {
	// explorer.setLinkingEnabled(explorerLinked);
	// }
	// }
	// }

	// @Override
	public void restoreEditor(IInteractionElement document) {
		IResource resource = ResourcesUiBridgePlugin.getDefault()
				.getResourceForElement(document, false);
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		if (activePage != null && resource instanceof IFile
				&& resource.exists()) {
			try {
				IDE.openEditor(activePage, (IFile) resource, false);
			} catch (PartInitException e) {
				MylynStatusHandler.fail(e, "failed to open editor for: "
						+ resource, false);
			}
		}
	}

	public void close(IInteractionElement node) {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				IEditorReference[] references = page.getEditorReferences();
				for (int i = 0; i < references.length; i++) {
					IEditorPart part = references[i].getEditor(false);
					if (part != null && part instanceof ScriptEditor) {
						ScriptEditor editor = (ScriptEditor) part;
						Object adapter = editor.getEditorInput().getAdapter(
								IModelElement.class);
						if (adapter instanceof IModelElement
								&& node.getHandleIdentifier().equals(
										((IModelElement) adapter)
												.getHandleIdentifier())) {
							editor.close(true);
						}
					}
				}
			}
		} catch (Throwable t) {
			MylynStatusHandler.fail(t, "Could not auto close editor.", false);
		}
	}

	public boolean acceptsEditor(IEditorPart editorPart) {
		return editorPart instanceof ScriptEditor;
	}

	public IInteractionElement getElement(IEditorInput input) {
		Object adapter = input.getAdapter(IModelElement.class);
		if (adapter instanceof IModelElement) {
			IModelElement modelElement = (IModelElement) adapter;
			String handle = ContextCorePlugin.getDefault().getStructureBridge(
					modelElement).getHandleIdentifier(modelElement);
			return ContextCorePlugin.getContextManager().getElement(handle);
		} else {
			return null;
		}
	}

	public List getContentOutlineViewers(IEditorPart editorPart) {
		if (editorPart == null || scriptOutlineField == null)
			return null;
		List viewers = new ArrayList();
		Object out = editorPart.getAdapter(IContentOutlinePage.class);
		if (out instanceof ScriptOutlinePage) {
			ScriptOutlinePage page = (ScriptOutlinePage) out;
			if (page != null && page.getControl() != null) {
				try {
					viewers.add((TreeViewer) scriptOutlineField.get(page));
				} catch (Exception e) {
					MylynStatusHandler.log(e, "could not get outline viewer");
				}
			}
		}
		return viewers;
	}

	public Object getObjectForTextSelection(TextSelection selection,
			IEditorPart editor) {
		if (editor instanceof ScriptEditor) {
			TextSelection textSelection = selection;
			try {
				if (selection != null) {
					return SelectionConverter.resolveEnclosingElement(
							(ScriptEditor) editor, textSelection);
				} else {
					Object element = ((ScriptEditor) editor).getEditorInput()
							.getAdapter(IModelElement.class);
					if (element instanceof IModelElement)
						return element;
				}
			} catch (ModelException e) {
				// ignore
			}
		}
		return null;
	}

	public String getContentType() {
		return "script";
	}

}
