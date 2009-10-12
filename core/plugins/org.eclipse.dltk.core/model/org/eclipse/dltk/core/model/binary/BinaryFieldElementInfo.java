/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.model.binary;


class BinaryFieldElementInfo extends BinaryMemberInfo {
	private String enumInitializerValue;
	private boolean isEnumValue;

	public boolean isEnumValue() {
		return isEnumValue;
	}

	public void setEnumValue(boolean isEnumValue) {
		this.isEnumValue = isEnumValue;
	}

	public String getEnumInitializerValue() {
		return enumInitializerValue;
	}

	public void setEnumInitializerValue(String enumInitializerValue) {
		this.enumInitializerValue = enumInitializerValue;
	}
}
