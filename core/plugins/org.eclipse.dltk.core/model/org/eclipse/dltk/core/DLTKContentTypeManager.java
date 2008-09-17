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
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IFileHandle;

public class DLTKContentTypeManager {

	private static boolean DEBUG = false;

	private static void logMethodEntry(IDLTKLanguageToolkit toolkit,
			Object input) {
		if (input != null) {
			String className = input.getClass().getName();
			int pos = className.lastIndexOf('.');
			if (pos > 0) {
				className = className.substring(pos + 1);
			}
			System.out.println("isValidFileNameForContentType " + input + ':' //$NON-NLS-1$
					+ className + ' ' + toolkit.getLanguageName());
		}
	}

	public static boolean isValidFileNameForContentType(
			IDLTKLanguageToolkit toolkit, String name) {
		if (DEBUG) {
			logMethodEntry(toolkit, name);
		}
		final IContentType masterType = getMasterContentType(toolkit
				.getLanguageContentType());
		if (masterType == null) {
			return false;
		}
		// Acquire derived content types
		final IContentType[] derived = getDerivedContentTypes(masterType);
		return isValidFileNameForContentType(derived, name);
	}

	public static boolean isValidFileNameForContentType(
			IDLTKLanguageToolkit toolkit, IPath path) {
		if (DEBUG) {
			logMethodEntry(toolkit, path);
		}
		final IContentType masterType = getMasterContentType(toolkit
				.getLanguageContentType());
		if (masterType == null) {
			return false;
		}
		final IContentType[] derived = getDerivedContentTypes(masterType);
		if (isValidFileNameForContentType(derived, path.lastSegment())) {
			return true;
		}
		if (EnvironmentPathUtils.isFull(path)) {
			final IFileHandle file = EnvironmentPathUtils.getFile(path);
			if (file != null && file.isFile()) {
				if (file.getEnvironment().isLocal()) {
					final File localFile = new File(file.toOSString());
					return toolkit.canValidateContent(file)
							&& validateLocalFileContent(masterType, derived,
									localFile);
				} else {
					return toolkit.canValidateContent(file)
							&& validateRemoteFileContent(masterType, derived,
									file);
				}
			}
			return false;
		}
		if (path.isAbsolute()) {
			final File file = path.toFile();
			if (file.isFile()) {
				return toolkit.canValidateContent(file)
						&& validateLocalFileContent(masterType, derived, file);
			}
		}
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IResource member = root.findMember(path);
		return member != null && member.getType() == IResource.FILE
				&& validateResourceContent(masterType, derived, (IFile) member);
	}

	private static boolean validateRemoteFileContent(IContentType masterType,
			final IContentType[] derived, IFileHandle file) {
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
	private static boolean validateLocalFileContent(IContentType masterType,
			IContentType[] derived, File file) {
		for (int i = 0; i < derived.length; i++) {
			IContentType type = derived[i];
			InputStream stream = null;
			try {
				stream = new BufferedInputStream(new FileInputStream(file),
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
			IContentType[] derived, String name) {
		for (int i = 0; i < derived.length; i++) {
			IContentType type = derived[i];
			if (type.isAssociatedWith(name)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidResourceForContentType(
			IDLTKLanguageToolkit toolkit, IResource resource) {
		if (DEBUG) {
			logMethodEntry(toolkit, resource);
		}
		if (!(resource instanceof IFile)) {
			return false;
		}
		// Custom filtering via language toolkit
		IStatus status = toolkit.validateSourceModule(resource);
		if (status.getSeverity() != IStatus.OK) {
			return false;
		}
		// Acquire master content type
		final IContentType masterType = getMasterContentType(toolkit
				.getLanguageContentType());
		if (masterType == null) {
			return false;
		}
		// Acquire derived content types
		final IContentType[] derived = getDerivedContentTypes(masterType);
		if (isValidFileNameForContentType(derived, resource.getName())) {
			return true;
		}
		// Check resources accessibility and synchronization
		if (!resource.isAccessible()
				|| !resource.isSynchronized(IResource.DEPTH_ZERO)) {
			return false;
		}
		// Delegate the decision if we should validate content to the language
		// toolkit.
		if (!toolkit.canValidateContent(resource)) {
			return false;
		}
		return validateResourceContent(masterType, derived, (IFile) resource);
	}

	private static boolean validateResourceContent(
			final IContentType masterType, final IContentType[] derived,
			final IFile file) {
		try {
			if (file.exists()) {
				final IContentDescription descr = file.getContentDescription();
				if (descr != null) {
					if (descr.getContentType().isKindOf(masterType)) {
						return true;
					}
				}
			}
		} catch (CoreException e1) {
			if (DLTKCore.DEBUG) {
				e1.printStackTrace();
			}
		}
		for (int i = 0; i < derived.length; i++) {
			final IContentType type = derived[i];
			InputStream contents = null;
			try {
				contents = new BufferedInputStream(file.getContents(), 2048);
				final IContentDescription description = type.getDescriptionFor(
						contents, IContentDescription.ALL);
				if (description != null) {
					if (checkDescription(type, description)) {
						return true;
					}
				}
			} catch (IOException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			} catch (CoreException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			} finally {
				closeStream(contents);
			}
		}
		return false;
	}

	private static IContentType getMasterContentType(String languageContentType) {
		final IContentTypeManager manager = Platform.getContentTypeManager();
		return manager.getContentType(languageContentType);
	}

	private static boolean checkDescription(IContentType type,
			IContentDescription description) {
		Object object = description
				.getProperty(ScriptContentDescriber.DLTK_VALID);
		if (object != null && ScriptContentDescriber.TRUE.equals(object)) {
			final IContentType contentType = description.getContentType();
			return contentType.isKindOf(type);
		}
		return false;
	}

	private static final Map derivedContentTypesCache = new HashMap();
	private static final ILock derivedContentTypesCacheLock = Job
			.getJobManager().newLock();

	private static IContentType[] getDerivedContentTypes(IContentType masterType) {
		derivedContentTypesCacheLock.acquire();
		try {
			if (!derivedContentTypesCache.containsKey(masterType)) {
				IContentTypeManager manager = Platform.getContentTypeManager();
				IContentType[] types = manager.getAllContentTypes();
				Set derived = new HashSet();
				for (int i = 0; i < types.length; i++) {
					if (types[i].isKindOf(masterType)) {
						derived.add(types[i]);
					}
				}
				derivedContentTypesCache.put(masterType, derived
						.toArray(new IContentType[derived.size()]));
			}
			return (IContentType[]) derivedContentTypesCache.get(masterType);
		} finally {
			derivedContentTypesCacheLock.release();
		}
	}
}
