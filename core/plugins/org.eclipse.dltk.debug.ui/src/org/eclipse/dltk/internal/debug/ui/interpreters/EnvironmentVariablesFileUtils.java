/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.eclipse.dltk.internal.debug.ui.interpreters;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.launching.EnvironmentVariable;

public class EnvironmentVariablesFileUtils {

	public static void save(EnvironmentVariable[] variables, String file)
			throws Exception {
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new BufferedOutputStream(
					new FileOutputStream(new File(file)), 4096));
			for (int i = 0; i < variables.length; i++) {
				writer.write(variables[i].getName() + " "
						+ variables[i].getValue() + "\n");
			}
		} catch (FileNotFoundException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			throw new Exception(e.getMessage());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static EnvironmentVariable[] load(String file) throws Exception {
		BufferedReader reader = null;
		List results = new ArrayList();
		try {
			reader = new BufferedReader(
					new InputStreamReader(new BufferedInputStream(
							new FileInputStream(new File(file)))));
			int ln = 1;
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				// line = line.trim();
				if (line.length() > 0) {
					int pos = line.indexOf(' ');
					if (pos == -1) {
						throw new Exception("Incorrect file format at line:"
								+ ln);
					}
					String varName = line.substring(0, pos).trim();
					if (varName.length() == 0) {
						throw new Exception("Incorrect file format at line:"
								+ ln);
					}
					String value = line.substring(pos + 1).trim();
					results.add(new EnvironmentVariable(varName, value));
				}// else skip line
				ln++;
			}
			if (results.size() > 0) {
				return (EnvironmentVariable[]) results
						.toArray(new EnvironmentVariable[results.size()]);
			}
		} catch (FileNotFoundException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			throw new Exception("IOError:" + e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
}
