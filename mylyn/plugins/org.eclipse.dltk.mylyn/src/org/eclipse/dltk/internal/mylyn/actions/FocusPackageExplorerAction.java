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

package org.eclipse.dltk.internal.mylyn.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.internal.mylyn.DLTKDeclarationsFilter;
import org.eclipse.dltk.internal.ui.StandardModelElementContentProvider;
import org.eclipse.dltk.internal.ui.actions.SelectionConverter;
import org.eclipse.dltk.internal.ui.scriptview.ScriptExplorerPart;
import org.eclipse.dltk.internal.ui.scriptview.ToggleLinkingAction;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.context.ui.AbstractAutoFocusViewAction;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class FocusPackageExplorerAction extends AbstractAutoFocusViewAction {

	public FocusPackageExplorerAction() {
		super(new InterestFilter(), true, true, true);
	}

	@Override
	protected ISelection resolveSelection(IEditorPart part, ITextSelection changedSelection, StructuredViewer viewer)
			throws CoreException {
		Object elementToSelect = null;
		if (changedSelection instanceof TextSelection) {
			TextSelection textSelection = (TextSelection) changedSelection;
			IModelElement javaElement = SelectionConverter.resolveEnclosingElement(part, textSelection);
			if (javaElement != null) {
				elementToSelect = javaElement;
			}
		}

		if (elementToSelect != null) {
			StructuredSelection currentSelection = (StructuredSelection) viewer.getSelection();
			if (currentSelection.size() <= 1) {
				if (elementToSelect instanceof IMember) {
					if (viewer.getContentProvider() instanceof StandardModelElementContentProvider) {
						if (!((StandardModelElementContentProvider) viewer.getContentProvider()).getProvideMembers()) {
							elementToSelect = ((IMember) elementToSelect).getSourceModule();
							return new StructuredSelection(elementToSelect);
						}
					}

					for (ViewerFilter filter : Arrays.asList(viewer.getFilters())) {
						if (filter instanceof DLTKDeclarationsFilter) {
							elementToSelect = ((IMember) elementToSelect).getSourceModule();
							return new StructuredSelection(elementToSelect);
						}
					}
				}
			}
			return new StructuredSelection(elementToSelect);
		} else {
			return null;
		}
	}

	// TODO: should have better way of doing this
	@Override
	protected void setManualFilteringAndLinkingEnabled(boolean enabled) {
		IViewPart part = super.getPartForAction();
		if (part instanceof ScriptExplorerPart) {
			for (IContributionItem item : ((ScriptExplorerPart) part).getViewSite()
					.getActionBars()
					.getToolBarManager()
					.getItems()) {
				if (item instanceof ActionContributionItem) {
					ActionContributionItem actionItem = (ActionContributionItem) item;
					if (actionItem.getAction() instanceof ToggleLinkingAction) {
						actionItem.getAction().setEnabled(enabled);
					}
				}
			}
			for (IContributionItem item : ((ScriptExplorerPart) part).getViewSite()
					.getActionBars()
					.getMenuManager()
					.getItems()) {
				if (item instanceof ActionContributionItem) {
					ActionContributionItem actionItem = (ActionContributionItem) item;
					// TODO: file bug asking for extensibility
					if (actionItem.getAction().getClass().getSimpleName().equals("ShowFilterDialogAction")) { //$NON-NLS-1$
						actionItem.getAction().setEnabled(enabled);
					}
				}
				// NOTE: turning off dynamically contributed filter items is not currently feasible
//				else if (item instanceof ContributionItem) {
//					ContributionItem contributionItem = (ContributionItem) item;
//					
//					if (contributionItem.getClass().getSimpleName().equals("FilterActionMenuContributionItem")) {
//						try {
//							Class<?> clazz = contributionItem.getClass();
//							Field field = clazz.getDeclaredField("fActionGroup");
//							field.setAccessible(true);
//							Object object = field.get(contributionItem);
//							if (object instanceof CustomFiltersActionGroup) {
//								CustomFiltersActionGroup group = (CustomFiltersActionGroup) object;
//								group.setFilters(new String[] { });
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
			}
		}
	}

	@Override
	protected void setDefaultLinkingEnabled(boolean on) {
		IViewPart part = super.getPartForAction();
		if (part instanceof ScriptExplorerPart) {
			((ScriptExplorerPart) part).setLinkingEnabled(on);
		}
	}

	@Override
	protected boolean isDefaultLinkingEnabled() {
		IViewPart part = super.getPartForAction();
		if (part instanceof ScriptExplorerPart) {
			return ((ScriptExplorerPart) part).isLinkingEnabled();
		}
		return false;
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		// TODO: get from super
		IViewPart part = super.getPartForAction();
		if (part instanceof ScriptExplorerPart) {
			viewers.add(((ScriptExplorerPart) part).getTreeViewer());
		}
		return viewers;
	}
}
