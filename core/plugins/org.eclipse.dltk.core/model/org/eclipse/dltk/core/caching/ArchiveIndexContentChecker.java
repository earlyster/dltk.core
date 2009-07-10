package org.eclipse.dltk.core.caching;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKContentTypeManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.caching.cache.CacheEntry;
import org.eclipse.dltk.core.caching.cache.CacheIndex;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

public class ArchiveIndexContentChecker {
	private ZipFile zipFile;
	private EList<EObject> contents;
	private File file;
	private long version;
	private IDLTKLanguageToolkit toolkit;

	public ArchiveIndexContentChecker(File file, long version,
			IDLTKLanguageToolkit toolkit) {
		this.file = file;
		this.version = version;
		this.toolkit = toolkit;
		try {
			zipFile = new ZipFile(file);

			ZipEntry entry = zipFile.getEntry(".index");
			Resource indexResource = new XMIResourceImpl(URI
					.createURI("dltk_cache://zipIndex"));
			indexResource.load(new BufferedInputStream(zipFile
					.getInputStream(entry), 8096), null);
			contents = indexResource.getContents();
		} catch (Exception e) {
		}
	}

	public boolean containChanges() {
		if (contents == null) {
			return true;
		}
		File parent = file.getParentFile();
		List<File> collected = new ArrayList<File>();
		for (EObject eObject : contents) {
			CacheIndex cacheIndex = (CacheIndex) eObject;
			EList<CacheEntry> entries = cacheIndex.getEntries();
			for (CacheEntry cacheEntry : entries) {
				String path = cacheEntry.getPath();
				File childFile = new File(parent, path);
				if (!childFile.exists()) {
					return true;
				}
				if (cacheEntry.getLastAccessTime() != version) {
					return true;
				}
				long timestamp = childFile.lastModified();
				try {
					File canonicalFile = childFile.getCanonicalFile();
					if (!canonicalFile.getAbsolutePath().equals(
							file.getAbsolutePath())) {
						// This is symlink
						timestamp = canonicalFile.lastModified();
					}
				} catch (IOException e) {
					return true;
				}

				long cacheStamp = cacheEntry.getTimestamp() / 1000;
				timestamp = timestamp / 1000;
				if (cacheStamp != timestamp) {
					return true;
				}
				collected.add(childFile);
			}
		}
		File[] listFiles = parent.listFiles();
		for (File file : listFiles) {
			if (file.isFile() && needIndexing(file.getName())
					&& !collected.contains(file)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @since 2.0
	 */
	public boolean containChanges(IFileStore store) {
		if (contents == null) {
			return true;
		}
		IFileStore parent = store.getParent();
		List<IFileStore> collected = new ArrayList<IFileStore>();
		for (EObject eObject : contents) {
			CacheIndex cacheIndex = (CacheIndex) eObject;
			EList<CacheEntry> entries = cacheIndex.getEntries();
			for (CacheEntry cacheEntry : entries) {
				String path = cacheEntry.getPath();
				IFileStore childFile = parent.getChild(path);
				IFileInfo childFileInfo = childFile.fetchInfo();
				if (!childFileInfo.exists()) {
					return true;
				}
				if (cacheEntry.getLastAccessTime() != version) {
					return true;
				}
				long timestamp = childFileInfo.getLastModified();
				if (childFileInfo.getAttribute(EFS.ATTRIBUTE_SYMLINK)) {
					String canonicalFile = childFileInfo
							.getStringAttribute(EFS.ATTRIBUTE_LINK_TARGET);
					IFileStore fileStore = childFile.getFileStore(new Path(
							canonicalFile));
					IFileInfo fetchInfo = fileStore.fetchInfo();
					timestamp = fetchInfo.getLastModified();
				}
				long cacheStamp = cacheEntry.getTimestamp() / 1000;
				timestamp = timestamp / 1000;
				if (cacheStamp != timestamp) {
					return true;
				}
				collected.add(childFile);
			}
		}
		IFileStore[] listFiles = null;
		;
		try {
			listFiles = parent.childStores(EFS.NONE, new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (listFiles != null) {
			for (IFileStore file : listFiles) {
				IFileInfo fileInfo = file.fetchInfo();
				if (!fileInfo.isDirectory() && needIndexing(file.getName())
						&& !collected.contains(file)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean needIndexing(String name) {
		return DLTKContentTypeManager.isValidFileNameForContentType(toolkit,
				new Path(name));
	}
}
