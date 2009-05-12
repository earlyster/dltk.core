package org.eclipse.dltk.core.caching;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.caching.cache.CacheEntry;
import org.eclipse.dltk.core.caching.cache.CacheEntryAttribute;
import org.eclipse.dltk.core.caching.cache.CacheIndex;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

/**
 * This cache provider checks for folder .index files and load such files into
 * required cache.
 * 
 * @author Andrei Sobolev
 */
public class ArchiveContentCacheProvider implements IContentCacheProvider {

	private File archiveTempFile;

	public ArchiveContentCacheProvider() {
		IPath stateLocation = DLTKCore.getDefault().getStateLocation();
		IPath arhiveTempLocation = stateLocation.append("archive_cache_temp");
		archiveTempFile = new File(arhiveTempLocation.toOSString());
		if (!archiveTempFile.exists()) {
			archiveTempFile.mkdir();
		}
	}

	public boolean updateCache(IFileHandle handle, IContentCache cache) {
		IFileHandle parent = handle.getParent();
		String DLTK_INDEX_FILE = ".dltk.index";
		IFileHandle child = parent.getChild(DLTK_INDEX_FILE);
		boolean found = false;
		if (child != null && child.exists()) {
			// Copy zip file into metadata temporaty location
			try {
				File file = File.createTempFile("dltk_archive_cache", "d",
						archiveTempFile);
				Util.copy(file, child
						.openInputStream(new NullProgressMonitor()));
				ZipFile zipFile = new ZipFile(file);
				ZipEntry entry = zipFile.getEntry(".index");
				Resource indexResource = new XMIResourceImpl(URI
						.createURI("dltk_cache://zipIndex"));
				indexResource.load(zipFile.getInputStream(entry), null);
				EList<EObject> contents = indexResource.getContents();
				for (EObject eObject : contents) {
					CacheIndex cacheIndex = (CacheIndex) eObject;
					EList<CacheEntry> entries = cacheIndex.getEntries();
					for (CacheEntry cacheEntry : entries) {
						String path = cacheEntry.getPath();
						IFileHandle entryHandle = parent.getChild(path);
						if (entryHandle.exists()
								&& entryHandle.lastModified() == cacheEntry
										.getTimestamp()) {
							if (handle.equals(entryHandle)) {
								found = true;
							}
							EList<CacheEntryAttribute> attributes = cacheEntry
									.getAttributes();
							for (CacheEntryAttribute cacheEntryAttribute : attributes) {
								OutputStream stream = cache
										.getCacheEntryAttributeOutputStream(
												entryHandle,
												cacheEntryAttribute.getName());
								String location = cacheEntryAttribute
										.getLocation();
								ZipEntry zipEntry = zipFile.getEntry(location);
								InputStream inputStream = zipFile
										.getInputStream(zipEntry);
								Util.copy(inputStream, stream);
								stream.close();
								inputStream.close();
							}
						}
					}
				}
				file.delete(); // Delete unused zip archive.
			} catch (IOException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		return found;
	}
}
