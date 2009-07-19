package org.eclipse.dltk.core.index2;

import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.core.index2.IndexerManager;

public abstract class AbstractIndexer implements IIndexer, IIndexingRequestor {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void indexDocument(ISourceModule sourceModule) {
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(sourceModule);
		if (toolkit == null) {
			return;
		}
		IIndexerParticipant participant = IndexerManager.getIndexerParticipant(
				this, toolkit.getNatureId());
		if (participant != null) {
			IIndexingParser parser = participant.getIndexingParser();
			if (parser != null) {
				parser.parseSourceModule(sourceModule, this);
			}
		}
	}
}
