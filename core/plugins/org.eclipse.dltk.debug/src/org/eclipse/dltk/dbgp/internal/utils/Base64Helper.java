/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.dbgp.internal.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.dltk.dbgp.exceptions.DbgpIOException;

public class Base64Helper {

	public static String encodeString(String s) {
		try {
			byte[] encode = Base64.encode(s.getBytes("UTF-8"));
			return new String(encode).replaceAll("\n", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decodeString(String base64) throws DbgpIOException {
		try {
			return new String(Base64.decode(base64.getBytes()), "UTF-8"); //$NON-NLS-1$
		} catch (IOException e) {
			throw new DbgpIOException(e);
		}
	}

	public static String encodeBytes(byte[] bytes) {
		return new String(Base64.encode(bytes));
	}

	public static byte[] decodeBytes(String base64) {
		return Base64.decode(base64.getBytes());
	}
}
