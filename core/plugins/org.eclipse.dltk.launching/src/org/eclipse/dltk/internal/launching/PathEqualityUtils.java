/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.launching;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

public class PathEqualityUtils {

	private static final class Win32PathCompare implements IPathEquality {

		public boolean equals(IPath path1, IPath path2) {
			/*
			 * the .equals method of IPath ignores trailing separators so we
			 * must as well
			 */
			return path1.removeTrailingSeparator().toOSString()
					.equalsIgnoreCase(
							path2.removeTrailingSeparator().toOSString());
		}

		public boolean isPrefixOf(IPath path, IPath anotherPath) {
			if (path.getDevice() == null) {
				if (anotherPath.getDevice() != null) {
					return false;
				}
			} else {
				if (!path.getDevice().equalsIgnoreCase(anotherPath.getDevice())) {
					return false;
				}
			}
			if (path.isEmpty() || (path.isRoot() && anotherPath.isAbsolute())) {
				return true;
			}
			int len = path.segmentCount();
			if (len > anotherPath.segmentCount()) {
				return false;
			}
			for (int i = 0; i < len; i++) {
				if (!path.segment(i).equalsIgnoreCase(anotherPath.segment(i)))
					return false;
			}
			return true;
		}

	}

	private static final class GenericPathCompare implements IPathEquality {

		public boolean equals(IPath path1, IPath path2) {
			return path1.equals(path2);
		}

		public boolean isPrefixOf(IPath path, IPath anotherPath) {
			return path.isPrefixOf(anotherPath);
		}

	}

	public static synchronized IPathEquality getInstance() {
		final IPathEquality compare;
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			compare = new Win32PathCompare();
		} else {
			compare = new GenericPathCompare();
		}
		return compare;
	}

}
