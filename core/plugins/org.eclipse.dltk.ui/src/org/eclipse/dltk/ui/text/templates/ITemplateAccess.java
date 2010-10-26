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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;

public interface ITemplateAccess {

	public interface ITemplateAccessInternal {
		void dispose();

		String getPreferenceQualifier();

		String getPreferenceKey();
	}

	/**
	 * Returns the template context type registry for the code generation
	 * templates.
	 * 
	 * @return the template context type registry for the code generation
	 *         templates
	 */
	ContextTypeRegistry getContextTypeRegistry();

	/**
	 * Returns the template store for the code generation templates.
	 * 
	 * @return the template store for the code generation templates
	 */
	TemplateStore getTemplateStore();

	/**
	 * Returns the preference store used to create the template store returned
	 * by {@link #getTemplateStore()}.
	 * 
	 * @return the preference store
	 */
	IPreferenceStore getTemplatePreferenceStore();

}
