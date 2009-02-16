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
package org.eclipse.dltk.ui.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.dltk.core.environment.EnvironmentChangedListener;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IEnvironmentChangedListener;
import org.eclipse.swt.widgets.Display;
import org.omg.CORBA.Environment;

/**
 * This class collects {@link IEnvironment}s from {@link EnvironmentManager} and
 * fires notification on UI thread if environments are changed.
 * 
 * At the moment if works only for initial retrieve of environments. It requires
 * some time to initialize RSE, so to prevent delays and possible deadlocks we
 * should not wait until RSE initialization is completed, this class is used to
 * fire the event after RSE initialization is completed.
 */
public class EnvironmentContainer {

	private boolean initialized = false;
	private final Map environments = new HashMap();

	private IEnvironmentChangedListener listener = null;

	/**
	 * Initialize the environments maintained by this object. The subsequent
	 * calls of this method are ignored.
	 */
	public void initialize() {
		if (!initialized) {
			initialized = true;
			synchronized (environments) {
				initEnvironments();
				installChangeListener();
			}
		}
	}

	private void initEnvironments() {
		environments.clear();
		final IEnvironment[] envs = EnvironmentManager.getEnvironments();
		for (int i = 0; i < envs.length; ++i) {
			final IEnvironment environment = envs[i];
			environments.put(environment.getId(), environment);
		}
	}

	private void installChangeListener() {
		if (listener == null) {
			listener = new EnvironmentChangedListener() {

				public void environmentsModified() {
					synchronized (environments) {
						initEnvironments();
					}
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							fireChangeNotifications();
						}
					});
				}

			};
			EnvironmentManager.addEnvironmentChangedListener(listener);
		}
	}

	private void uninstallChangeListener() {
		if (listener != null) {
			EnvironmentManager.removeEnvironmentChangedListener(listener);
			listener = null;
		}
	}

	/**
	 * Returns the list of {@link IEnvironment}s
	 * 
	 * @return
	 */
	public List getEnvironments() {
		return new ArrayList(environments.values());
	}

	/**
	 * Returns the identifiers of {@link Environment}s managed by this object
	 * 
	 * @return
	 */
	public String[] getEnvironmentIds() {
		synchronized (environments) {
			return (String[]) environments.keySet().toArray(
					new String[environments.size()]);
		}
	}

	/**
	 * @param environmentId
	 * @return
	 */
	public IEnvironment get(String environmentId) {
		return (IEnvironment) environments.get(environmentId);
	}

	/**
	 * Returns the name of the environment with the specified id
	 * 
	 * @param environmentId
	 * @return
	 */
	public String getName(String environmentId) {
		final IEnvironment environment = (IEnvironment) environments
				.get(environmentId);
		if (environment != null) {
			return environment.getName();
		} else {
			return "(" + environmentId + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Release resources occupied by this object
	 */
	public void dispose() {
		uninstallChangeListener();
		changeListeners.clear();
		environments.clear();
		initialized = false;
	}

	private final ListenerList changeListeners = new ListenerList();

	/**
	 * Registers the specified change listener to be called when available
	 * environments are changed. Specified event handler is called on the UI
	 * thread.
	 * 
	 * @param runnable
	 */
	public void addChangeListener(Runnable runnable) {
		changeListeners.add(runnable);
	}

	protected void fireChangeNotifications() {
		Object[] listeners = changeListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((Runnable) listeners[i]).run();
		}
	}

}
