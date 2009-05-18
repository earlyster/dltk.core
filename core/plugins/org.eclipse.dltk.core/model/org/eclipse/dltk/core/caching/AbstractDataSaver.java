package org.eclipse.dltk.core.caching;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.internal.core.util.Util;

public abstract class AbstractDataSaver {
	protected OutputStream stream;
	protected DataOutputStream out;
	private List<String> stringIndex = new ArrayList<String>();
	private ByteArrayOutputStream bout;

	public AbstractDataSaver(OutputStream stream) {
		this.stream = stream;
		bout = new ByteArrayOutputStream();
		this.out = new DataOutputStream(bout);
	}

	protected void writeString(String value) throws IOException {
		if (value == null) {
			out.writeByte(0);
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

	protected void storeStringIndex() throws IOException {
		this.out.flush();
		this.out = new DataOutputStream(this.stream);

		// Store strings
		out.writeInt(stringIndex.size());
		for (String s : this.stringIndex) {
			Util.writeUTF(out, s.toCharArray());
		}
		org.eclipse.dltk.compiler.util.Util.copy(new ByteArrayInputStream(bout
				.toByteArray()), this.out);
	}
}
