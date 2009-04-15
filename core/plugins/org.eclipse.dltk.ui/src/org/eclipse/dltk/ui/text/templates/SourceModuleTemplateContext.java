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
package org.eclipse.dltk.ui.text.templates;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jface.text.templates.TemplateContextType;

public class SourceModuleTemplateContext extends FileTemplateContext {

	/**
	 * @param contextType
	 * @param lineDelimiter
	 */
	public SourceModuleTemplateContext(TemplateContextType contextType,
			String lineDelimiter) {
		super(contextType, lineDelimiter);
	}

	public void setSourceModuleVariables(ISourceModule module) {
		// TODO add interpreter variables
		final IResource resource = module.getResource();
		if (resource instanceof IFile) {
			setResourceVariables((IFile) resource);
		}
	}

}
