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

package org.eclipse.mylyn.internal.dltk;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.ui.viewsupport.IProblemChangedListener;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;

public class InterestInducingProblemListener implements
		IProblemChangedListener, IPropertyChangeListener {

	// TODO: consider getting rid of this
	private DLTKStructureBridge scriptStructureBridge;

	public InterestInducingProblemListener() {
		scriptStructureBridge = new DLTKStructureBridge();
	}

	public void problemsChanged(IResource[] changedResources,
			boolean isMarkerChange) {
		try {
			if (!ContextCorePlugin.getContextManager().isContextActive()) {
				return;
			} else {
				for (int i = 0; i < changedResources.length; i++) {
					IResource resource = changedResources[i];
					if (resource instanceof IFile) {
						IModelElement modelElement = (IModelElement) resource
								.getAdapter(IModelElement.class);
						if (modelElement != null) {
							IInteractionElement element = ContextCorePlugin
									.getContextManager().getElement(
											modelElement.getHandleIdentifier());
							if (!scriptStructureBridge.containsProblem(element)) {
								ContextCorePlugin
										.getContextManager()
										.removeErrorPredictedInterest(
												element.getHandleIdentifier(),
												scriptStructureBridge.contentType,
												true);
							} else {
								ContextCorePlugin
										.getContextManager()
										.addErrorPredictedInterest(
												element.getHandleIdentifier(),
												scriptStructureBridge.contentType,
												true);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			MylynStatusHandler.log(e, "could not update on marker change");
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (MylynDLTKPrefConstants.PREDICTED_INTEREST_ERRORS.equals(event
				.getProperty())) {
			if (MylynDLTKPlugin.getDefault().getPreferenceStore().getBoolean(
					MylynDLTKPrefConstants.PREDICTED_INTEREST_ERRORS)) {
				enable();
			} else {
				disable();
			}
		}
	}

	public void enable() {
		//DLTKUIPlugin.getDefault().getProblemMarkerManager().addListener(this);
	}

	public void disable() {
		//JavaPlugin.getDefault().getProblemMarkerManager().removeListener(this)
		// ;
	}
}
