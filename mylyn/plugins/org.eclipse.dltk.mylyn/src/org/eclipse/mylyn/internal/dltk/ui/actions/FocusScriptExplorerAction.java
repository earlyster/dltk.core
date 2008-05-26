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

package org.eclipse.mylyn.internal.dltk.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.internal.ui.actions.SelectionConverter;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.scriptview.ScriptExplorerPart;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.context.ui.AbstractAutoFocusViewAction;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.internal.dltk.ui.DLTKDeclarationsFilter;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;

public class FocusScriptExplorerAction extends AbstractAutoFocusViewAction {

	public FocusScriptExplorerAction() {
		super(new InterestFilter(), true, true, true);
	}

	protected ISelection resolveSelection(IEditorPart part,
			ITextSelection changedSelection, StructuredViewer viewer)
			throws CoreException {
		Object elementToSelect = null;
		if (changedSelection instanceof TextSelection
				&& part instanceof ScriptEditor) {
			TextSelection textSelection = (TextSelection) changedSelection;
			IModelElement modelElement = SelectionConverter
					.resolveEnclosingElement((ScriptEditor) part, textSelection);
			if (modelElement != null) {
				elementToSelect = modelElement;
			}
		}

		if (elementToSelect != null) {
			StructuredSelection currentSelection = (StructuredSelection) viewer
					.getSelection();
			if (currentSelection.size() <= 1) {
				// for (ViewerFilter filter :
				// Arrays.asList(viewer.getFilters())) {
				for (ListIterator it = Arrays.asList(viewer.getFilters())
						.listIterator(); it.hasNext();) {
					ViewerFilter filter = (ViewerFilter) it.next();
					if (filter instanceof DLTKDeclarationsFilter
							&& elementToSelect instanceof IMember) {
						elementToSelect = ((IMember) elementToSelect)
								.getSourceModule();
					}
				}
			}
		}
		return new StructuredSelection(elementToSelect);
	}

	protected void setDefaultLinkingEnabled(boolean on) {
		IViewPart part = super.getPartForAction();
		if (part instanceof ScriptExplorerPart) {
			((ScriptExplorerPart) part).setLinkingEnabled(on);
		}
	}

	protected boolean isDefaultLinkingEnabled() {
		IViewPart part = super.getPartForAction();
		if (part instanceof ScriptExplorerPart) {
			return ((ScriptExplorerPart) part).isLinkingEnabled();
		}
		return false;
	}

	public List getViewers() {
		List viewers = new ArrayList();
		// TODO: get from super
		IViewPart part = super.getPartForAction();
		if (part instanceof ScriptExplorerPart) {
			viewers.add(((ScriptExplorerPart) part).getTreeViewer());
		}
		return viewers;
	}
}
