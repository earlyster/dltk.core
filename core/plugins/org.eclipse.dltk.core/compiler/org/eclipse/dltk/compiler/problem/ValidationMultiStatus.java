/*******************************************************************************
 * Copyright (c) 2012 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.compiler.problem;

import java.util.Collection;
import java.util.List;

public class ValidationMultiStatus implements IValidationStatus {

	private final ValidationStatus[] children;

	public ValidationMultiStatus(ValidationStatus[] children) {
		this.children = children;
	}

	public ValidationStatus[] getChildren() {
		return children;
	}

	public static IValidationStatus of(Collection<ValidationStatus> children) {
		if (children == null || children.isEmpty()) {
			return null;
		} else if (children.size() == 1) {
			if (children instanceof List<?>) {
				return ((List<ValidationStatus>) children).get(0);
			} else {
				return children.toArray(new ValidationStatus[1])[0];
			}
		} else {
			return new ValidationMultiStatus(
					children.toArray(new ValidationStatus[children.size()]));
		}
	}

}
