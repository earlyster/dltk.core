/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.debug.ui.display.internal;

import org.eclipse.dltk.debug.ui.display.IEvaluateConsole;
import org.eclipse.dltk.debug.ui.display.IEvaluateConsoleFactory;
import org.eclipse.dltk.debug.ui.display.ScriptDisplayView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.activities.WorkbenchActivityHelper;
import org.eclipse.ui.internal.console.ConsolePluginImages;
import org.eclipse.ui.internal.console.IInternalConsoleConstants;

/**
 * @since 3.1
 */
public class OpenConsoleAction extends Action implements IMenuCreator {

	private ScriptDisplayView view;
	private IEvaluateConsoleFactory[] fFactoryExtensions;
	private Menu fMenu;

	public OpenConsoleAction(ScriptDisplayView view,
			IEvaluateConsoleFactory[] factories) {
		this.view = view;
		fFactoryExtensions = factories;
		setText(ConsoleMessages.OpenConsoleAction_0);
		setToolTipText(ConsoleMessages.OpenConsoleAction_1);
		setImageDescriptor(ConsolePluginImages
				.getImageDescriptor(IInternalConsoleConstants.IMG_ELCL_NEW_CON));
		setMenuCreator(this);
		// PlatformUI
		// .getWorkbench()
		// .getHelpSystem()
		// .setHelp(this,
		// IConsoleHelpContextIds.CONSOLE_OPEN_CONSOLE_ACTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IMenuCreator#dispose()
	 */
	public void dispose() {
		fFactoryExtensions = null;
	}

	/*
	 * @see
	 * org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets.
	 * Event)
	 * 
	 * @since 3.5
	 */
	public void runWithEvent(Event event) {
		if (event.widget instanceof ToolItem) {
			ToolItem toolItem = (ToolItem) event.widget;
			Control control = toolItem.getParent();
			Menu menu = getMenu(control);

			Rectangle bounds = toolItem.getBounds();
			Point topLeft = new Point(bounds.x, bounds.y + bounds.height);
			menu.setLocation(control.toDisplay(topLeft));
			menu.setVisible(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets
	 * .Control)
	 */
	public Menu getMenu(Control parent) {
		if (fMenu != null) {
			fMenu.dispose();
		}

		fMenu = new Menu(parent);
		int accel = 1;
		for (int i = 0; i < fFactoryExtensions.length; i++) {
			IEvaluateConsoleFactory extension = fFactoryExtensions[i];
			if (!WorkbenchActivityHelper.filterItem(extension)
					&& extension.isEnabled()) {
				String label = extension.getLabel();
				ImageDescriptor image = extension.getImageDescriptor();
				addActionToMenu(fMenu, new ConsoleFactoryAction(label, image,
						extension), accel);
				accel++;
			}
		}
		return fMenu;
	}

	private void addActionToMenu(Menu parent, Action action, int accelerator) {
		if (accelerator < 10) {
			StringBuffer label = new StringBuffer();
			// add the numerical accelerator
			label.append('&');
			label.append(accelerator);
			label.append(' ');
			label.append(action.getText());
			action.setText(label.toString());
		}

		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets
	 * .Menu)
	 */
	public Menu getMenu(Menu parent) {
		return null;
	}

	private class ConsoleFactoryAction extends Action {

		private IEvaluateConsoleFactory fConfig;

		public ConsoleFactoryAction(String label, ImageDescriptor image,
				IEvaluateConsoleFactory extension) {
			setText(label);
			if (image != null) {
				setImageDescriptor(image);
			}
			fConfig = extension;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		public void run() {
			final IEvaluateConsole console = fConfig.create();
			if (console != null) {
				view.addConsole(console);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.action.IAction#runWithEvent(org.eclipse.swt.widgets
		 * .Event)
		 */
		public void runWithEvent(Event event) {
			run();
		}
	}
}
