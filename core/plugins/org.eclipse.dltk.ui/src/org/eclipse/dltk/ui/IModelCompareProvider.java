package org.eclipse.dltk.ui;

/**
 * Interface used to provide specific compare information for custom elements.
 */
public interface IModelCompareProvider {

	/**
	 * Used in compare, category functions.
	 */
	public static final class CompareResult {
		int result;

		public CompareResult(int result) {
			this.result = result;
		}
	}

	public static final CompareResult GREATER = new CompareResult(1);
	public static final CompareResult LESS = new CompareResult(-1);
	public static final CompareResult EQUALS = new CompareResult(0);

	/**
	 * Return category for specified element.
	 * 
	 * @see IModelCompareCategories for category constants.
	 * 
	 * @return One of {@link IModelCompareCategories} in {@link CompareResult}
	 *         Return null if comparison of element are not supported.
	 */
	Integer category(Object parentElement);

	/**
	 * Compare two elements.
	 * 
	 * Return null if comparison of elements are not supported.
	 */
	CompareResult compare(Object element1, Object element2, int cat1, int cat2);
}
