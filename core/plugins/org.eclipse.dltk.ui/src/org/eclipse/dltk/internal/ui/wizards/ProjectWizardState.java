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
package org.eclipse.dltk.internal.ui.wizards;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.ui.wizards.IProjectWizardInitializer.IProjectWizardState;

public class ProjectWizardState implements IProjectWizardState {

	private final String nature;
	private IEnvironment environment;
	private String externalLocation;
	private String projectName;
	private String mode;
	private final Map<String, String> tooltips = new HashMap<String, String>();

	public ProjectWizardState(String nature) {
		this.nature = nature;
	}

	public String getScriptNature() {
		return nature;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String name) {
		this.projectName = name;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getToolTipText(String mode) {
		return tooltips.get(mode);
	}

	public void setToolTipText(String mode, String tooltip) {
		tooltips.put(mode, tooltip);
	}

	public void setEnvironment(IEnvironment environment) {
		this.environment = environment;
	}

	public IEnvironment getEnvironment() {
		return environment;
	}

	public String getExternalLocation() {
		return externalLocation;
	}

	public void setExternalLocation(String externalLocation) {
		this.externalLocation = externalLocation;
	}

}
