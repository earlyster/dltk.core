package org.eclipse.dltk.core.caching;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.IFileHandle;

public abstract class AbstractContentCache implements IContentCache {
	public synchronized String getCacheEntryAttributeString(IFileHandle handle,
			String attribute) {
		InputStream stream = getCacheEntryAttribute(handle, attribute);
		if (stream != null) {
			try {
				char[] chars = Util.getInputStreamAsCharArray(stream, -1, null);
				stream.close();
				return new String(chars);
			} catch (IOException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public boolean setCacheEntryAttribute(IFileHandle handle, String attribute,
			String value) {
		OutputStream outputStream = getCacheEntryAttributeOutputStream(handle,
				attribute);
		if (outputStream != null) {
			try {
				outputStream.write(value.getBytes());
				outputStream.close();
			} catch (IOException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
				return false;
			}
			return true;
		}
		return false;
	}

}