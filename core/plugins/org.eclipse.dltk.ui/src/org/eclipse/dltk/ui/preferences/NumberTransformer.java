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
package org.eclipse.dltk.ui.preferences;

/**
 * Abstract class implementing {@link ITextConverter} to be used for integer
 * numbers.
 */
public abstract class NumberTransformer implements ITextConverter {

	public String convertPreference(String value) {
		try {
			return String.valueOf(convertPreference(Integer.parseInt(value)));
		} catch (NumberFormatException e) {
			return value;
		}
	}

	/**
	 * Convert value from the internal format to the format suitable to display
	 * in the text box
	 * 
	 * @param value
	 * @return
	 */
	protected abstract int convertPreference(int value);

	public String convertInput(String input) {
		try {
			return String.valueOf(convertInput(Integer.parseInt(input)));
		} catch (NumberFormatException e) {
			return input;
		}
	}

	/**
	 * Convert value entered into the text box to the internal format.
	 * 
	 * @param input
	 * @return
	 */
	protected abstract int convertInput(int input);

}
