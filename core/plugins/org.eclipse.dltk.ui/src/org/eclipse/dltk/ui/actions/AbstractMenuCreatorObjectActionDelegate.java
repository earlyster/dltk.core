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
package org.eclipse.dltk.ui.actions;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public abstract class AbstractMenuCreatorObjectActionDelegate implements
		IObjectActionDelegate, IMenuCreator {

	protected interface IMenuBuilder {
		void addAction(IAction action);

		void addSeparator();
	}

	private static class MenuBuilder implements IMenuBuilder {

		private final Menu menu;

		public MenuBuilder(Menu menu) {
			this.menu = menu;
		}

		public void addAction(IAction action) {
			new ActionContributionItem(action).fill(menu, -1);
		}

		public void addSeparator() {
			new MenuItem(menu, SWT.SEPARATOR);
		}

	}

	// current action
	private IAction fDelegateAction;

	// whether to re-fill the menu (reset on selection change)
	private boolean fFillMenu = true;

	// current selection
	private IStructuredSelection currentSelection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// We don't have a need for the active part.
	}

	public void run(IAction action) {
		// Never called because we become a menu.
	}

	public void selectionChanged(IAction action, ISelection newSelection) {
		// if the selection is an IResource, save it and enable our action
		if (newSelection instanceof IStructuredSelection) {
			fFillMenu = true;
			if (fDelegateAction != action) {
				fDelegateAction = action;
				fDelegateAction.setMenuCreator(this);
			}
			// enable our menu
			action.setEnabled(true);
			currentSelection = (IStructuredSelection) newSelection;
			return;
		}
		action.setEnabled(false);
		currentSelection = null;
	}

	public void dispose() {
		// nothing to do
	}

	public Menu getMenu(Control parent) {
		// never called
		return null;
	}

	public Menu getMenu(Menu parent) {
		/*
		 * Create the new menu. The menu will get filled when it is about to be
		 * shown. see fillMenu(Menu).
		 */
		final Menu menu = new Menu(parent);
		/*
		 * Add listener to re-populate the menu each time it is shown because
		 * MenuManager.update(boolean, boolean) doesn't dispose pull-down
		 * ActionContribution items for each popup menu.
		 */
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				if (fFillMenu) {
					final Menu m = (Menu) e.widget;
					final MenuItem[] items = m.getItems();
					for (int i = 0; i < items.length; i++) {
						items[i].dispose();
					}
					fillMenu(new MenuBuilder(m), currentSelection);
					fFillMenu = false;
				}
			}
		});
		return menu;
	}

	/**
	 * @param menu
	 */
	protected abstract void fillMenu(IMenuBuilder menu,
			IStructuredSelection selection);

}
