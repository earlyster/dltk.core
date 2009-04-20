/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     xored software, Inc. - initial API and Implementation (Yuri Strot) 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.formatter.profiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.dltk.ui.formatter.IProfile;
import org.eclipse.dltk.ui.formatter.IProfileManager;

/**
 * The model for the set of profiles which are available in the workbench.
 */
public class ProfileManager implements IProfileManager {

	/**
	 * A map containing the available profiles, using the IDs as keys.
	 */
	private final Map fProfiles;

	/**
	 * The available profiles, sorted by name.
	 */
	private final List fProfilesByName;

	/**
	 * The currently selected profile.
	 */
	private Profile fSelected;

	/**
	 * Create and initialize a new profile manager.
	 * 
	 * @param profiles
	 *            Initial custom profiles (List of type
	 *            <code>CustomProfile</code>)
	 * @param profileVersioner
	 */
	public ProfileManager(List profiles, Profile defProfile, String profileId) {
		fProfiles = new HashMap();
		fProfilesByName = new ArrayList();

		for (final Iterator iter = profiles.iterator(); iter.hasNext();) {
			final Profile profile = (Profile) iter.next();
			if (profile instanceof CustomProfile) {
				((CustomProfile) profile).setManager(this);
			}
			fProfiles.put(profile.getID(), profile);
			fProfilesByName.add(profile);
		}

		Collections.sort(fProfilesByName);

		Profile profile = (Profile) fProfiles.get(profileId);
		if (profile == null) {
			profile = defProfile;
		}
		fSelected = profile;
	}

	/**
	 * Get an immutable list as view on all profiles, sorted alphabetically.
	 * Unless the set of profiles has been modified between the two calls, the
	 * sequence is guaranteed to correspond to the one returned by
	 * <code>getSortedNames</code>.
	 * 
	 * @return a list of elements of type <code>Profile</code>
	 * 
	 * @see #getSortedDisplayNames()
	 */
	public List getSortedProfiles() {
		return Collections.unmodifiableList(fProfilesByName);
	}

	/**
	 * Get the names of all profiles stored in this profile manager, sorted
	 * alphabetically. Unless the set of profiles has been modified between the
	 * two calls, the sequence is guaranteed to correspond to the one returned
	 * by <code>getSortedProfiles</code>.
	 * 
	 * @return All names, sorted alphabetically
	 * @see #getSortedProfiles()
	 */
	public String[] getSortedDisplayNames() {
		final String[] sortedNames = new String[fProfilesByName.size()];
		int i = 0;
		for (final Iterator iter = fProfilesByName.iterator(); iter.hasNext();) {
			Profile curr = (Profile) iter.next();
			sortedNames[i++] = curr.getName();
		}
		return sortedNames;
	}

	/**
	 * Get the profile for this profile id.
	 * 
	 * @param ID
	 *            The profile ID
	 * @return The profile with the given ID or <code>null</code>
	 */
	public Profile getProfile(String ID) {
		return (Profile) fProfiles.get(ID);
	}

	public IProfile getSelected() {
		return fSelected;
	}

	public void setSelected(IProfile profile) {
		final Profile newSelected = (Profile) fProfiles.get(profile.getID());
		if (newSelected != null && !newSelected.equals(fSelected)) {
			fSelected = newSelected;
		}
	}

	public boolean containsName(String name) {
		for (final Iterator iter = fProfilesByName.iterator(); iter.hasNext();) {
			Profile curr = (Profile) iter.next();
			if (name.equals(curr.getName())) {
				return true;
			}
		}
		return false;
	}

	public void addProfile(IProfile profile) {
		if (profile instanceof CustomProfile) {
			CustomProfile newProfile = (CustomProfile) profile;
			newProfile.setManager(this);
			final CustomProfile oldProfile = (CustomProfile) fProfiles
					.get(profile.getID());
			if (oldProfile != null) {
				fProfiles.remove(oldProfile.getID());
				fProfilesByName.remove(oldProfile);
				oldProfile.setManager(null);
			}
			fProfiles.put(profile.getID(), profile);
			fProfilesByName.add(profile);
			Collections.sort(fProfilesByName);
			fSelected = newProfile;
		}
	}

	/**
	 * Delete the currently selected profile from this profile manager. The next
	 * profile in the list is selected.
	 * 
	 * @return true if the profile has been successfully removed, false
	 *         otherwise.
	 */
	public boolean deleteSelected() {
		return deleteProfile(fSelected);
	}

	public boolean deleteProfile(IProfile profile) {
		if (profile instanceof CustomProfile) {
			CustomProfile oldProfile = (CustomProfile) profile;
			int index = fProfilesByName.indexOf(profile);

			fProfiles.remove(oldProfile.getID());
			fProfilesByName.remove(oldProfile);

			oldProfile.setManager(null);

			if (index >= fProfilesByName.size())
				index--;
			fSelected = (Profile) fProfilesByName.get(index);

			return true;
		}
		return false;
	}

	public IProfile rename(IProfile profile, String newName) {
		final String trimmed = newName.trim();
		if (trimmed.equals(profile.getName()))
			return profile;
		if (profile instanceof BuiltInProfile) {
			CustomProfile newProfile = new CustomProfile(trimmed, profile
					.getSettings(), profile.getFormatterId(), profile
					.getVersion());
			addProfile(newProfile);
			return newProfile;
		} else {
			CustomProfile cProfile = (CustomProfile) profile;

			String oldID = profile.getID();
			cProfile.fName = trimmed;

			fProfiles.remove(oldID);
			fProfiles.put(profile.getID(), profile);

			Collections.sort(fProfilesByName);
			return cProfile;
		}
	}

	public void profileReplaced(CustomProfile oldProfile,
			CustomProfile newProfile) {
		fProfiles.remove(oldProfile.getID());
		fProfiles.put(newProfile.getID(), newProfile);
		fProfilesByName.remove(oldProfile);
		fProfilesByName.add(newProfile);
		Collections.sort(fProfilesByName);

		setSelected(newProfile);
	}
}
