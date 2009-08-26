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
package org.eclipse.dltk.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @since 2.0
 */
public class DLTKLogging {

	private static ServiceTracker debugTracker = null;
	private static boolean initialized = false;

	private static synchronized DebugOptions getDebugOptions() {
		if (debugTracker == null) {
			final BundleContext context = getBundleContext();
			if (context == null)
				return null;
			debugTracker = new ServiceTracker(context, DebugOptions.class
					.getName(), null);
			debugTracker.open();
		}
		final DebugOptions debugOptions = (DebugOptions) debugTracker
				.getService();
		if (debugOptions != null && !initialized) {
			final String loggingOptions = new InstanceScope().getNode(
					DLTKCore.PLUGIN_ID).get(DLTKCore.LOGGING_OPTIONS, null);
			if (loggingOptions != null) {
				for (String option : TextUtils.split(loggingOptions,
						DLTKCore.LOGGING_OPTION_SEPARATOR)) {
					debugOptions.setOption(option, Boolean.TRUE.toString());
				}
			}
			initialized = true;
		}
		return debugOptions;
	}

	public static boolean isEnabled(String option) {
		final DebugOptions debugOptions = getDebugOptions();
		return debugOptions != null
				&& debugOptions.getBooleanOption(option, false);
	}

	public static void setEnabled(String option, boolean value) {
		final DebugOptions debugOptions = getDebugOptions();
		if (debugOptions != null) {
			debugOptions.setOption(option, Boolean.toString(value));
		}
	}

	private static BundleContext getBundleContext() {
		final Plugin plugin = DLTKCore.getDefault();
		if (plugin != null) {
			final Bundle bundle = plugin.getBundle();
			if (bundle != null) {
				return bundle.getBundleContext();
			}
		}
		return null;
	}

	/**
	 * @param options
	 * @return
	 */
	public static Map<String, Boolean> getState(Collection<String> options) {
		final DebugOptions debugOptions = getDebugOptions();
		if (debugOptions != null) {
			final Map<String, Boolean> result = new HashMap<String, Boolean>();
			for (String option : options) {
				boolean value = debugOptions.getBooleanOption(option, false);
				result.put(option, Boolean.valueOf(value));
			}
			return result;
		} else {
			return Collections.emptyMap();
		}
	}

	public static void setState(Map<String, Boolean> state) {
		final DebugOptions debugOptions = getDebugOptions();
		if (debugOptions != null) {
			for (Map.Entry<String, Boolean> entry : state.entrySet()) {
				debugOptions.setOption(entry.getKey(), entry.getValue()
						.toString());
			}
		}
	}

}
