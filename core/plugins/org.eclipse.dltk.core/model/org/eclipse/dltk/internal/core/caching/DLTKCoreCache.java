package org.eclipse.dltk.internal.core.caching;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SimpleClassDLTKExtensionManager;
import org.eclipse.dltk.core.caching.AbstractContentCache;
import org.eclipse.dltk.core.caching.IContentCacheProvider;
import org.eclipse.dltk.core.caching.MetadataContentCache;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IFileHandle;

public class DLTKCoreCache extends AbstractContentCache {
	private MetadataContentCache metadataCache;
	private SimpleClassDLTKExtensionManager extensions = new SimpleClassDLTKExtensionManager(
			DLTKCore.PLUGIN_ID + ".contentCacheProvider");

	private ModelCacheListener listener = new ModelCacheListener() {
		protected void remove(IProject element) {
			// TODO: Maybe here visit element and also remove all children?
			IFileHandle handle = EnvironmentPathUtils.getFile(element);
			if (handle != null) {
				metadataCache.clearCacheEntryAttributes(handle);
			}
		};

		protected void remove(IScriptProject element) {
			removeElement(element);
		}

		private void removeElement(IModelElement element) {
			IFileHandle handle = EnvironmentPathUtils.getFile(element);
			if (handle != null) {
				metadataCache.clearCacheEntryAttributes(handle);
			}
		};

		protected void remove(IScriptFolder element) {
			removeElement(element);
		};

		protected void remove(ISourceModule element) {
			removeElement(element);
		};
	};

	public DLTKCoreCache() {
		metadataCache = new MetadataContentCache(DLTKCore.getDefault()
				.getStateLocation().append("cache"));
		DLTKCore.addElementChangedListener(listener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
		initialize();
	}

	private void initialize() {
		Object[] objects = extensions.getObjects();
		for (int i = 0; i < objects.length; i++) {
			IContentCacheProvider provider = (IContentCacheProvider) objects[i];
			provider.setCache(metadataCache);
		}
	}

	public void stop() {
		metadataCache.save(false);
		DLTKCore.removeElementChangedListener(listener);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
	}

	public void clearCacheEntryAttributes(IFileHandle handle) {
		metadataCache.clearCacheEntryAttributes(handle);
	}

	public InputStream getCacheEntryAttribute(IFileHandle handle,
			String attribute) {
		if (handle == null) {
			return null;
		}
		InputStream result = metadataCache.getCacheEntryAttribute(handle,
				attribute);
		if (result == null) {
			// Ask for IContentCacheProviders
			Object[] objects = extensions.getObjects();
			for (int i = 0; i < objects.length; i++) {
				IContentCacheProvider provider = (IContentCacheProvider) objects[i];
				InputStream stream = provider.getAttributeAndUpdateCache(
						handle, attribute);
				if (stream != null) {
					return stream;
				}
			}
		}
		return result;
	}

	public OutputStream getCacheEntryAttributeOutputStream(IFileHandle handle,
			String attribute) {
		return metadataCache.getCacheEntryAttributeOutputStream(handle,
				attribute);
	}

	public void removeCacheEntryAttributes(IFileHandle handle, String attribute) {
		metadataCache.removeCacheEntryAttributes(handle, attribute);
	}

	public void clear() {
		metadataCache.clear();
	}

	public File getEntryAsFile(IFileHandle handle, String attribute) {
		return metadataCache.getEntryAsFile(handle, attribute);
	}
}
