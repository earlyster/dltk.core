package org.eclipse.dltk.core.caching;

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
	boolean updateCache(IFileHandle handle, IContentCache cache);
}
