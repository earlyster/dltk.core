package org.eclipse.dltk.core.tests.cache;

import org.eclipse.dltk.core.caching.IContentCache;
import org.eclipse.dltk.core.caching.IContentCacheProvider;
import org.eclipse.dltk.core.environment.IFileHandle;

public class TestContentCacheProvider implements IContentCacheProvider {

	public TestContentCacheProvider() {
	}

	public boolean updateCache(IFileHandle handle, IContentCache cache) {
		return false;
	}
}
