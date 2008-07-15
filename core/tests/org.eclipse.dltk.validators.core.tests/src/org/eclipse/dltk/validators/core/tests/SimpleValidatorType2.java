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
package org.eclipse.dltk.validators.core.tests;

public class SimpleValidatorType2 extends SimpleValidatorType {

	public static final String NATURE = "SimpleNature";

	public String getID() {
		return super.getID() + "2";
	}

	public String getNature() {
		return NATURE;
	}

}
