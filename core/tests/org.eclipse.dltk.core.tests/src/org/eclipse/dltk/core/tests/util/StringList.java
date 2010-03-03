/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.tests.util;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class StringList extends ArrayList<String> {

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String line : this) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

}
