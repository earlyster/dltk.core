package org.eclipse.dltk.core.caching;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.internal.core.util.Util;

public abstract class AbstractDataSaver {
	private static final int MAX_STR = 65500;
	protected DataOutputStream out;
	private List<String> stringIndex = new ArrayList<String>();
	private final ByteArrayOutputStream data = new ByteArrayOutputStream();

	/**
	 * @since 2.0
	 */
	public AbstractDataSaver() {
		this.out = new DataOutputStream(data);
	}

	protected void writeString(String value) throws IOException {
		if (value == null) {
			out.writeByte(0);
			return;
		}
		if (value.length() > MAX_STR) {
			// Split string to two strings
			List<String> strs = new ArrayList<String>();
			int len = value.length();
			int pos = 0;
			while (len > 0) {
				if (len > MAX_STR) {
					len -= MAX_STR;
					strs.add(value.substring(pos, pos + MAX_STR));
					pos += MAX_STR;
				} else {
					strs.add(value.substring(pos, pos + len));
					len = 0;
				}
			}
			out.writeByte(4);
			out.writeInt(strs.size());
			for (String part : strs) {
				writeString(part);
			}
			return;
		}

		int indexOf = stringIndex.indexOf(value);
		if (indexOf != -1) {
			outNum(indexOf, 1, 2);
			return;
		} else {
			// Try to find part of word
			if (value.length() > 6) {
				for (String base : stringIndex) {
					if (base.contains(value)) {
						// Part of string
						int pos = base.indexOf(value);
						out.writeByte(3);
						int basePos = stringIndex.indexOf(base);
						outNum(basePos, 1, 2);
						outNum(pos, 1, 2);
						outNum(value.length(), 1, 2);
						return;
					}
				}
			}
			stringIndex.add(value);
			outNum(stringIndex.size() - 1, 1, 2);
			return;
		}
	}

	protected void outNum(int indexOf, int id1, int id2) throws IOException {
		if (indexOf <= Byte.MAX_VALUE) {
			out.writeByte(id1);
			out.writeByte(indexOf);
		} else if (indexOf > Byte.MAX_VALUE) {
			out.writeByte(id2);
			out.writeInt(indexOf);
		}
	}

	/**
	 * @since 2.0
	 */
	protected void storeStringIndex(OutputStream stream) throws IOException {
		final DataOutputStream indexOut = new DataOutputStream(stream);
		// Store strings
		indexOut.writeInt(stringIndex.size());
		for (String s : this.stringIndex) {
			Util.writeUTF(indexOut, s.toCharArray());
		}
		indexOut.flush();
	}

	/**
	 * @param stream
	 * @throws IOException
	 * @since 2.0
	 */
	protected void saveDataTo(OutputStream stream) throws IOException {
		data.writeTo(stream);
	}

	/**
	 * @since 2.0
	 */
	protected void saveTo(OutputStream stream) throws IOException {
		storeStringIndex(stream);
		data.writeTo(stream);
	}

}
