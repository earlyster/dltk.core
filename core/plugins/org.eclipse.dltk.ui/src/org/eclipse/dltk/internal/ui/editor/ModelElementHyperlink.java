/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - Initial implementation
 *     xored software, Inc. - implement getHyperlinkText() & getTypeLabel (Alex Panchenko)  
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.editor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.internal.ui.DelegatedOpen;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.actions.OpenAction;
import org.eclipse.dltk.ui.infoviews.ModelElementArray;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.osgi.util.NLS;

/**
 * Model element hyperlink.
 */
public class ModelElementHyperlink implements IHyperlink {

	private final IRegion fRegion;
	private final OpenAction fOpenAction;
	private final Object selection;

	/**
	 * Creates a new Script element hyperlink.
	 */
	public ModelElementHyperlink(IRegion region, Object selection,
			OpenAction openAction) {
		Assert.isNotNull(openAction);
		Assert.isNotNull(region);

		fRegion = region;
		fOpenAction = openAction;
		this.selection = selection;
	}

	public IRegion getHyperlinkRegion() {
		return fRegion;
	}

	public void open() {
		if (selection instanceof ModelElementArray) {
			fOpenAction.selectAndOpen(((ModelElementArray) selection)
					.getElements());
		} else {
			fOpenAction.run(new Object[] { selection });
		}
	}

	public String getTypeLabel() {
		return DLTKEditorMessages.ModelElementHyperlink_typeLabel;
	}

	private final long TITLE_FLAGS = ScriptElementLabels.ALL_FULLY_QUALIFIED
			| ScriptElementLabels.M_APP_RETURNTYPE
			| ScriptElementLabels.M_PARAMETER_TYPES
			| ScriptElementLabels.M_PARAMETER_NAMES
			| ScriptElementLabels.M_EXCEPTIONS
			| ScriptElementLabels.F_APP_TYPE_SIGNATURE
			| ScriptElementLabels.M_PRE_TYPE_PARAMETERS
			| ScriptElementLabels.T_TYPE_PARAMETERS
			| ScriptElementLabels.USE_RESOLVED;

	public String getHyperlinkText() {
		final String text;
		if (selection instanceof IModelElement) {
			final IModelElement me = (IModelElement) selection;
			text = ScriptElementLabels.getDefault().getElementLabel(me,
					TITLE_FLAGS);
		} else if (selection instanceof DelegatedOpen) {
			text = ((DelegatedOpen) selection).getName();
		} else if (selection instanceof ModelElementArray) {
			final ModelElementArray array = (ModelElementArray) selection;
			text = array.getContentDescription();
		} else {
			return DLTKEditorMessages.ModelElementHyperlink_defaultText;
		}
		return NLS.bind(DLTKEditorMessages.ModelElementHyperlink_text, text);
	}
}
