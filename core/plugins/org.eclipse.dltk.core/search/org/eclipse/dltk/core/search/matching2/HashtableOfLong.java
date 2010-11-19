/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.search.matching2;

/**
 * Hashtable for non-zero long keys.
 */
final class HashtableOfLong<E> {
	// to avoid using Enumerations, walk the individual tables skipping nulls
	private long[] keyTable;
	private Object[] valueTable;

	private int elementSize; // number of elements in the table
	private int threshold;

	public HashtableOfLong() {
		this(13);
	}

	public HashtableOfLong(int size) {
		this.elementSize = 0;
		this.threshold = size;
		// size represents the expected number of elements
		int extraRoom = (int) (size * 1.75f);
		if (this.threshold == extraRoom)
			extraRoom++;
		this.keyTable = new long[extraRoom];
		this.valueTable = new Object[extraRoom];
	}

	public boolean containsKey(long key) {
		int length = keyTable.length, index = ((int) (key >>> 32)) % length;
		long currentKey;
		while ((currentKey = keyTable[index]) != 0) {
			if (currentKey == key)
				return true;
			if (++index == length) {
				index = 0;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public E get(long key) {
		int length = keyTable.length, index = ((int) (key >>> 32)) % length;
		long currentKey;
		while ((currentKey = keyTable[index]) != 0) {
			if (currentKey == key)
				return (E) valueTable[index];
			if (++index == length) {
				index = 0;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public E put(long key, E value) {
		int length = keyTable.length, index = ((int) (key >>> 32)) % length;
		long currentKey;
		while ((currentKey = keyTable[index]) != 0) {
			if (currentKey == key)
				return (E) (valueTable[index] = value);
			if (++index == length) {
				index = 0;
			}
		}
		keyTable[index] = key;
		valueTable[index] = value;

		// assumes the threshold is never equal to the size of the table
		if (++elementSize > threshold)
			rehash();
		return value;
	}

	@SuppressWarnings("unchecked")
	private void rehash() {
		HashtableOfLong<E> newHashtable = new HashtableOfLong<E>(
				elementSize * 2);
		// double the number of expected elements
		long currentKey;
		for (int i = keyTable.length; --i >= 0;)
			if ((currentKey = keyTable[i]) != 0)
				newHashtable.put(currentKey, (E) valueTable[i]);

		this.keyTable = newHashtable.keyTable;
		this.valueTable = newHashtable.valueTable;
		this.threshold = newHashtable.threshold;
	}

	public int size() {
		return elementSize;
	}

	@Override
	public String toString() {
		String s = ""; //$NON-NLS-1$
		Object object;
		for (int i = 0, length = valueTable.length; i < length; i++)
			if ((object = valueTable[i]) != null)
				s += keyTable[i] + " -> " + object.toString() + "\n"; //$NON-NLS-2$ //$NON-NLS-1$
		return s;
	}

	public void clear() {
		if (elementSize > 0) {
			for (int i = valueTable.length; --i >= 0;) {
				valueTable[i] = null;
				keyTable[i] = 0;
			}
			elementSize = 0;
		}
	}
}
