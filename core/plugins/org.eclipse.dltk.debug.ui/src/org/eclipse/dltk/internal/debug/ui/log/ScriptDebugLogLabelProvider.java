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
package org.eclipse.dltk.internal.debug.ui.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.dltk.internal.ui.text.DLTKColorManager;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

public class ScriptDebugLogLabelProvider implements ITableLabelProvider,
		IColorProvider {

	private final IColorManager colorManager = new DLTKColorManager(false);

	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd"); //$NON-NLS-1$

	private final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"HH:mm:ss.SSS"); //$NON-NLS-1$

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	}

	public void dispose() {
		colorManager.dispose();
	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ScriptDebugLogItem) {
			final ScriptDebugLogItem item = (ScriptDebugLogItem) element;
			switch (columnIndex) {
			case 0:
				return dateFormat.format(new Date(item.getTimestamp()));
			case 1:
				return timeFormat.format(new Date(item.getTimestamp()));
			case 2:
				return item.getType();
			case 3:
				return item.getMessage();
			}
		}
		return null;
	}

	public Color getBackground(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	private final RGB textColor = new RGB(85, 85, 85);
	private final RGB inputColor = new RGB(0, 0, 255);
	private final RGB outputColor = new RGB(0, 128, 0);

	public Color getForeground(Object element) {
		if (element instanceof ScriptDebugLogItem) {
			final ScriptDebugLogItem item = (ScriptDebugLogItem) element;
			if (item.getType() == Messages.ItemType_Input) {
				return colorManager.getColor(inputColor);
			} else if (item.getType() == Messages.ItemType_Output) {
				return colorManager.getColor(outputColor);
			} else {
				return colorManager.getColor(textColor);
			}
		}
		return null;
	}
}
