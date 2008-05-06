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
package org.eclipse.dltk.core.internal.rse.perfomance;

import org.eclipse.core.runtime.Platform;

public final class RSEPerfomanceStatistics {
	public static final boolean PERFOMANCE_TRACING = Boolean
			.valueOf(
					Platform
							.getDebugOption("org.eclipse.dltk.rse.core/perfomance")).booleanValue(); //$NON-NLS-1$

	private final static String[] statisticNames = { "Total Bytes received", // 0
			"Files accessed", // 1
			"Executions count", // 2
			"Average execution time", // 3
			"Deployments Created", // 4
			"Environment receive time", // 5
			"Environment receive count", // 6
			"Has project invocations", // 7
			"Has project execution time", // 8
			"Execution time" // 9
	};
	public static final int TOTAL_BYTES_RECEIVED = 0;
	public static final int FILES_ACCESSED = 1;
	public static final int EXECUTION_COUNT = 2;
	public static final int AVERAGE_EXECUTION_TIME = 3;
	public static final int DEPLOYMENTS_CREATED = 4;
	public static final int ENVIRONMENT_RECEIVE_TIME = 5;
	public static final int ENVIRONMENT_RECEIVE_COUNT = 6;
	public static final int HAS_PROJECT_EXECUTIONS = 7;
	public static final int HAS_POJECT_EXECUTIONS_TIME = 8;
	public static final int EXECUTION_TIME = 9;

	public final static int STATISTICS_COUNT = 10;
	// Statistics
	private static long statistics[] = null;
	static {
		statistics = new long[STATISTICS_COUNT];
		for (int i = 0; i < STATISTICS_COUNT; i++) {
			statistics[i] = 0;
		}
	}

	public static void inc(int pos) {
		statistics[pos]++;
	}

	public static long get(int pos) {
		return statistics[pos];
	}

	public static void inc(int pos, long l) {
		statistics[pos] += l;
	}

	public static String getTitle(int i) {
		return statisticNames[i];
	}

}
