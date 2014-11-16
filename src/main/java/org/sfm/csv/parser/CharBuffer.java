package org.sfm.csv.parser;

import java.io.IOException;
import java.io.Reader;

public final class CharBuffer {


	private char[] buffer;
	private int bufferLength;
	private int bufferIndex;
	
	private int mark;

	public CharBuffer(final int bufferSize) {
		this.buffer = new char[bufferSize];
	}
	
	public void mark() {
		this.mark = bufferIndex;
	}
	
	public boolean fillBuffer(Reader reader) throws IOException {
		
		shiftBufferToMark();
		
		int length = reader.read(buffer, bufferLength, buffer.length- bufferLength);
		if (length != -1) {
			bufferLength += length;
			return true;
		} else {
			return false;
		}
	}


	private void shiftBufferToMark() {
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
		bufferIndex = bufferIndex - mark;
		bufferLength = newLength;
		
		mark = 0;
	}

	public char[] getCharBuffer() {
		return buffer;
	}

	public boolean isAllConsumed() {
		return mark >= bufferIndex -1 ;
	}

	public int getMark() {
		return mark;
	}

	public int getLengthFromMark() {
		return bufferIndex - mark;
	}

	public char getNextChar() {
		return buffer[bufferIndex++];
	}

	public boolean hasContent() {
		return bufferIndex < bufferLength;
	}
}
