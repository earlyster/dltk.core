package org.eclipse.dltk.core.caching;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.internal.core.util.Util;

public class AbstractDataLoader {
	protected InputStream stream;
	protected DataInputStream in;
	private List<String> stringIndex = new ArrayList<String>();

	public AbstractDataLoader(InputStream stream) {
		this.stream = stream;
		this.in = new DataInputStream(this.stream);
	}

	protected void readStrings() throws IOException {
		int stringCount = in.readInt();
		for (int i = 0; i < stringCount; ++i) {
			stringIndex.add(new String(Util.readUTF(in)));
		}
	}

	protected int readNum(int id1, int id2) throws IOException {
		byte b = in.readByte();
		if (b == id1) {
			return in.readByte();
		} else if (b == id2) {
			return in.readInt();
		}
		return 0;
	}

	protected String readString() throws IOException {
		byte b = in.readByte();
		if (b == 0) {
			return null;
		}
		if (b == 1) {
			int pos = in.readByte();
			return stringIndex.get(pos);
		} else if (b == 2) {
			int pos = in.readInt();
			return stringIndex.get(pos);
		} else if (b == 3) {
			int basePos = readNum(1, 2);
			int pos = readNum(1, 2);
			int len = readNum(1, 2);
			String base = stringIndex.get(basePos);
			String str = base.substring(pos, pos + len);
			return str;
		} else if (b == 4) {
			int count = in.readInt();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < count; i++) {
				buffer.append(readString());
			}
			return buffer.toString();
		}
		return "";
	}
}
