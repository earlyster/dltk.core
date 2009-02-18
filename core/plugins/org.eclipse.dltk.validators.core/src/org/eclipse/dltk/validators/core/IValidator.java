/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.core;

import org.eclipse.dltk.core.IScriptProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Contains validator properties.
 * 
 * @author Haiodo
 */
public interface IValidator {

	String getID();

	String getName();

	boolean isWorkingCopy();

	IValidator getWorkingCopy();

	boolean isAutomatic();

	boolean isAutomatic(IScriptProject project);

	IValidatorType getValidatorType();

	boolean isValidatorValid(IScriptProject project);

	/**
	 * Returns the {@link IValidatorWorker} to operate on the specified
	 * <code>environment</code> and send output to the specified
	 * <code>output</code>
	 * 
	 * @param project
	 * @return
	 * @see IValidatorType#supports(Class)
	 */
	Object getValidator(IScriptProject project, Class validatorType);

	void loadFrom(Element element);

	void storeTo(Document doc, Element element);

	void setName(String name);

	void setAutomatic(boolean active);

}
