/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.eclipse.dltk.core;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * This class is designed to show progress messages using Eclipse Jobs subsystem
 * 
 * @author asobolev
 * @since 2.0
 */
public class ProgressMonitoringJob extends Job implements IProgressMonitor {
	private static class WorkNode {
		int type;
		String message;
		double worked;

		public WorkNode(int type, String message, double worked) {
			super();
			this.type = type;
			this.message = message;
			this.worked = worked;
		}
	}

	private static final int BEGIN_TASK = 0;

	private static final int DONE = 1;

	private static final int INTERNAL_WORKED = 2;

	private static final int SET_CANCELED = 3;

	private static final int SET_TASK_NAME = 4;

	private static final int SUB_TASK = 5;

	private static final int WORKED = 6;

	private List<WorkNode> queue = new LinkedList<WorkNode>();

	private final Object lock = new Object();
	private boolean canceled = false;

	public ProgressMonitoringJob(String message, int totalWork) {
		super(message);
		setPriority(Job.LONG);
		schedule();
		beginTask(message, totalWork);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		for (;;) {
			if (monitor.isCanceled()) {
				synchronized (lock) {
					canceled = true;
					return Status.CANCEL_STATUS;
				}
			}
			synchronized (lock) {
				if (queue.isEmpty()) {
					try {
						lock.wait(100);
					} catch (InterruptedException e) {
						DLTKCore.error(e);
					}
					continue;
				}

				WorkNode node = queue.remove(0);
				if (node != null) {
					switch (node.type) {
					case BEGIN_TASK:
						monitor.beginTask(node.message, (int) node.worked);
						break;
					case DONE:
						monitor.done();
						return Status.OK_STATUS;
					case INTERNAL_WORKED:
						monitor.internalWorked(node.worked);
						break;
					case SET_CANCELED:
						monitor.setCanceled(node.worked == 1 ? true : false);
						break;
					case SET_TASK_NAME:
						monitor.setTaskName(node.message);
						break;
					case SUB_TASK:
						monitor.subTask(node.message);
						break;
					case WORKED:
						monitor.worked((int) node.worked);
						break;
					}

				}
			}
		}
	}

	public void beginTask(String name, int totalWork) {
		synchronized (lock) {
			WorkNode node = new WorkNode(BEGIN_TASK, name, totalWork);
			addToQueue(node);
		}
	}

	private void addToQueue(WorkNode node) {
		synchronized (lock) {
			queue.add(node);
			lock.notifyAll();
		}
	}

	public void done() {
		addToQueue(new WorkNode(DONE, null, 0));
	}

	public void internalWorked(double work) {
		addToQueue(new WorkNode(INTERNAL_WORKED, null, work));

	}

	public boolean isCanceled() {
		synchronized (lock) {
			return canceled;
		}
	}

	public void setCanceled(boolean value) {
		synchronized (lock) {
			this.canceled = value;
		}
		addToQueue(new WorkNode(SET_CANCELED, null, value ? 1 : 0));
	}

	public void setTaskName(String name) {
		addToQueue(new WorkNode(SET_TASK_NAME, name, 0));

	}

	public void subTask(String name) {
		addToQueue(new WorkNode(SUB_TASK, name, 0));
	}

	public void worked(int work) {
		addToQueue(new WorkNode(WORKED, null, work));
	}
}