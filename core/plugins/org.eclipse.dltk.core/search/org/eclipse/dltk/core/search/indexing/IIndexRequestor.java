package org.eclipse.dltk.core.search.indexing;

import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.ISourceElementRequestorExtension;
import org.eclipse.dltk.compiler.SourceElementRequestorKind;
import org.eclipse.dltk.core.ISourceElementParser;

/**
 * Marker interface for {@link ISourceElementRequestor} to identify if
 * {@link ISourceElementParser} was called by the indexer.
 * 
 * Deprecated since 2010-11-26
 * 
 * @see ISourceElementRequestorExtension
 * @see SourceElementRequestorKind
 * @since 2.0
 */
@Deprecated
public interface IIndexRequestor {

}
