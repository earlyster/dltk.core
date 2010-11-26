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
package org.eclipse.dltk.compiler;

public enum SourceElementRequestorKind {
	STRUCTURE {
		@Override
		public boolean matches(ISourceElementRequestor requestor) {
			return getMode(requestor) == ISourceElementRequestorExtension.MODE_STRUCTURE;
		}
	},
	INDEXER {
		@Override
		public boolean matches(ISourceElementRequestor requestor) {
			return getMode(requestor) == ISourceElementRequestorExtension.MODE_INDEX;
		}
	};

	public abstract boolean matches(ISourceElementRequestor requestor);

	private static int getMode(ISourceElementRequestor requestor) {
		if (requestor instanceof ISourceElementRequestorExtension) {
			return ((ISourceElementRequestorExtension) requestor).getMode();
		} else {
			return ISourceElementRequestorExtension.MODE_UNKNOWN;
		}
	}
}
