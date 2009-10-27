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

import java.util.Date;

import org.eclipse.dltk.internal.ui.text.DLTKColorManager;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import com.ibm.icu.text.SimpleDateFormat;

public class ScriptDebugLogLabelProvider extends LabelProvider implements
		ITableLabelProvider, IColorProvider {

	private final IColorManager colorManager = new DLTKColorManager(false);

	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd"); //$NON-NLS-1$

	private final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"HH:mm:ss.SSS"); //$NON-NLS-1$

	@Override
	public void dispose() {
		super.dispose();
		colorManager.dispose();
	}

	public Image getColumnImage(Object element, int columnIndex) {
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
				if (item.getSessionId() > 0) {
					return String.valueOf(item.getSessionId());
				} else {
					break;
				}
			case 4:
				return formatMessage(item);
			}
		}
		return null;
	}

	private static final String XML_DECL_BEGIN = "<?xml"; //$NON-NLS-1$
	private static final String XML_DECL_END = "?>"; //$NON-NLS-1$

	private String formatMessage(ScriptDebugLogItem item) {
		String result = item.getMessage();
		if (result.startsWith(XML_DECL_BEGIN)) {
			int end = result.indexOf(XML_DECL_END);
			if (end >= 0) {
				end += XML_DECL_END.length();
				while (end < result.length()
						&& Character.isWhitespace(result.charAt(end))) {
					++end;
				}
				result = result.substring(end);
			}
		}
		return result.replaceAll("[\\p{Cntrl}]+", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public Color getBackground(Object element) {
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
