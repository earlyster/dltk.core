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
package org.eclipse.dltk.formatter;

import org.eclipse.jface.text.IRegion;

public interface IFormatterWriter {

	void ensureLineStarted(IFormatterContext context) throws Exception;

	void write(IFormatterContext context, int startOffset, int endOffset)
			throws Exception;

	void excludeRegion(IRegion region);

	void addNewLineCallback(IFormatterCallback callback);

}
