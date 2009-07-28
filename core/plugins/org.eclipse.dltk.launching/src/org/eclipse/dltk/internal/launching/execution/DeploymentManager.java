/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Yuri Strot)
 *******************************************************************************/
package org.eclipse.dltk.internal.launching.execution;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.dltk.core.environment.IDeployment;

public class DeploymentManager implements ILaunchesListener2 {
	private Map launchToDeployment = new HashMap();
	private Set activeDeployments = new HashSet();

	private static DeploymentManager sInstance = null;

	public static synchronized DeploymentManager getInstance() {
		if (sInstance == null) {
			sInstance = new DeploymentManager();
		}
		return sInstance;
	}

	public void startup() {
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
	}

	public void shutdown() {
		undeployAll(activeDeployments);
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
	}

	public void launchesAdded(ILaunch[] launches) {
	}

	public void launchesChanged(ILaunch[] launches) {
	}

	public synchronized void launchesRemoved(ILaunch[] launches) {
		for (int i = 0; i < launches.length; i++) {
			if (launchToDeployment.containsKey(launches[i])) {
				Set deployments = (Set) launchToDeployment.get(launches[i]);
				undeployAll(deployments);
				launchToDeployment.remove(launches[i]);
			}
		}
	}

	private synchronized void undeployAll(Collection deployments) {
		Set copy = new HashSet(deployments);
		for (Iterator iterator = copy.iterator(); iterator.hasNext();) {
			IDeployment deployment = (IDeployment) iterator.next();
			deployment.dispose();
			activeDeployments.remove(deployment);
		}
	}

	public synchronized void addDeployment(IDeployment deployment) {
		activeDeployments.add(deployment);
	}

	public synchronized void addDeployment(ILaunch launch,
			IDeployment deployment) {
		activeDeployments.add(deployment);
		if (launchToDeployment.containsKey(launch)) {
			((Set) launchToDeployment.get(launch)).add(deployment);
		} else {
			Set elements = new HashSet();
			elements.add(deployment);
			launchToDeployment.put(launch, elements);
		}
	}

	public synchronized void removeDeployment(IDeployment deployment) {
		activeDeployments.remove(deployment);
	}

	public void launchesTerminated(ILaunch[] launches) {
		launchesRemoved(launches);
	}
}
