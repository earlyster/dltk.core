/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.dltk.internal.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IModelStatusConstants;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IFileHandle;

/**
 * Represents an external source module.
 */
public class ExternalSourceModule extends AbstractExternalSourceModule {

	private IStorage storage;

	public ExternalSourceModule(ScriptFolder parent, String name,
			WorkingCopyOwner owner, IStorage storage) {
		this(parent, name, owner, true, storage);
	}

	public ExternalSourceModule(ScriptFolder parent, String name,
			WorkingCopyOwner owner, boolean readOnly, IStorage storage) {
		super(parent, name, owner, readOnly);
		this.storage = storage;
	}

	/*
	 * @see org.eclipse.dltk.internal.core.AbstractSourceModule#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ExternalSourceModule)) {
			return false;
		}

		return super.equals(obj);
	}

	/*
	 * @see org.eclipse.core.resources.IStorage#getContents()
	 */
	public InputStream getContents() throws CoreException {
		return storage.getContents();
	}

	/*
	 * @see org.eclipse.dltk.compiler.env.IDependent#getFileName()
	 */
	public char[] getFileName() {
		return getPath().toOSString().toCharArray();
	}

	/*
	 * @see org.eclipse.core.resources.IStorage#getFullPath()
	 */
	public IPath getFullPath() {
		if (this.storage != null) {
			return storage.getFullPath();
		} else {
			return getPath();
		}
	}

	/*
	 * @see org.eclipse.core.resources.IStorage#getName()
	 */
	public String getName() {
		return storage.getName();
	}

	public IResource getResource() {
		return null;
	}

	/*
	 * @see org.eclipse.dltk.internal.core.AbstractSourceModule#getBufferContent()
	 */
	protected char[] getBufferContent() throws ModelException {
		IFileHandle file = EnvironmentPathUtils.getFile(getPath());
		InputStream stream = null;

		try {
			if (file != null && file.exists()) {
				stream = new BufferedInputStream(file.openInputStream());
			} else {
				// This is an archive entry
				boolean inProjectArchive = false;
				ProjectFragment projectFragment = this.getProjectFragment();
				if (projectFragment.isArchive()) {
					if (projectFragment.getResource() != null) {
						inProjectArchive = projectFragment.getResource()
								.exists();
					}
				}
				if (!inProjectArchive) {
					throw newNotPresentException();
				}
				stream = new BufferedInputStream(storage.getContents());
			}

		} catch (IOException e) {
			throw new ModelException(e,
					IModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
		} catch (CoreException e) {
			throw new ModelException(e,
					IModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
		}

		char[] content;
		try {
			content = org.eclipse.dltk.compiler.util.Util
					.getInputStreamAsCharArray(stream, -1, "utf-8"); //$NON-NLS-1$
		} catch (IOException e) {
			throw new ModelException(e, IModelStatusConstants.IO_EXCEPTION);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}

		return content;
	}

	/*
	 * @see org.eclipse.dltk.internal.core.AbstractExternalSourceModule#getModuleType()
	 */
	protected String getModuleType() {
		return "DLTK External Source Moule: "; //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.dltk.internal.core.AbstractSourceModule#getNatureId()
	 */
	protected String getNatureId() throws CoreException {
		IPath path = getFullPath();
		return lookupLanguageToolkit(path).getNatureId();
	}

	/*
	 * @see org.eclipse.dltk.internal.core.AbstractSourceModule#getOriginalSourceModule()
	 */
	protected ISourceModule getOriginalSourceModule() {
		return new ExternalSourceModule((ScriptFolder) getParent(),
				getElementName(), DefaultWorkingCopyOwner.PRIMARY, storage);
	}
}
