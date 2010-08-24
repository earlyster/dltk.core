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
package org.eclipse.dltk.ui.documentation;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @since 3.0
 */
public class TextDocumentationResponse extends AbstractDocumentationResponse {

	private final String content;
	private final String title;

	public TextDocumentationResponse(Object object, String content) {
		this(object, content, null);
	}

	public TextDocumentationResponse(Object object, String content, String title) {
		super(object);
		this.content = content;
		this.title = title;
	}

	public Reader getReader() throws IOException {
		return new StringReader(content);
	}

	@Override
	public String getTitle() {
		return title;
	}

}
