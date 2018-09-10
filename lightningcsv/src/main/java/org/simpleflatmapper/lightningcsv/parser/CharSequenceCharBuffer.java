package org.simpleflatmapper.lightningcsv.parser;

public final class CharSequenceCharBuffer extends CharBuffer {

	public CharSequenceCharBuffer(final String str) {
		super(str.toCharArray(), str.length());
	}

	public CharSequenceCharBuffer(final CharSequence str) {
		super(toCharArray(str), str.length());
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public final boolean shiftAndRead(int shiftFrom) {
		throw new UnsupportedOperationException();
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
