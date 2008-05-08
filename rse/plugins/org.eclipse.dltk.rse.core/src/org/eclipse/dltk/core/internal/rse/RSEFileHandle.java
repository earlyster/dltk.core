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
package org.eclipse.dltk.core.internal.rse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.internal.environment.EFSFileHandle;
import org.eclipse.dltk.core.internal.rse.perfomance.RSEPerfomanceStatistics;
import org.eclipse.rse.internal.efs.RSEFileSystem;

public class RSEFileHandle extends EFSFileHandle {

	public RSEFileHandle(IEnvironment env, URI locationURI) {
		super(env, RSEFileSystem.getInstance().getStore(locationURI));
	}

	public boolean exists() {
		// TODO Auto-generated method stub
		return super.exists();
	}

	public InputStream openInputStream(IProgressMonitor monitor)
			throws IOException {
		if (RSEPerfomanceStatistics.PERFOMANCE_TRACING) {

			final InputStream stream = super.openInputStream(monitor);
			return new InputStream() {
				public int read() throws IOException {
					int read = stream.read();
					if (read != -1) {
						RSEPerfomanceStatistics
								.inc(RSEPerfomanceStatistics.TOTAL_BYTES_RECEIVED);
					}
					return read;
				}
			};
		}
		return super.openInputStream(monitor);
	}
}
