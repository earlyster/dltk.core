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
package org.eclipse.dltk.formatter.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.dltk.formatter.internal.ExcludeRegionList;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class ExcludeRegionListTest extends TestCase {

	public void testExcludes1() {
		ExcludeRegionList w = new ExcludeRegionList();
		w.excludeRegion(new Region(0, 2));
		w.excludeRegion(new Region(1, 2));
		List<IRegion> excludes = w.getExcludes();
		assertEquals(1, excludes.size());
		assertEquals(new Region(0, 3), excludes.get(0));
	}

	public void testExcludes2() {
		ExcludeRegionList w = new ExcludeRegionList();
		w.excludeRegion(new Region(0, 1));
		w.excludeRegion(new Region(1, 2));
		List<IRegion> excludes = w.getExcludes();
		assertEquals(1, excludes.size());
		assertEquals(new Region(0, 3), excludes.get(0));
	}

	public void testExcludes3() {
		ExcludeRegionList w = new ExcludeRegionList();
		final Region r04 = new Region(0, 4);
		w.excludeRegion(r04);
		w.excludeRegion(new Region(1, 2));
		List<IRegion> excludes = w.getExcludes();
		assertEquals(1, excludes.size());
		assertEquals(r04, excludes.get(0));
		assertSame(r04, excludes.get(0));
	}

	public void testExcludes4() {
		ExcludeRegionList w = new ExcludeRegionList();
		w.excludeRegion(new Region(0, 2));
		w.excludeRegion(new Region(4, 2));
		List<IRegion> excludes = w.getExcludes();
		assertEquals(2, excludes.size());
		assertEquals(new Region(0, 2), excludes.get(0));
		assertEquals(new Region(4, 2), excludes.get(1));
	}

	public void testExcludes5() {
		ExcludeRegionList w = new ExcludeRegionList();
		w.excludeRegion(new Region(0, 2));
		w.excludeRegion(new Region(4, 2));
		final Region r08 = new Region(0, 8);
		w.excludeRegion(r08);
		List<IRegion> excludes = w.getExcludes();
		assertEquals(1, excludes.size());
		assertEquals(r08, excludes.get(0));
		assertSame(r08, excludes.get(0));
	}

	public void testExcludes6() {
		ExcludeRegionList w = new ExcludeRegionList();
		final Region r = new Region(0, 4);
		w.excludeRegion(r);
		w.excludeRegion(new Region(1, 1));
		List<IRegion> excludes = w.getExcludes();
		assertEquals(1, excludes.size());
		assertEquals(r, excludes.get(0));
		assertSame(r, excludes.get(0));
	}

	public void testExcludes7() {
		ExcludeRegionList w = new ExcludeRegionList();
		w.excludeRegion(new Region(2, 2));
		IRegion[] ranges;
		ranges = w.selectValidRanges(0, 3);
		assertEquals(1, ranges.length);
		assertEquals(new Region(0, 2), ranges[0]);
		ranges = w.selectValidRanges(0, 5);
		assertEquals(2, ranges.length);
		assertEquals(new Region(0, 2), ranges[0]);
		assertEquals(new Region(4, 1), ranges[1]);
		ranges = w.selectValidRanges(2, 2);
		assertEquals(0, ranges.length);
	}
}
