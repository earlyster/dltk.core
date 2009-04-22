package org.eclipse.dltk.internal.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.internal.core.util.Util;

public class PersistentTimeStampMap {
	private Hashtable timestamps = null;
	private File file;

	public PersistentTimeStampMap(File timestampsFile) {
		this.file = timestampsFile;
	}

	public void save() throws CoreException {
		if (timestamps != null) {
			saveTimeStamps(timestamps, file);
		}
	}

	public Hashtable getTimestamps() {
		if (timestamps == null) {
			timestamps = readTimeStamps(this.file);
		}
		return timestamps;
	}

	private Hashtable readTimeStamps(File timestampsFile) {
		Hashtable timeStamps = new Hashtable();
		DataInputStream in = null;
		try {
			in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(timestampsFile)));
			int size = in.readInt();
			while (size-- > 0) {
				String key = in.readUTF();
				long timestamp = in.readLong();
				timeStamps.put(Path.fromPortableString(key),
						new Long(timestamp));
			}
		} catch (IOException e) {
			if (timestampsFile.exists())
				Util.log(e, "Unable to read external time stamps"); //$NON-NLS-1$
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// nothing we can do: ignore
				}
			}
		}
		return timeStamps;
	}

	private void saveTimeStamps(Hashtable stamps, File timestamps)
			throws CoreException {
		if (stamps == null)
			return;
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(timestamps)));
			out.writeInt(stamps.size());
			Iterator keys = stamps.keySet().iterator();
			while (keys.hasNext()) {
				IPath key = (IPath) keys.next();
				out.writeUTF(key.toPortableString());
				Long timestamp = (Long) stamps.get(key);
				out.writeLong(timestamp.longValue());
			}
		} catch (IOException e) {
			IStatus status = new Status(IStatus.ERROR, DLTKCore.PLUGIN_ID,
					IStatus.ERROR, "Problems while saving timestamps", e); //$NON-NLS-1$
			throw new CoreException(status);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// nothing we can do: ignore
				}
			}
		}
	}
}
