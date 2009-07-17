package org.eclipse.dltk.internal.core;

import java.util.zip.ZipEntry;

import org.eclipse.dltk.core.IArchiveEntry;

public class ZipArchiveEntry implements IArchiveEntry {

	private ZipEntry zipEntry;

	public ZipArchiveEntry(ZipEntry zipEntry) {
		this.zipEntry = zipEntry;
	}

	public ZipEntry getZipEntry() {
		return zipEntry;
	}

	public void setZipEntry(ZipEntry zipEntry) {
		this.zipEntry = zipEntry;
	}

	public String getName() {
		return zipEntry.getName();
	}

	public boolean isDirectory() {
		return zipEntry.isDirectory();
	}

	public long getSize() {
		return zipEntry.getSize();
	}
}
