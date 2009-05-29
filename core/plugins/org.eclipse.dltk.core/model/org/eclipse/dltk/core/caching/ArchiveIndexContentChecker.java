package org.eclipse.dltk.core.caching;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
			indexResource.load(zipFile.getInputStream(entry), null);
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
				if (cacheEntry.getTimestamp() != timestamp) {
					return true;
				}
				collected.add(childFile);
			}
		}
		File[] listFiles = parent.listFiles();
		for (File file : listFiles) {
			if (needIndexing(file) && !collected.contains(file)) {
				return true;
			}
		}
		return false;
	}

	private boolean needIndexing(File file) {
		if (file.isFile()) {
			return DLTKContentTypeManager.isValidFileNameForContentType(
					toolkit, new Path(file.getAbsolutePath()));
		}
		return false;
	}
}
