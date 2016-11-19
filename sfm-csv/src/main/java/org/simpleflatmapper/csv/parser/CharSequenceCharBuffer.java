package org.simpleflatmapper.csv.parser;

import java.io.IOException;
import java.io.Reader;

public final class CharSequenceCharBuffer extends CharBuffer {

	public CharSequenceCharBuffer(final String str)
			throws IOException {
		super(str.toCharArray(), str.length());
	}

	public CharSequenceCharBuffer(final CharSequence str)
			throws IOException {
		super(toCharArray(str), str.length());
	}

	@Override
	public final boolean next() throws IOException {
		return false;
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
}
