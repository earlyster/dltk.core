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
package org.eclipse.dltk.internal.ui.wizards.buildpath;

import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class BPVariableElementLabelProvider extends LabelProvider implements
		IColorProvider {

	// shared, do not dispose:
	private Image fJARImage;
	private Image fFolderImage;
	private Color fResolvedBackground;

	// private Image fDeprecatedJARImage;
	// private Image fDeprecatedFolderImage;

	private boolean fHighlightReadOnly;

	public BPVariableElementLabelProvider(boolean highlightReadOnly) {
		fJARImage = DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_EXTJAR);
		fFolderImage = PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_OBJ_FOLDER);

		// fDeprecatedJARImage = new DecorationOverlayIcon(fJARImage,
		// DLTKPluginImages.DESC_OVR_DEPRECATED, IDecoration.TOP_LEFT)
		// .createImage();
		// fDeprecatedFolderImage = new DecorationOverlayIcon(fFolderImage,
		// DLTKPluginImages.DESC_OVR_DEPRECATED, IDecoration.TOP_LEFT)
		// .createImage();

		fHighlightReadOnly = highlightReadOnly;
		fResolvedBackground = null;
	}

	/*
	 * @see LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof BPVariableElement) {
			BPVariableElement curr = (BPVariableElement) element;
			IPath path = curr.getPath();
			if (path.toFile().isFile()) {
				return fJARImage;
			}
			return fFolderImage;
		}
		return super.getImage(element);
	}

	/*
	 * @see LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof BPVariableElement) {
			BPVariableElement curr = (BPVariableElement) element;
			String name = curr.getName();
			IPath path = curr.getPath();

			String result = name;
			ArrayList restrictions = new ArrayList(2);

			if (curr.isReadOnly() && fHighlightReadOnly) {
				restrictions
						.add(NewWizardMessages.BPVariableElementLabelProvider_read_only);
			}
			// if (curr.isDeprecated()) {
			// restrictions
			// .add(NewWizardMessages.BPVariableElementLabelProvider_deprecated);
			// }
			if (restrictions.size() == 1) {
				result = NLS
						.bind(
								NewWizardMessages.BPVariableElementLabelProvider_one_restriction,
								new Object[] { result, restrictions.get(0) });
			} else if (restrictions.size() == 2) {
				result = NLS
						.bind(
								NewWizardMessages.BPVariableElementLabelProvider_two_restrictions,
								new Object[] { result, restrictions.get(0),
										restrictions.get(1) });
			}

			if (path != null) {
				String appendix;
				if (!path.isEmpty()) {
					// TODO(alon): Figure out the best compatibility mode for
					// 3.3 vs 3.4
					appendix = TextProcessor.process(path.toOSString(),
							"*.?/\\:."); //$NON-NLS-1$
					// appendix = BasicElementLabels.getPathLabel(path, true);
				} else {
					appendix = NewWizardMessages.BPVariableElementLabelProvider_empty;
				}
				result = NLS
						.bind(
								NewWizardMessages.BPVariableElementLabelProvider_appendix,
								new Object[] { result, appendix });
			}

			return result;
		}

		return super.getText(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
	 */
	public Color getBackground(Object element) {
		if (element instanceof BPVariableElement) {
			BPVariableElement curr = (BPVariableElement) element;
			if (fHighlightReadOnly && curr.isReadOnly()) {
				if (fResolvedBackground == null) {
					Display display = Display.getCurrent();
					fResolvedBackground = display
							.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
				}
				return fResolvedBackground;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		super.dispose();
		// fDeprecatedFolderImage.dispose();
		// fDeprecatedJARImage.dispose();
	}

}
