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

package org.eclipse.mylyn.internal.dltk.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.context.ui.AbstractFocusViewAction;
import org.eclipse.ui.IViewPart;


public class FocusDebugViewAction extends AbstractFocusViewAction {

	public FocusDebugViewAction() {
		super(new InterestFilter(), true, true, false);
	}
	

	public List getViewers() {
		List viewers = new ArrayList();
		IViewPart view = super.getPartForAction();
//		if (view instanceof LaunchView) {
//			LaunchView launchView = (LaunchView)view;
//			viewers.add((StructuredViewer)launchView.getViewer());
//		}
		return viewers;
	}
	
}
