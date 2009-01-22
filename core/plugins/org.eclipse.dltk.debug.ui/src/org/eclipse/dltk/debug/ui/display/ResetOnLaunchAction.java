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
package org.eclipse.dltk.debug.ui.display;

import org.eclipse.jface.action.Action;

public class ResetOnLaunchAction extends Action {

	private final DebugConsolePage page;

	public ResetOnLaunchAction(DebugConsolePage page) {
		super(Messages.ResetOnLaunchAction_text, AS_CHECK_BOX);
		this.page = page;
	}

	public void run() {
		page.setResetOnLaunch(isChecked());
	}

}
