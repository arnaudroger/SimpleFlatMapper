package org.sfm.csv.parser;

import java.io.IOException;
import java.io.Reader;

public final class CharSequenceCharBuffer extends CharBuffer {

	public CharSequenceCharBuffer(final CharSequence charSequence)
			throws IOException {
		super(toCharArray(charSequence), charSequence.length());
	}

	private static char[] toCharArray(CharSequence charSequence) {
		if (charSequence instanceof String) {
			return ((String)charSequence).toCharArray();
		} else {
			char[] buffer = new char[charSequence.length()];for(int i = 0; i < buffer.length; i++) {
				buffer[i] = charSequence.charAt(i);
			}
			return buffer;
		}
	}

	public boolean fillBuffer() throws IOException {
		return false;
	}

	public int shiftBufferToMark() throws BufferOverflowException {
		return 0;
	}

	public char[] getCharBuffer() {
		return buffer;
	}

	public int getMark() {
		return mark;
	}

	public char getChar(int bufferIndex) {
		return buffer[bufferIndex];
	}

	public int getBufferSize() {
		return bufferSize;
	}
}
