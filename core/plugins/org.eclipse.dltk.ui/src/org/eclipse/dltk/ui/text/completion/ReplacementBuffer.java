/*******************************************************************************
 * Copyright (c) 2011 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.text.completion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

/**
 * @since 4.0
 */
public class ReplacementBuffer {
	private final List<IRegion> arguments = new ArrayList<IRegion>();

	public void addArgument(int offset, int length) {
		arguments.add(new Region(offset, length));
	}

	private final StringBuilder buffer = new StringBuilder();

	public void append(String text) {
		buffer.append(text);
	}

	public int length() {
		return buffer.length();
	}

	@Override
	public String toString() {
		return buffer.toString();
	}

	public boolean hasArguments() {
		return !arguments.isEmpty();
	}

	public List<IRegion> getArguments() {
		return arguments;
	}

}
