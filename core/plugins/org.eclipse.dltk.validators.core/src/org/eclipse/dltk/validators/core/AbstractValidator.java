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
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractValidator implements IValidator, Cloneable {

	private static final String ATTR_ACTIVE = "active"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$

	private final String id;
	private String name;
	private final IValidatorType type;
	private boolean automatic = true;
	private boolean workingCopy;

	protected AbstractValidator(String id, String name, IValidatorType type) {
		this.id = id;
		this.type = type;
		this.name = name;
	}

	public String getID() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public final void loadFrom(Element element) {
		final boolean savedWorkingCopy = workingCopy;
		workingCopy = true;
		try {
			load(element);
		} finally {
			workingCopy = savedWorkingCopy;
		}
	}

	protected void load(Element element) {
		if (!type.isBuiltin()) {
			this.name = element.getAttribute(ATTR_NAME);
		}
		this.automatic = loadBoolean(element, ATTR_ACTIVE);
	}

	protected boolean loadBoolean(Element element, final String attribute) {
		return Boolean.valueOf(element.getAttribute(attribute)).booleanValue();
	}

	public void storeTo(Document doc, Element element) {
		element.setAttribute(ATTR_NAME, getName());
		element.setAttribute(ATTR_ACTIVE, Boolean.toString(isAutomatic()));
	}

	public IValidatorType getValidatorType() {
		return this.type;
	}

	public void setName(String name) {
		this.name = name;
		fireChanged();
	}

	public boolean isAutomatic() {
		return automatic;
	}

	public boolean isAutomatic(IScriptProject project) {
		return isAutomatic();
	}

	public void setAutomatic(boolean value) {
		this.automatic = value;
		fireChanged();
	}

	protected void fireChanged() {
		if (!workingCopy) {
			ValidatorRuntime.fireValidatorChanged(this);
		}
	}

	public boolean isWorkingCopy() {
		return workingCopy;
	}

	public IValidator getWorkingCopy() {
		if (isWorkingCopy()) {
			return this;
		}
		try {
			final AbstractValidator copy = (AbstractValidator) clone();
			copy.workingCopy = true;
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param project
	 * @return
	 */
	protected IEnvironment getEnvrironment(IScriptProject project) {
		return EnvironmentManager.getEnvironment(project);
	}
}
