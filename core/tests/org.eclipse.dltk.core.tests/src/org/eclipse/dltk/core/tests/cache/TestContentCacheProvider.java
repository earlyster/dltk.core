package org.eclipse.dltk.core.tests.cache;

import java.io.InputStream;

import org.eclipse.dltk.core.caching.IContentCache;
import org.eclipse.dltk.core.caching.IContentCacheProvider;
import org.eclipse.dltk.core.environment.IFileHandle;

public class TestContentCacheProvider implements IContentCacheProvider {

	public TestContentCacheProvider() {
	}

	public InputStream getAttributeAndUpdateCache(IFileHandle handle,
			String attribute) {
		return null;
	}

	public void setCache(IContentCache cache) {
	}
}
