package org.sfm.csv.parser;

import java.io.IOException;
import java.io.Reader;

public final class CharBuffer {


	private char[] buffer;
	private int bufferLength;

	private int mark;

	public CharBuffer(final int bufferSize) {
		this.buffer = new char[bufferSize];
	}
	
	public void mark(int index) {
		this.mark = index;
	}
	
	public boolean fillBuffer(Reader reader) throws IOException {
		int length = reader.read(buffer, bufferLength, buffer.length- bufferLength);
		if (length != -1) {
			bufferLength += length;
			return true;
		} else {
			return false;
		}
	}


	public int shiftBufferToMark() {
		// shift buffer consumer data
		int newLength = Math.max(bufferLength - mark, 0);

		// if buffer tight double the size
		if (newLength <= (bufferLength >> 1)) {
			System.arraycopy(buffer, mark, buffer, 0, newLength);
		} else {
			// double buffer size
			char[] newbuffer = new char[buffer.length << 1];
			System.arraycopy(buffer, mark, newbuffer, 0, newLength);
			buffer = newbuffer;
		}
		bufferLength = newLength;

		int m = mark;
		mark = 0;
		return m;
	}

	public char[] getCharBuffer() {
		return buffer;
	}

	public boolean isAllConsumed(int bufferIndex) {
		return mark >= bufferIndex -1 ;
	}

	public int getMark() {
		return mark;
	}

	public int getLengthFromMark(int bufferIndex) {
		return bufferIndex - mark;
	}

	public char getChar(int bufferIndex) {
		return buffer[bufferIndex];
	}

	public boolean hasContent(int bufferIndex) {
		return bufferIndex < bufferLength;
	}


	public int getBufferLength() {
		return bufferLength;
	}
}
