package org.eclipse.dltk.core.search;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.ISearchFactory;
import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.search.indexing.SourceIndexerRequestor;
import org.eclipse.dltk.core.search.matching.MatchLocator;
import org.eclipse.dltk.core.search.matching.MatchLocatorParser;

public abstract class AbstractSearchFactory implements ISearchFactory {
	public SourceIndexerRequestor createSourceRequestor() {
		return new SourceIndexerRequestor();
	}

	public DLTKSearchParticipant createSearchParticipant() {
		return null;
	}

	@Deprecated
	public final MatchLocator createMatchLocator(SearchPattern pattern,
			SearchRequestor requestor, IDLTKSearchScope scope,
			SubProgressMonitor monitor) {
		return new MatchLocator();
	}

	public ISearchPatternProcessor createSearchPatternProcessor() {
		return null;
	}

	public String getNormalizedTypeName(IType type) {
		return type.getElementName();
	}

	public IMatchLocatorParser createMatchParser(MatchLocator locator) {
		return new MatchLocatorParser(locator) {
		};
	}
}
