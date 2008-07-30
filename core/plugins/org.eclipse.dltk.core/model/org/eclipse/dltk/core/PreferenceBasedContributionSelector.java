package org.eclipse.dltk.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;

/**
 * Contribution selector based on the contribution id saved in the preferences.
 * 
 * The concrete preference key to retrieve saved id from is specified the
 * extension point, for example:
 * 
 * <pre>
 * &lt;selector class=&quot;org.eclipse.dltk.core.ConfigurableContributionSelector&quot;&gt;
 * &lt;parameter name=&quot;qualifier&quot; value=&quot;org.eclipse.dltk.ruby.ui&quot;/&gt;
 * &lt;parameter name=&quot;key&quot; value=&quot;formatterId&quot;/&gt;
 * &lt;/selector&gt;
 * </pre>
 */
public class PreferenceBasedContributionSelector extends
		DLTKIdContributionSelector implements IExecutableExtension {

	/**
	 * The node name of the child elements
	 */
	private static final String NODE_PARAM = "parameter"; //$NON-NLS-1$

	/**
	 * the name of the parameter name attribute
	 */
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$

	/**
	 * the name of the parameter value attribute
	 */
	private static final String ATR_VALUE = "value"; //$NON-NLS-1$

	/**
	 * qualifier parameter name
	 */
	private static final String PARAM_QUALIFIER = "qualifier"; //$NON-NLS-1$

	/**
	 * key parameter name
	 */
	private static final String PARAM_KEY = "key"; //$NON-NLS-1$

	private String qualifier;
	private String key;

	protected String getSavedContributionId(PreferencesLookupDelegate delegate) {
		if (qualifier != null && key != null) {
			return delegate.getString(qualifier, key);
		} else {
			return null;
		}
	}

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		IConfigurationElement[] parameters = config.getChildren(NODE_PARAM);
		for (int i = 0; i < parameters.length; ++i) {
			final IConfigurationElement parameter = parameters[i];
			final String parameterName = parameter.getAttribute(ATTR_NAME);
			if (PARAM_QUALIFIER.equals(parameterName)) {
				qualifier = parameter.getAttribute(ATR_VALUE);
			} else if (PARAM_KEY.equals(parameterName)) {
				key = parameter.getAttribute(ATR_VALUE);
			}
		}
	}

	/**
	 * @return the qualifier
	 */
	public String getQualifier() {
		return qualifier;
	}

	/**
	 * @param qualifier
	 *            the qualifier to set
	 */
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

}
