package org.eclipse.dltk.core.caching;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.caching.cache.CacheEntry;
import org.eclipse.dltk.core.caching.cache.CacheEntryAttribute;
import org.eclipse.dltk.core.caching.cache.CacheFactory;
import org.eclipse.dltk.core.caching.cache.CacheIndex;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

/**
 * Class designed to build archive index files.
 */
public class ArchiveCacheIndexBuilder {
	private ZipOutputStream zip;
	private CacheIndex index = CacheFactory.eINSTANCE.createCacheIndex();
	private long version;

	public ArchiveCacheIndexBuilder(OutputStream stream, long version)
			throws IOException {
		zip = new ZipOutputStream(new BufferedOutputStream(stream, 8096));
		this.version = version;
	}

	public void addEntry(String fileName, long timeStamp, String attribute,
			InputStream value) throws IOException {
		CacheEntry entry = getEntry(fileName, timeStamp);
		EList<CacheEntryAttribute> attributes = entry.getAttributes();
		String location = generateLocation(fileName, attribute);
		CacheEntryAttribute attr = CacheFactory.eINSTANCE
				.createCacheEntryAttribute();
		attr.setName(attribute);
		attr.setLocation(location);
		attributes.add(attr);
		ZipEntry zipEntry = new ZipEntry(location);
		zip.putNextEntry(zipEntry);
		Util.copy(value, zip);
		zip.closeEntry();
	}

	public void done() throws IOException {
		// Write .index file.
		Resource res = new XMIResourceImpl();
		res.getContents().add(index);
		ZipEntry entry = new ZipEntry(".index");
		zip.putNextEntry(entry);
		res.save(zip, null);
		zip.closeEntry();
		zip.close();
	}

	private String generateLocation(String path, String attribute) {
		return "_" + path + "_" + attribute;
	}

	private CacheEntry getEntry(String path, long timeStamp) {
		EList<CacheEntry> entries = index.getEntries();
		for (CacheEntry cacheEntry : entries) {
			if (cacheEntry.getPath().equals(path)) {
				return cacheEntry;
			}
		}
		CacheEntry entry = CacheFactory.eINSTANCE.createCacheEntry();
		entry.setLastAccessTime(version);
		entry.setPath(path);
		entry.setTimestamp(timeStamp);
		index.getEntries().add(entry);
		return entry;
	}
}
