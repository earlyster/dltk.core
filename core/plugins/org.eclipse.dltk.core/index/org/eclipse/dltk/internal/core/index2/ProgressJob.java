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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.internal.core.search.processing.JobManager;
import org.eclipse.dltk.internal.core.util.Messages;

class ProgressJob extends Job {

	private JobManager jobManager;
	private IProgressMonitor monitor;

	public ProgressJob(JobManager jobManager) {
		super(Messages.manager_indexingInProgress);
		this.jobManager = jobManager;
	}

	protected IStatus run(IProgressMonitor monitor) {
		this.monitor = monitor;
		monitor.beginTask(Messages.manager_indexingTask,
				IProgressMonitor.UNKNOWN);

		while (!monitor.isCanceled() && (jobManager.awaitingJobsCount()) > 0) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		monitor.done();
		return Status.OK_STATUS;
	}

	public void subTask(String message) {
		if (monitor != null) {
			monitor.subTask(message);
		}
	}
}