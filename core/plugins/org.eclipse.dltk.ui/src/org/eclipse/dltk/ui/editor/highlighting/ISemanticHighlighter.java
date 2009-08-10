package org.eclipse.dltk.ui.editor.highlighting;

import java.util.List;

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
			List<HighlightedPosition> currentPositions);

}
