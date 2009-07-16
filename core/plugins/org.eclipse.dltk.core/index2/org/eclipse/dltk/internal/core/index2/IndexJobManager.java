/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.internal.core.index2;

import org.eclipse.dltk.internal.core.search.processing.IJob;
import org.eclipse.dltk.internal.core.search.processing.JobManager;

/**
 * Job manager used while indexing
 * 
 * @author michael
 * 
 */
public class IndexJobManager extends JobManager {

	public String processName() {
		return "DLTK Indexing [2]";
	}

	public void requestIfNotWaiting(IJob job) {
		if (!isJobWaiting(job)) {
			request(job);
		}
	}
}
