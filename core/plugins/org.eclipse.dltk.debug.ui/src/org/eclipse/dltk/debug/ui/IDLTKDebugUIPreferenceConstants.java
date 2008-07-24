package org.eclipse.dltk.debug.ui;


public interface IDLTKDebugUIPreferenceConstants {

	String PREF_ACTIVE_FILTERS_LIST = DLTKDebugUIPlugin.PLUGIN_ID
			+ ".preference_active_filters_list"; //$NON-NLS-1$
	String PREF_INACTIVE_FILTERS_LIST = DLTKDebugUIPlugin.PLUGIN_ID
			+ ".preference_inactive_filters_list"; //$NON-NLS-1$
	String PREF_ALERT_HCR_FAILED = DLTKDebugUIPlugin.PLUGIN_ID
			+ ".alert_hot_code_replace_failed"; //$NON-NLS-1$
	String PREF_ALERT_HCR_NOT_SUPPORTED = DLTKDebugUIPlugin.PLUGIN_ID
			+ ".alert_hot_code_replace_not_supported"; //$NON-NLS-1$

	/**
	 * String preference indication when and where variable details should
	 * appear. Valid values include:
	 * <ul>
	 * <li><code>INLINE_ALL</code> to show inline details for all variables
	 * <li><code>INLINE_FORMATTERS</code> to show inline details for variables
	 * with formatters
	 * <li><code>DETAIL_PANE</code> to show details only in the detail pane
	 * </ul>
	 */
	public static final String PREF_SHOW_DETAILS = DLTKDebugUIPlugin.PLUGIN_ID
			+ ".show_details"; //$NON-NLS-1$

	/**
	 * "Show detail" preference values.
	 */
	public static final String INLINE_ALL = "INLINE_ALL"; //$NON-NLS-1$
	public static final String INLINE_FORMATTERS = "INLINE_FORMATTERS"; //$NON-NLS-1$
	public static final String DETAIL_PANE = "DETAIL_PANE"; //$NON-NLS-1$
}
