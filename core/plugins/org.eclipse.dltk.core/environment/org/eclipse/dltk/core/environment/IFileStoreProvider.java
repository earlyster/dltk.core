package org.eclipse.dltk.core.environment;

import org.eclipse.core.filesystem.IFileStore;

/**
 * @since 2.0
 */
public interface IFileStoreProvider {
	IFileStore getFileStore();
}
