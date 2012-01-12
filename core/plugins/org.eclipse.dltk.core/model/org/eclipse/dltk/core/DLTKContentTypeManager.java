/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.core.runtime.content.IContentTypeManager.ContentTypeChangeEvent;
import org.eclipse.core.runtime.content.IContentTypeManager.IContentTypeChangeListener;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.core.DLTKAssociationManager;
import org.eclipse.dltk.internal.core.NopAssociationManager;
import org.eclipse.dltk.internal.core.ScriptFileConfiguratorManager;

public class DLTKContentTypeManager {

	public static final QualifiedName DLTK_VALID = new QualifiedName(
			DLTKCore.PLUGIN_ID, "valid"); //$NON-NLS-1$

	/**
	 * Persisted value of the {@link #DLTK_VALID} property
	 */
	private static final String TRUE_VALUE = "true"; //$NON-NLS-1$

	/**
	 * Persisted value of the {@link #DLTK_VALID} property
	 */
	private static final String FALSE_VALUE = "false"; //$NON-NLS-1$

	private static final boolean DEBUG = false;
	private static final boolean DEBUG_CONTENT = false;

	private static void log(String message, Object input) {
		if (input != null) {
			String className = input.getClass().getName();
			int pos = className.lastIndexOf('.');
			if (pos > 0) {
				className = className.substring(pos + 1);
			}
			System.out.println(message + ' ' + input + ':' + className);
		}
	}

	public static boolean isValidFileNameForContentType(
			IDLTKLanguageToolkit toolkit, String name) {
		if (DEBUG) {
			log(toolkit.getLanguageName(), name);
		}
		final IDLTKAssociationManager associationManager = getAssociationManager(toolkit);
		if (associationManager.isAssociatedWith(name)) {
			return true;
		}
		// Acquire content types
		final IContentType[] contentTypes = getContentTypes(toolkit);
		return isValidFileNameForContentType(contentTypes, name);
	}

	public static boolean isValidFileNameForContentType(
			IDLTKLanguageToolkit toolkit, IPath path) {
		if (DEBUG) {
			log(toolkit.getLanguageName(), path);
		}
		final IDLTKAssociationManager associationManager = getAssociationManager(toolkit);
		if (associationManager.isAssociatedWith(path.lastSegment())) {
			return true;
		}
		// Acquire content types
		final IContentType[] contentTypes = getContentTypes(toolkit);
		if (isValidFileNameForContentType(contentTypes, path.lastSegment())) {
			return true;
		}
		if (EnvironmentPathUtils.isFull(path)) {
			final IFileHandle file = EnvironmentPathUtils.getFile(path);
			if (file != null && file.isFile()) {
				if (file.getEnvironment().isLocal()) {
					final File localFile = new File(file.toOSString());
					return toolkit.canValidateContent(file)
							&& validateLocalFileContent(contentTypes, localFile);
				} else {
					return toolkit.canValidateContent(file)
							&& validateRemoteFileContent(contentTypes, file);
				}
			}
			return false;
		}
		if (path.isAbsolute()) {
			final File file = path.toFile();
			if (file.isFile()) {
				return toolkit.canValidateContent(file)
						&& validateLocalFileContent(contentTypes, file);
			}
		}
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IResource member = root.findMember(path);
		return member != null && member.getType() == IResource.FILE
				&& validateResourceContent(contentTypes, (IFile) member);
	}

	private static boolean validateRemoteFileContent(
			final IContentType[] derived, IFileHandle file) {
		if (DEBUG_CONTENT) {
			log("validateContent", file); //$NON-NLS-1$
		}
		for (int i = 0; i < derived.length; i++) {
			IContentType type = derived[i];
			InputStream stream = null;
			try {
				stream = new BufferedInputStream(file.openInputStream(null),
						2048);
				IContentDescription description = type.getDescriptionFor(
						stream, IContentDescription.ALL);
				if (description != null) {
					if (checkDescription(type, description)) {
						return true;
					}
				}
			} catch (IOException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			} finally {
				closeStream(stream);
			}
		}
		return false;
	}

	/**
	 * @param masterType
	 * @param derived
	 * @param file
	 * @return
	 */
	private static boolean validateLocalFileContent(IContentType[] derived,
			File file) {
		if (DEBUG_CONTENT) {
			log("validateContent", file); //$NON-NLS-1$
		}
		for (int i = 0; i < derived.length; i++) {
			IContentType type = derived[i];
			InputStream stream = null;
			try {
				stream = new BufferedInputStream(new FileInputStream(file));
				IContentDescription description = type.getDescriptionFor(
						stream, IContentDescription.ALL);
				if (description != null) {
					if (checkDescription(type, description)) {
						return true;
					}
				}
			} catch (IOException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			} finally {
				closeStream(stream);
			}
		}
		return false;
	}

	private static void closeStream(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Look for derived for associated extensions
	 */
	private static boolean isValidFileNameForContentType(
			IContentType[] contentTypes, String name) {
		if (name == null || name.length() == 0) {
			return false;
		}
		for (int i = 0; i < contentTypes.length; i++) {
			IContentType type = contentTypes[i];
			if (type.isAssociatedWith(name)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidResourceForContentType(
			IDLTKLanguageToolkit toolkit, IResource resource) {
		if (DEBUG) {
			log(toolkit.getLanguageName(), resource);
		}
		if (!(resource instanceof IFile)) {
			return false;
		}
		// Custom filtering via language toolkit
		IStatus status = toolkit.validateSourceModule(resource);
		if (status.getSeverity() != IStatus.OK) {
			return false;
		}
		final IDLTKAssociationManager associationManager = getAssociationManager(toolkit);
		if (associationManager.isAssociatedWith(resource.getName())) {
			try {
				configureAsScript((IFile) resource, toolkit.getNatureId());
			} catch (CoreException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
			return true;
		}
		// Acquire content types
		final IContentType[] contentTypes = getContentTypes(toolkit);
		if (isValidFileNameForContentType(contentTypes, resource.getName())) {
			return true;
		}
		// Delegate the decision if we should validate content to the language
		// toolkit.
		if (!toolkit.canValidateContent(resource)) {
			return false;
		}
		try {
			final String value = resource.getPersistentProperty(DLTK_VALID);
			if (value != null) {
				return TRUE_VALUE.equals(value);
			}
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			return false;
		}
		final boolean result = validateResourceContent(contentTypes,
				(IFile) resource);
		setValidScript((IFile) resource, toolkit.getNatureId(), result);
		return result;
	}

	private static boolean validateResourceContent(
			final IContentType[] contentTypes, final IFile file) {
		if (DEBUG_CONTENT) {
			log("validateContent", file); //$NON-NLS-1$
		}
		try {
			if (contentTypes.length != 0 && file.exists()) {
				final IContentDescription descr = file.getContentDescription();
				if (descr != null) {
					if (descr.getContentType().isKindOf(contentTypes[0])) {
						return true;
					}
				}
			}
		} catch (CoreException e1) {
			if (DLTKCore.DEBUG) {
				e1.printStackTrace();
			}
		}
		try {
			for (int i = 0; i < contentTypes.length; i++) {
				final IContentType type = contentTypes[i];
				/*
				 * TODO use something like LazyInputStream if there are multiple
				 * content types
				 */
				final InputStream contents = new BufferedInputStream(
						file.getContents(), 2048);
				try {
					final IContentDescription description = type
							.getDescriptionFor(contents,
									IContentDescription.ALL);
					if (description != null) {
						if (checkDescription(type, description)) {
							return true;
						}
					}
				} catch (IOException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				} finally {
					closeStream(contents);
				}
			}
		} catch (CoreException e) {
			/*
			 * CoreException is thrown when resource does not exist, is out of
			 * sync or something similar - there is no need to process other
			 * content types if it happens.
			 */
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Sets value of {@link #DLTK_VALID} property
	 * 
	 * @param file
	 * @param natureId
	 * @param value
	 */
	private static void setValidScript(IFile file, String natureId,
			boolean value) {
		try {
			file.setPersistentProperty(DLTK_VALID, value ? TRUE_VALUE
					: FALSE_VALUE);
			if (value) {
				configureAsScript(file, natureId);
			}
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	private static void configureAsScript(IFile file, String natureId)
			throws CoreException {
		final IScriptFileConfigurator[] configurators = ScriptFileConfiguratorManager
				.get(natureId);
		if (configurators != null) {
			for (int i = 0; i < configurators.length; ++i) {
				configurators[i].configure(file);
			}
		}
	}

	private static boolean checkDescription(IContentType type,
			IContentDescription description) {
		Object object = description.getProperty(DLTK_VALID);
		if (object != null && Boolean.TRUE.equals(object)) {
			final IContentType contentType = description.getContentType();
			return contentType.isKindOf(type);
		}
		return false;
	}

	private static final Map<IDLTKLanguageToolkit, IContentType[]> contentTypesCache = new HashMap<IDLTKLanguageToolkit, IContentType[]>();

	private static IContentTypeChangeListener changeListener = null;

	/**
	 * Returns {@link IContentType}s for the specified IDLTKLanguageToolkit. If
	 * there are no content types found empty array is returned. Master content
	 * type is returned first.
	 * 
	 * @param toolkit
	 * @return
	 */
	private static IContentType[] getContentTypes(IDLTKLanguageToolkit toolkit) {
		IContentType[] result;
		synchronized (contentTypesCache) {
			result = contentTypesCache.get(toolkit);
		}
		if (result != null) {
			return result;
		}
		final IContentTypeManager manager = Platform.getContentTypeManager();
		synchronized (contentTypesCache) {
			if (changeListener == null) {
				changeListener = new IContentTypeChangeListener() {
					public void contentTypeChanged(ContentTypeChangeEvent event) {
						synchronized (contentTypesCache) {
							contentTypesCache.clear();
						}
					}
				};
				manager.addContentTypeChangeListener(changeListener);
			}
		}
		final IContentType masterType = manager.getContentType(toolkit
				.getLanguageContentType());
		if (masterType != null) {
			final Set<IContentType> selected = new HashSet<IContentType>();
			for (IContentType type : manager.getAllContentTypes()) {
				if (type.isKindOf(masterType)) {
					selected.add(type);
				}
			}
			result = selected.toArray(new IContentType[selected.size()]);
			for (int i = 1; i < result.length; ++i) {
				if (result[i] == masterType) {
					final IContentType temp = result[0];
					result[0] = result[i];
					result[i] = temp;
					break;
				}
			}
		} else {
			result = new IContentType[0];
		}
		synchronized (contentTypesCache) {
			contentTypesCache.put(toolkit, result);
		}
		return result;
	}

	private static final Map<IDLTKLanguageToolkit, IDLTKAssociationManager> associationManagerCache = new HashMap<IDLTKLanguageToolkit, IDLTKAssociationManager>();

	/**
	 * @param toolkit
	 * @return
	 */
	private static IDLTKAssociationManager getAssociationManager(
			IDLTKLanguageToolkit toolkit) {
		IDLTKAssociationManager manager;
		synchronized (associationManagerCache) {
			manager = associationManagerCache.get(toolkit);
		}
		if (manager != null) {
			return manager;
		}
		manager = toolkit.getPreferenceQualifier() != null ? new DLTKAssociationManager(
				toolkit.getNatureId(), toolkit.getPreferenceQualifier())
				: new NopAssociationManager();
		synchronized (associationManagerCache) {
			associationManagerCache.put(toolkit, manager);
		}
		return manager;
	}

	private static IResourceChangeListener listener = null;

	private static class ResetScriptValidPropertyListener implements
			IResourceChangeListener, IResourceDeltaVisitor {

		public void resourceChanged(IResourceChangeEvent event) {
			try {
				event.getDelta().accept(this);
			} catch (CoreException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			final IResource resource = delta.getResource();
			if (resource.getType() == IResource.FILE) {
				if (delta.getKind() == IResourceDelta.CHANGED
						&& (delta.getFlags() & (IResourceDelta.CONTENT | IResourceDelta.REPLACED)) != 0) {
					resource.setPersistentProperty(DLTK_VALID, null);
				}
				return false;
			}
			return true;
		}

	}

	public static void installListener() {
		if (listener == null) {
			listener = new ResetScriptValidPropertyListener();
			ResourcesPlugin.getWorkspace().addResourceChangeListener(listener,
					IResourceChangeEvent.POST_CHANGE);
		}
	}

	public static void uninstallListener() {
		if (listener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(
					listener);
			listener = null;
		}
	}
}
