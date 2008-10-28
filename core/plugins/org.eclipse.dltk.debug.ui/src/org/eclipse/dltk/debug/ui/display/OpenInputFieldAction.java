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
package org.eclipse.dltk.debug.ui.display;

import org.eclipse.dltk.debug.ui.ScriptDebugImages;
import org.eclipse.jface.action.Action;

public class OpenInputFieldAction extends Action {

	private final DebugConsolePage page;

	public OpenInputFieldAction(DebugConsolePage page) {
		super(Messages.OpenInputFieldAction_openCodeField, AS_CHECK_BOX);
		setImageDescriptor(ScriptDebugImages
				.getImageDescriptor(ScriptDebugImages.IMG_OBJS_SHOW_SNIPPET_FIELD));
		this.page = page;
	}

	public void run() {
		if (isChecked()) {
			page.openInputField();
		} else {
			page.closeInputField();
		}
	}
}
