/*******************************************************************************
 * Copyright (c) 2011 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.compiler.problem;

import org.eclipse.core.resources.IMarker;

public enum ProblemSeverity {
	DEFAULT(-1), IGNORE(-1), INFO(IMarker.SEVERITY_INFO), WARNING(
			IMarker.SEVERITY_WARNING), ERROR(IMarker.SEVERITY_ERROR);

	public final int value;

	private ProblemSeverity(int value) {
		this.value = value;
	}
}
