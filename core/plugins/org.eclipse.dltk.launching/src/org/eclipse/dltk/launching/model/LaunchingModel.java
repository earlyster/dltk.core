/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.launching.model;

import java.io.File;
import java.io.IOException;
import java.util.ListIterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.Predicate;
import org.eclipse.dltk.internal.launching.DLTKLaunchingPlugin;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

public class LaunchingModel {

	private static LaunchingModel instance = null;

	public static synchronized LaunchingModel getInstance() {
		if (instance == null) {
			instance = new LaunchingModel();
		}
		return instance;
	}

	private LaunchingModel() {

	}

	private class Model {
		final Resource resource;

		public Model(Resource resource) {
			this.resource = resource;
		}

		public InterpreterInfo find(IInterpreterInstall interpreter) {
			final String environmentId = interpreter.getEnvironmentId();
			final String location = interpreter.getInstallLocation().getPath()
					.toString();
			for (EObject object : resource.getContents()) {
				if (object instanceof InterpreterInfo) {
					final InterpreterInfo info = (InterpreterInfo) object;
					if (environmentId.equals(info.getEnvironment())
							&& location.equals(info.getLocation())) {
						return info;
					}
				}
			}
			return null;
		}

		/**
		 * @param interpreter
		 * @return
		 */
		public InterpreterInfo create(IInterpreterInstall interpreter) {
			InterpreterInfo info = find(interpreter);
			if (info == null) {
				info = LaunchingModelFactory.eINSTANCE.createInterpreterInfo();
				info.setEnvironment(interpreter.getEnvironmentId());
				info.setLocation(interpreter.getInstallLocation().getPath()
						.toString());
				resource.getContents().add(info);
			}
			return info;
		}

		public void save() {
			try {
				resource.save(null);
				synchronized (modelLock) {
					model = null;
				}
			} catch (IOException e) {
				DLTKLaunchingPlugin.log(e);
			}
		}
	}

	private static class ModelApapter extends EContentAdapter {

		@Override
		public void notifyChanged(Notification notification) {
			if (!notification.isTouch())
				throw new UnsupportedOperationException();
			super.notifyChanged(notification);
		}

	}

	private Model model = null;
	private final Object modelLock = new Object();

	private Model getModel() {
		synchronized (modelLock) {
			if (model == null) {
				model = loadModel();
				model.resource.eAdapters().add(new ModelApapter());
			}
			return model;
		}
	}

	private static final String MODEL_FILENAME = "model.xmi"; //$NON-NLS-1$

	private static URI getModelLocation() {
		final IPath path = DLTKLaunchingPlugin.getDefault().getStateLocation()
				.append(MODEL_FILENAME);
		return URI.createFileURI(path.toOSString());
	}

	private static boolean canLoad(URI location) {
		if (location.isFile()) {
			return new File(location.toFileString()).exists();
		} else {
			return true;
		}
	}

	private Model loadModel() {
		final URI location = getModelLocation();
		final Resource resource = new XMIResourceImpl(location);
		try {
			if (canLoad(location)) {
				resource.load(null);
			}
		} catch (IOException e) {
			DLTKLaunchingPlugin.log(e);
		}
		return new Model(resource);
	}

	/**
	 * Returns the first content object of the specified type or
	 * <code>null</code>.
	 * 
	 * @param interpreter
	 * @param clazz
	 * @return
	 */
	public EObject find(IInterpreterInstall interpreter,
			Predicate<EObject> predicate) {
		InterpreterInfo info = getModel().find(interpreter);
		if (info != null) {
			for (EObject object : info.getContents()) {
				if (predicate.evaluate(object)) {
					return object;
				}
			}
		}
		return null;
	}

	/**
	 * Replace the first content object of the specified type with the new
	 * value.
	 * 
	 * @param interpreter
	 * @param clazz
	 * @param value
	 *            new value or <code>null</code>
	 * @return
	 */
	public EObject save(IInterpreterInstall interpreter,
			Predicate<EObject> predicate, EObject value) {
		if (value != null) {
			Assert.isLegal(predicate.evaluate(value));
		}
		// TODO synchronization
		final Model model = loadModel();
		final InterpreterInfo info = model.create(interpreter);
		for (ListIterator<EObject> i = info.getContents().listIterator(); i
				.hasNext();) {
			EObject object = i.next();
			if (predicate.evaluate(object)) {
				if (value != null) {
					i.set(value);
				} else {
					i.remove();
				}
				model.save();
				return object;
			}
		}
		if (value != null) {
			info.getContents().add(value);
			model.save();
		}
		return null;
	}
}
