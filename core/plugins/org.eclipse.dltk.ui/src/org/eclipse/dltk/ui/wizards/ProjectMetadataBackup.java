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
package org.eclipse.dltk.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.internal.corext.util.Messages;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.ui.DLTKUIPlugin;

public class ProjectMetadataBackup {

	private static final String DOT = "."; //$NON-NLS-1$

	private static class BackupKey {
		final URI location;
		final String filename;

		public BackupKey(URI location, String filename) {
			this.location = location;
			this.filename = filename;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + filename.hashCode();
			result = prime * result + location.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof BackupKey) {
				final BackupKey other = (BackupKey) obj;
				return filename.equals(other.filename)
						&& location.equals(other.location);
			}
			return false;
		}

	}

	private final Map<BackupKey, File> entries = new HashMap<BackupKey, File>();

	/**
	 * @param projectLocation
	 * @param filenames
	 * @throws CoreException
	 */
	public void backup(URI projectLocation, String[] filenames)
			throws CoreException {
		final IFileStore folder = EFS.getStore(projectLocation);
		if (folder.fetchInfo().exists()) {
			for (int i = 0; i < filenames.length; ++i) {
				final String filename = filenames[i];
				final BackupKey key = new BackupKey(projectLocation, filename);
				if (entries.containsKey(key)) {
					continue;
				}
				final IFileStore file = folder.getChild(filename);
				if (file.fetchInfo().exists()) {
					String tmp = filename;
					if (tmp.startsWith(DOT)) {
						tmp = tmp.substring(DOT.length());
					}
					tmp = "eclipse-" + tmp + "-";//$NON-NLS-1$ //$NON-NLS-2$
					final File backup = createBackup(file, tmp);
					entries.put(key, backup);
				} else {
					entries.put(key, null);
				}
			}
		} else {
			for (int i = 0; i < filenames.length; ++i) {
				final BackupKey key = new BackupKey(projectLocation,
						filenames[i]);
				if (entries.containsKey(key)) {
					continue;
				}
				entries.put(key, null);
			}
		}
	}

	/**
	 * @param projectLocation
	 * @param monitor
	 * @throws CoreException
	 * @since 2.0
	 */
	public void restore(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("", entries.size() * 2); //$NON-NLS-1$
		try {
			for (Map.Entry<BackupKey, File> entry : entries.entrySet()) {
				try {
					final File backup = entry.getValue();
					if (backup == null) {
						monitor.worked(2);
						continue;
					}
					final IFileStore projectFile = EFS.getStore(
							entry.getKey().location).getChild(
							entry.getKey().filename);
					projectFile.delete(EFS.NONE, new SubProgressMonitor(
							monitor, 1));
					copyFile(backup, projectFile, new SubProgressMonitor(
							monitor, 1));
					backup.delete();
				} catch (IOException e) {
					IStatus status = new Status(
							IStatus.ERROR,
							DLTKUIPlugin.PLUGIN_ID,
							IStatus.ERROR,
							NewWizardMessages.ScriptProjectWizardSecondPage_problem_restore_project,
							e);
					throw new CoreException(status);
				}
			}
		} finally {
			entries.clear();
		}
	}

	private File createBackup(IFileStore source, String name)
			throws CoreException {
		try {
			File bak = File.createTempFile(name, ".bak"); //$NON-NLS-1$
			copyFile(source, bak);
			return bak;
		} catch (IOException e) {
			IStatus status = new Status(
					IStatus.ERROR,
					DLTKUIPlugin.PLUGIN_ID,
					IStatus.ERROR,
					Messages
							.format(
									NewWizardMessages.ScriptProjectWizardSecondPage_problem_backup,
									name), e);
			throw new CoreException(status);
		}
	}

	private void copyFile(IFileStore source, File target) throws IOException,
			CoreException {
		InputStream is = source.openInputStream(EFS.NONE, null);
		FileOutputStream os = new FileOutputStream(target);
		copyFile(is, os);
	}

	private void copyFile(File source, IFileStore target,
			IProgressMonitor monitor) throws IOException, CoreException {
		FileInputStream is = new FileInputStream(source);
		OutputStream os = target.openOutputStream(EFS.NONE, monitor);
		copyFile(is, os);
	}

	private void copyFile(InputStream is, OutputStream os) throws IOException {
		try {
			byte[] buffer = new byte[8192];
			while (true) {
				int bytesRead = is.read(buffer);
				if (bytesRead == -1)
					break;

				os.write(buffer, 0, bytesRead);
			}
		} finally {
			try {
				is.close();
			} finally {
				os.close();
			}
		}
	}

}
