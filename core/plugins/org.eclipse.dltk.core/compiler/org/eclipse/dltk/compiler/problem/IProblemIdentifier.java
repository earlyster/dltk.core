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

/**
 * The problem identifier. Use <code>enum</code> to implement it and
 * {@link org.eclipse.dltk.utils.EnumNLS} to load localized messages from
 * <code>.properties</code> files.
 */
public interface IProblemIdentifier {

	/**
	 * Returns the unique identifier for persistence purposes
	 * 
	 * @return
	 */
	String getId();

	/**
	 * Formats the message for this problem using the specified arguments.
	 * 
	 * @param args
	 * @return
	 */
	String formatMessage(Object... args);
}
