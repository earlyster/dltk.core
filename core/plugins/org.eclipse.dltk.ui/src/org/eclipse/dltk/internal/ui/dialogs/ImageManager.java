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
package org.eclipse.dltk.internal.ui.dialogs;

import org.eclipse.dltk.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

class ImageManager extends ImageDescriptorRegistry {

	public ImageManager() {
		super(false);
	}

	@Override
	public Image get(ImageDescriptor descriptor) {
		if (descriptor == null)
			descriptor = ImageDescriptor.getMissingImageDescriptor();
		return super.get(descriptor);
	}

}
