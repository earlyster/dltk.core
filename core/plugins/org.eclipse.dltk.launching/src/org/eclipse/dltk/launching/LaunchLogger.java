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
package org.eclipse.dltk.launching;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.eclipse.dltk.core.environment.IExecutionLogger;

import com.ibm.icu.text.SimpleDateFormat;

class LaunchLogger implements IExecutionLogger {

	private final String fileName;

	public LaunchLogger() {
		fileName = new SimpleDateFormat("yyyy-MM-dd-HHmm").format(new Date())
				+ ".log";
	}

	public void logLine(String line) {
		final File file = new File(System.getProperty("user.home"), fileName);
		try {
			final FileWriter writer = new FileWriter(file, true);
			try {
				writer.write(line);
				writer.write("\n");
			} finally {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		} catch (IOException e) {
			// ignore?
		}
	}
}
