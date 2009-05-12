package org.eclipse.dltk.core.caching;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.dltk.core.environment.IFileHandle;

public interface IContentCache {
	/*
	 * Methods to store content attributes
	 */
	public InputStream getCacheEntryAttribute(IFileHandle handle,
			String attribute);

	public OutputStream getCacheEntryAttributeOutputStream(IFileHandle handle,
			String attribute);

	public String getCacheEntryAttributeString(IFileHandle handle,
			String attribute);

	public boolean setCacheEntryAttribute(IFileHandle handle, String attribute,
			String value);

	public void removeCacheEntryAttributes(IFileHandle handle, String attribute);

	public void clearCacheEntryAttributes(IFileHandle handle);
}