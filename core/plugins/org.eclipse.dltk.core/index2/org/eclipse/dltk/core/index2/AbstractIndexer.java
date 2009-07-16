package org.eclipse.dltk.core.index2;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.PriorityClassDLTKExtensionManager;

public abstract class AbstractIndexer implements IIndexer, IIndexingRequestor {

	private static PriorityClassDLTKExtensionManager parserManager = new PriorityClassDLTKExtensionManager(
			DLTKCore.PLUGIN_ID + ".index2Parser"); //$NON-NLS-1$

	public void indexDocument(ISourceModule sourceModule) {

		IIndexingParser parser = (IIndexingParser) parserManager
				.getObject(sourceModule);
		if (parser != null) {
			parser.parseSourceModule(sourceModule, this);
		}
	}
}
