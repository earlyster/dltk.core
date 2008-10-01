package org.eclipse.dltk.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;
import org.eclipse.dltk.utils.CharArraySequence;

public abstract class ScriptContentDescriber implements ITextContentDescriber {
	public QualifiedName[] getSupportedOptions() {
		return new QualifiedName[] { DLTKContentTypeManager.DLTK_VALID };
	}

	private final static int BUFFER_LENGTH = 2 * 1024;
	private final static int HEADER_LENGTH = 4 * 1024;
	private final static int FOOTER_LENGTH = 4 * 1024;

	private static boolean checkHeader(File file, Pattern[] headerPatterns,
			Pattern[] footerPatterns) throws FileNotFoundException, IOException {
		FileInputStream reader = null;
		try {
			reader = new FileInputStream(file);
			byte buf[] = new byte[BUFFER_LENGTH + 1];
			int res = reader.read(buf);
			if (res == -1 || res == 0) {
				return false;
			}

			String header = new String(buf);

			if (checkBufferForPatterns(header, headerPatterns)) {
				return true;
			}
			if (file.length() < BUFFER_LENGTH && footerPatterns != null) {
				if (checkBufferForPatterns(header, footerPatterns)) {
					return true;
				}
			}

			return false;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private static boolean checkFooter(File file, Pattern[] footerPatterns)
			throws FileNotFoundException, IOException {
		RandomAccessFile raFile = new RandomAccessFile(file, "r"); //$NON-NLS-1$
		try {
			long len = BUFFER_LENGTH;
			long fileSize = raFile.length();
			long offset = fileSize - len;
			if (offset < 0) {
				offset = 0;
			}
			raFile.seek(offset);
			byte buf[] = new byte[BUFFER_LENGTH + 1];
			int code = raFile.read(buf);
			if (code != -1) {
				String content = new String(buf, 0, code);
				if (checkBufferForPatterns(content, footerPatterns)) {
					return true;
				}
			}
			return false;
		} finally {
			raFile.close();
		}

	}

	private static boolean checkBufferForPatterns(CharSequence header,
			Pattern[] patterns) {
		if (patterns == null) {
			return false;
		}
		for (int i = 0; i < patterns.length; i++) {
			Matcher m = patterns[i].matcher(header);
			if (m.find()) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkPatterns(File file, Pattern[] headerPatterns,
			Pattern[] footerPatterns) {
		try {
			if (checkHeader(file, headerPatterns, footerPatterns)) {
				return true;
			}
			if (footerPatterns != null && file.length() > BUFFER_LENGTH
					&& checkFooter(file, footerPatterns)) {
				return true;
			}
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	/**
	 * Reads the specified number of bytes from the specified reader. Calls
	 * {@link Reader#read(char[], int, int)} multiple times until EOF of the
	 * specified number of bytes is read. Also catches {@link IOException} and
	 * return -1 on it.
	 * 
	 * @param reader
	 * @param bufffer
	 * @param offset
	 * @param len
	 * @return
	 */
	private static int read(Reader reader, char[] bufffer, int offset, int len) {
		try {
			int count = 0;
			while (len > 0) {
				final int result = reader.read(bufffer, offset, len);
				if (result > 0) {
					offset += result;
					len -= result;
					count += result;
				} else if (result < 0) {
					if (count == 0) {
						return result;
					} else {
						break;
					}
				}
			}
			return count;
		} catch (IOException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace(); // ignore
			}
			return -1;
		}
	}

	public static boolean checkPatterns(Reader reader,
			Pattern[] headerPatterns, Pattern[] footerPatterns) {
		/*
		 * There is no need to use BufferedReader here since a) we read blocks,
		 * b) implementation provided by eclipse.core already do buffering.
		 */
		final int bufferSize = Math.max(HEADER_LENGTH, FOOTER_LENGTH);
		char[] buffer = new char[bufferSize];
		int len = read(reader, buffer, 0, HEADER_LENGTH);
		if (len > 0) {
			if (headerPatterns != null && headerPatterns.length > 0) {
				if (checkBufferForPatterns(new CharArraySequence(buffer, len),
						headerPatterns)) {
					return true;
				}
			}
		}
		if (footerPatterns != null && footerPatterns.length > 0) {
			char[] prevBuffer = new char[bufferSize];
			int prevLen = 0;
			for (;;) {
				final char[] tempBuffer = buffer;
				buffer = prevBuffer;
				prevBuffer = tempBuffer;
				//
				final int savedLen = prevLen;
				prevLen = len;
				len = savedLen;
				//
				len = read(reader, buffer, 0, bufferSize);
				if (len <= 0) {
					final CharSequence footer;
					if (savedLen >= FOOTER_LENGTH) {
						footer = new CharArraySequence(buffer, savedLen
								- FOOTER_LENGTH, FOOTER_LENGTH);
					} else {
						int footerLength = prevLen;
						if (savedLen > 0) {
							footerLength += savedLen;
						}
						if (footerLength > FOOTER_LENGTH) {
							footerLength = FOOTER_LENGTH;
						}
						int prevOffset = Math.max(prevLen - footerLength, 0);
						int prevSize = Math.min(prevLen, footerLength);
						if (savedLen > 0) {
							System.arraycopy(buffer, 0, buffer, footerLength
									- savedLen, savedLen);
							prevOffset += savedLen;
							prevSize -= savedLen;
						}
						if (prevSize > 0) {
							System.arraycopy(prevBuffer, prevOffset, buffer, 0,
									prevSize);
						}
						footer = new CharArraySequence(buffer, footerLength);
					}
					if (checkBufferForPatterns(footer, footerPatterns)) {
						return true;
					}
				}
				break;
			}
		}
		return false;
	}

	public int describe(InputStream contents, IContentDescription description)
			throws IOException {
		return describe(new InputStreamReader(contents), description);
	}
}
