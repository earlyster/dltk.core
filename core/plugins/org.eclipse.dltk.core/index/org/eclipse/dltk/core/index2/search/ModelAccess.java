package org.eclipse.dltk.core.index2.search;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.IIndexer;
import org.eclipse.dltk.core.index2.IIndexerParticipant;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.index2.search.ISearchEngine.SearchFor;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.internal.core.index2.IndexerManager;

/**
 * Utility for accessing DLTK model elements through index
 * 
 * @author michael
 * 
 */
public class ModelAccess {

	public IField[] findFields(String name, MatchRule matchRule, int flags,
			IDLTKSearchScope scope) {

		List<IField> result = new LinkedList<IField>();
		findElements(IModelElement.FIELD, name, matchRule, flags, scope, result);
		return (IField[]) result.toArray(new IField[result.size()]);
	}

	public IMethod[] findMethods(String name, MatchRule matchRule, int flags,
			IDLTKSearchScope scope) {

		List<IMethod> result = new LinkedList<IMethod>();
		findElements(IModelElement.METHOD, name, matchRule, flags, scope,
				result);
		return (IMethod[]) result.toArray(new IMethod[result.size()]);
	}

	public IType[] findTypes(String name, MatchRule matchRule, int flags,
			IDLTKSearchScope scope) {

		List<IType> result = new LinkedList<IType>();
		findElements(IModelElement.TYPE, name, matchRule, flags, scope, result);
		return (IType[]) result.toArray(new IType[result.size()]);
	}

	protected <T extends IModelElement> void findElements(int elementType,
			String name, MatchRule matchRule, int flags,
			IDLTKSearchScope scope, final Collection<T> result) {

		IIndexer indexer = IndexerManager.getIndexer();
		final IIndexerParticipant participant = IndexerManager
				.getIndexerParticipant(indexer, scope.getLanguageToolkit()
						.getNatureId());
		if (indexer == null || participant == null) {
			return;
		}

		ISearchEngine searchEngine = indexer.createSearchEngine();

		searchEngine.search(elementType, name, flags, 0,
				SearchFor.DECLARATIONS, matchRule, scope,
				new ISearchRequestor() {

					public void match(int elementType, int flags, int offset,
							int length, int nameOffset, int nameLength,
							String elementName, String metadata,
							String qualifier, ISourceModule sourceModule,
							boolean isReference) {

						IModelElement element = participant
								.getElementResolver().resolve(elementType,
										flags, offset, length, nameOffset,
										nameLength, elementName, metadata,
										qualifier, sourceModule);
						if (element != null) {
							result.add((T) element);
						}
					}
				}, null);
	}
}