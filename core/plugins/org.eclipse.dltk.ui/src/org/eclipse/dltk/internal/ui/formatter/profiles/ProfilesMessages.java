/**
 * 
 */
package org.eclipse.dltk.internal.ui.formatter.profiles;

import org.eclipse.osgi.util.NLS;

/**
 * @author Yuri Strot
 *
 */
public class ProfilesMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.ui.formatter.profiles.ProfilesMessages"; //$NON-NLS-1$
	public static String ProfileStore_noValueForKey;
	public static String ProfileStore_readingProblems;
	public static String ProfileStore_serializingProblems;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ProfilesMessages.class);
	}

	private ProfilesMessages() {
	}
}
