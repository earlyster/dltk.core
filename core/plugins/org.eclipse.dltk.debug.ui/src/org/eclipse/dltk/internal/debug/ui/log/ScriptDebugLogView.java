/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/

package org.eclipse.dltk.internal.debug.ui.log;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

public class ScriptDebugLogView extends ViewPart {

	public static final String VIEW_ID = "org.eclipse.dltk.debug.ui.dbgpLogView"; //$NON-NLS-1$

	private final List items = new ArrayList();
	private TableViewer viewer;

	public ScriptDebugLogView() {
		super();
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		addColumn(Messages.Column_Date, 100, true);
		addColumn(Messages.Column_Time, 100, true);
		addColumn(Messages.Column_Type, 100, true);
		addColumn(Messages.Column_Message, 400, false);
		viewer.getTable().addListener(SWT.Resize, new Listener() {

			public void handleEvent(Event event) {
				final Table table = (Table) event.widget;
				final int columnCount = table.getColumnCount();
				int w = table.getClientArea().width;
				for (int i = 0; i < columnCount - 1; ++i) {
					w -= table.getColumn(i).getWidth();
				}
				if (w > 0) {
					table.getColumn(columnCount - 1).setWidth(w);
				}
			}

		});
		viewer.setContentProvider(new ScriptDebugLogContentProvider());
		viewer.setLabelProvider(new ScriptDebugLogLabelProvider());
		viewer.setInput(items);

		createActions();
		createMenu();
		createToolbar();
		createContextMenu();
	}

	/**
	 * @param caption
	 * @param width
	 */
	private void addColumn(String caption, int width, boolean center) {
		final TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText(caption);
		column.setWidth(width);
	}

	public void append(final ScriptDebugLogItem item) {
		synchronized (items) {
			items.add(item);
		}
		viewer.getTable().getDisplay().asyncExec(new Runnable() {

			public void run() {
				viewer.refresh(false, false);
				final Table table = viewer.getTable();
				final int itemCount = table.getItemCount();
				if (itemCount > 0) {
					table.showItem(table.getItem(itemCount - 1));
				}
			}

		});
	}

	private IAction copyAction;
	private IAction clearAction;

	public void createActions() {
		copyAction = new ScriptDebugLogCopyAction(viewer);
		clearAction = new Action(Messages.ScriptDebugLogView_clear) {
			public void run() {
				synchronized (items) {
					items.clear();
				}
				viewer.refresh();
			}
		};
	}

	private void createMenu() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
		manager.add(copyAction);
		manager.add(clearAction);
	}

	private void createToolbar() {
		IToolBarManager manager = getViewSite().getActionBars()
				.getToolBarManager();
		manager.add(copyAction);
		manager.add(clearAction);
	}

	private void createContextMenu() {
		// Create menu manager.
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});

		// Create menu.
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		// Register menu for extension.
		getSite().registerContextMenu(menuManager, viewer);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(copyAction);
		manager.add(clearAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
}
