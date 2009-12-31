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
package org.eclipse.dltk.ui.documentation;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.dltk.core.DLTKCore;

/**
 * @since 2.0
 */
public class DocumentationUtils {

	/**
	 * Returns the {@link Reader} for the specified
	 * {@link IDocumentationResponse} or <code>null</code>.
	 * 
	 * @param response
	 *            input response, probably <code>null</code>
	 * @return
	 */
	public static Reader getReader(IDocumentationResponse response) {
		if (response != null) {
			try {
				return response.getReader();
			} catch (IOException e) {
				if (DLTKCore.DEBUG)
					e.printStackTrace();
			}
		}
		return null;
	}

}
