package org.eclipse.dltk.core.caching;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.mixin.IMixinRequestor;

public class MixinModelCollector extends AbstractDataSaver implements
		IMixinRequestor {

	public MixinModelCollector() {
		super(new ByteArrayOutputStream());
	}

	public void reportElement(ElementInfo info) {
		try {
			if (info != null && info.key != null) {
				writeString(info.key);
			}
		} catch (IOException e) {
		}
	}

	public byte[] getBytes() {
		try {
			stream.flush();
			storeStringIndex();
		} catch (IOException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		return ((ByteArrayOutputStream) stream).toByteArray();
	}
}
