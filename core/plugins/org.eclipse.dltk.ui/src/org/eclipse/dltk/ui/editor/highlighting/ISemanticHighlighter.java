package org.eclipse.dltk.ui.editor.highlighting;

import java.util.List;

import org.eclipse.dltk.ui.editor.highlighting.HighlightedPosition;

/**
 * Language specific semantic highlighting API.
 */
public interface ISemanticHighlighter {

	/**
	 * The result of the semantic highlighting execution.
	 */
	public static class UpdateResult {

		public final HighlightedPosition[] addedPositions;
		public final HighlightedPosition[] removedPositions;

		/**
		 * @param added
		 *            positions that should be added. The listed positions
		 *            should not overlap. The list should be sorted by starting
		 *            offset.
		 * @param removed
		 *            positions that should be removed. The listed positions
		 *            should be sorted by starting offset.
		 * @deprecated
		 */
		public UpdateResult(List added, List removed) {
			this.addedPositions = new HighlightedPosition[added.size()];
			added.toArray(this.addedPositions);
			this.removedPositions = new HighlightedPosition[removed.size()];
			removed.toArray(this.removedPositions);
		}

		/**
		 * @param addedPositions
		 *            positions that should be added. The listed positions
		 *            should not overlap. The array should be sorted by starting
		 *            offset.
		 * @param removedPositions
		 *            positions that should be removed. The listed positions
		 *            should be sorted by starting offset.
		 */
		public UpdateResult(HighlightedPosition[] addedPositions,
				HighlightedPosition[] removedPositions) {
			this.addedPositions = addedPositions;
			this.removedPositions = removedPositions;
		}
	}

	/**
	 * Initialize the position updater
	 * 
	 * @param factory
	 *            factory to create {@link HighlightedPosition}s
	 * @param styles
	 *            available highlighting styles
	 */
	void initialize(IHighlightedPositionFactory factory,
			HighlightingStyle[] styles);

	/**
	 * @param code
	 *            source module
	 * @param currentPositions
	 *            current semantic {@link HighlightedPosition}s sorted by
	 *            starting offset.
	 * @return
	 */
	UpdateResult reconcile(org.eclipse.dltk.compiler.env.ISourceModule code,
			List currentPositions);

}
