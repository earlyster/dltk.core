package org.eclipse.dltk.core.index2;

/**
 * Provides language dependent implementations for indexing parser and element
 * resolver.
 * 
 * @author michael
 * 
 */
public interface IIndexerParticipant {

	/**
	 * Returns indexing parser for indexer.
	 * 
	 * @return
	 */
	public IIndexingParser getIndexingParser();

	/**
	 * Returns element resolver for indexer.
	 * 
	 * @return
	 */
	public IElementResolver getElementResolver();
}
