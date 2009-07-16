package org.eclipse.dltk.internal.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.dltk.core.Archive;
import org.eclipse.dltk.core.ArchiveEntry;

public class ZipArchiveFile implements Archive {

	private ZipFile zipFile;
	public ZipArchiveFile(File file) throws ZipException, IOException {
		zipFile = new ZipFile(file);
	}

	public ZipArchiveFile(String zipName) throws IOException {
		zipFile = new ZipFile(zipName);
	}

	public InputStream getInputStream(ArchiveEntry entry) throws IOException {
		ZipArchiveEntry zipArchiveEntry = (ZipArchiveEntry) entry;
		return zipFile.getInputStream(zipArchiveEntry.getZipEntry());
	}

	public ArchiveEntry getArchiveEntry(String name) {
		return new ZipArchiveEntry(zipFile.getEntry(name));
	}

	public Enumeration<? extends ArchiveEntry> getArchiveEntries() {
		final Enumeration<? extends ZipEntry> zipEnumeration = zipFile
				.entries();

		return new Enumeration<ArchiveEntry>() {

			public boolean hasMoreElements() {
				return zipEnumeration.hasMoreElements();
			}

			public ArchiveEntry nextElement() {
				return new ZipArchiveEntry(zipEnumeration.nextElement());
			}

		};
	}

	public void close() throws IOException {
		zipFile.close();
	}

	public String getName() {
		return zipFile.getName();
	}

}
