/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.wizards;

import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @since 2.0
 */
public class ControlDecorationManager implements IControlDecorationManager {

	private Map<Control, ControlDecoration> decorations = new IdentityHashMap<Control, ControlDecoration>();

	public void show(Control control, IStatus status) {
		ControlDecoration decoration = decorations.get(control);
		if (decoration == null) {
			decoration = new ControlDecoration(control, SWT.LEFT | SWT.TOP);
			decorations.put(control, decoration);
		}
		final Image image = getImageFor(status);
		if (decoration.getImage() != image) {
			final Composite parent = control.getParent();
			if (parent.getLayout() instanceof GridLayout) {
				final GridLayout layout = (GridLayout) parent.getLayout();
				if (image.getBounds().width > layout.horizontalSpacing) {
					layout.horizontalSpacing = image.getBounds().width;
					parent.layout();
				}
			}
			decoration.setImage(image);
		}
		decoration.setDescriptionText(status.getMessage());
		decoration.show();
	}

	protected Image getImageFor(IStatus status) {
		final Image image = DLTKUIPlugin.getImageDescriptorRegistry().get(
		// status.getSeverity() == IStatus.ERROR ?
				DLTKPluginImages.DESC_OVR_ERROR
		// : DLTKPluginImages.DESC_OVR_WARNING
				);
		return image;
	}

	/**
	 * @param control
	 */
	public void hide(Control control) {
		ControlDecoration decoration = decorations.get(control);
		if (decoration != null) {
			decoration.hide();
		}
	}

	public void dispose() {
		for (ControlDecoration decoration : decorations.values()) {
			decoration.hide();
			decoration.dispose();
		}
		decorations.clear();
	}

	public void commit() {
		// empty
	}

	private static class ControlDecorationManagerWorkingCopy implements
			IControlDecorationManager {

		private final ControlDecorationManager manager;
		private Map<Control, ControlDecoration> activeDecorations = new IdentityHashMap<Control, ControlDecoration>();

		public ControlDecorationManagerWorkingCopy(
				ControlDecorationManager manager) {
			this.manager = manager;
			this.activeDecorations.putAll(manager.decorations);
		}

		public void commit() {
			if (!activeDecorations.isEmpty()) {
				for (Control control : activeDecorations.keySet()) {
					manager.hide(control);
				}
				activeDecorations.clear();
			}
		}

		public void hide(Control control) {
			activeDecorations.remove(control);
			manager.hide(control);
		}

		public void show(Control control, IStatus status) {
			activeDecorations.remove(control);
			manager.show(control, status);
		}

	}

	public IControlDecorationManager beginReporting() {
		return new ControlDecorationManagerWorkingCopy(this);
	}

}
