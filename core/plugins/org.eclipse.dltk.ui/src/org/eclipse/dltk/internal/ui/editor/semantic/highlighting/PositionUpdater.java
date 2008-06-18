package org.eclipse.dltk.internal.ui.editor.semantic.highlighting;

import java.util.List;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.SemanticHighlightingManager.HighlightedPosition;

/**
 * Abstract base class for the language specific semantic highlighting updater.
 */
public abstract class PositionUpdater {

	/**
	 * The result of the semantic highlighting updater execution.
	 * 
	 * TODO it would be more efficient to use arrays instead of lists
	 */
	public static class UpdateResult {

		public final List addedPositions;
		public final List removedPositions;

		/**
		 * @param addedPositions
		 *            positions that should be added. The listed positions
		 *            should not overlap. The list should be sorted by starting
		 *            offset.
		 * @param removedPositions
		 *            positions that should be removed. The listed positions
		 *            should be sorted by starting offset.
		 */
		public UpdateResult(List addedPositions, List removedPositions) {
			this.addedPositions = addedPositions;
			this.removedPositions = removedPositions;
		}
	}

	/**
	 * @param sourceModule
	 *            source module TODO should be changed to allow passing
	 *            arbitrary content - to use in syntax color preferences
	 *            preview.
	 * @param presenter
	 *            used to create {@link HighlightedPosition}s. See
	 *            {@link SemanticHighlightingPresenter#createHighlightedPosition(int, int, Highlighting)}
	 *            TODO probably we should introduce interface
	 *            IHighlightedPositionFactory
	 *            #createHighlightedPosition(start,offset,styleIndex)
	 * @param highlightings
	 *            available highlighting styles
	 * @param currentPositions
	 *            current semantic highlighting positions sorted by starting
	 *            offset.
	 * @return
	 */
	public abstract UpdateResult reconcile(ISourceModule sourceModule,
			SemanticHighlightingPresenter presenter,
			Highlighting[] highlightings, List currentPositions);

}
