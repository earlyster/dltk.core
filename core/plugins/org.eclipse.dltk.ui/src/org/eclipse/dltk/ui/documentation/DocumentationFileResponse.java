/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @since 2.0
 */
public class DocumentationFileResponse extends AbstractDocumentationResponse {

	private final File file;
	private final String encoding;

	/**
	 * @param file
	 * @param encoding
	 */
	public DocumentationFileResponse(Object object, File file) {
		this(object, file, null);
	}

	/**
	 * @param file
	 * @param encoding
	 */
	public DocumentationFileResponse(Object object, File file, String encoding) {
		super(object);
		this.file = file;
		this.encoding = encoding;
	}

	public Reader getReader() throws FileNotFoundException,
			UnsupportedEncodingException {
		final InputStream stream = new FileInputStream(file);
		return encoding != null ? new InputStreamReader(stream, encoding)
				: new InputStreamReader(stream);
	}

	@Override
	public URL getURL() throws MalformedURLException {
		return file.toURI().toURL();
	}

}
