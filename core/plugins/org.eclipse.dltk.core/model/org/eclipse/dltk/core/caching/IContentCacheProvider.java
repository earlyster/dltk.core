package org.eclipse.dltk.core.caching;

import java.io.InputStream;

import org.eclipse.dltk.core.environment.IFileHandle;

/**
 * Interface used to fill cache with some additional values.
 */
public interface IContentCacheProvider {
	/**
	 * Called then cache entry are not found in one cache. Implementation need
	 * to fill cache with attributes store in this cache.
	 * 
	 * @param handle
	 * @return true if cache entry are filled into cache.
	 */
	InputStream getAttributeAndUpdateCache(IFileHandle handle, String attribute);

	void setCache(IContentCache cache);
}
