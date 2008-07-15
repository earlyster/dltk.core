/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.core.tests;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.validators.core.AbstractValidator;
import org.eclipse.dltk.validators.core.ISourceModuleValidator;
import org.eclipse.dltk.validators.core.IValidatorOutput;
import org.eclipse.dltk.validators.core.IValidatorType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SimpleValidator extends AbstractValidator implements
		ISourceModuleValidator {
	private String value = "";
	private boolean valid = true;

	public boolean isValid() {
		return valid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		fireChanged();
	}

	protected SimpleValidator(String id, IValidatorType type) {
		super(id, "", type);
	}

	protected void load(Element element) {
		super.load(element);
		this.value = element.getAttribute("simple_value");
		this.valid = loadBoolean(element, "simple_valid");
	}

	public void storeTo(Document doc, Element element) {
		super.storeTo(doc, element);
		element.setAttribute("simple_value", this.value);
		element.setAttribute("simple_valid", Boolean.toString(this.valid));
	}

	public IStatus validate(ISourceModule[] module, IValidatorOutput output,
			IProgressMonitor monitor) {
		return Status.OK_STATUS;
	}

	public void setValid(boolean b) {
		this.valid = b;
		fireChanged();
	}

	public boolean isValidatorValid(IScriptProject project) {
		return this.valid;
	}

	public void clean(ISourceModule[] module) {

	}

	public void clean(IResource[] resource) {
	}

	public Object getValidator(IScriptProject project, Class validatorType) {
		if (ISourceModuleValidator.class.equals(validatorType)) {
			return this;
		}
		return null;
	}

}
