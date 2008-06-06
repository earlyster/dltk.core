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
package org.eclipse.dltk.ui.templates;

/**
 * Implementation of the {@link IScriptTemplateIndenter} which passes original
 * lines without modifications.
 */
public class NopScriptTemplateIndenter implements IScriptTemplateIndenter {

	public void indentLine(StringBuffer sb, String indent, String line) {
		sb.append(indent);
		sb.append(line);
	}
}
