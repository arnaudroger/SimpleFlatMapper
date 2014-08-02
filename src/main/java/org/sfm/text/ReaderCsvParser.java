package org.sfm.text;

import java.io.IOException;
import java.io.Reader;

public final class ReaderCsvParser {
	
	static enum State {
		IN_QUOTE, QUOTE, NONE
	}
	private char[] buffer;

	int bufferLength;
	State currentState = State.NONE;
	long currentRow = 0, currentCol = 0;

	int currentStart =0;
	int bufferOffset = 0;

	
	public ReaderCsvParser(int bufferSize) {
		buffer = new char[bufferSize];
	}
	
	/**
	 * parse cvs from input stream assumes character encoding for '"', ',' and '\n' match utf8
	 * @param is
	 * @param handler
	 * @return
	 * @throws IOException
	 */
	public void parse(Reader is, CharsCellHandler handler) throws IOException {
		char c = 0;
		
		while((bufferLength = is.read(buffer, bufferOffset, buffer.length - bufferOffset)) != -1) {
			c = consumeBytes(handler);
		}
		
		if (bufferOffset > 0 || c == ',' ) {
			handler.cell(currentRow, currentCol, buffer, 0, bufferOffset);
		}
	}


	private char consumeBytes(CharsCellHandler handler) {
		bufferLength += bufferOffset;
		
		char c = 0;
		for(int i = 0; i < bufferLength; i++) {
			c = buffer[i];
			handleByte(handler, c, i);
		}
		
		shiftBuffer();
		
		return c;
	}

	private void handleByte(CharsCellHandler handler, char c, int i) {
		switch(c) {
		case '"':
			if (currentStart == i) {
				currentState = State.IN_QUOTE;
			} else if (currentState == State.IN_QUOTE) {
				currentState = State.QUOTE;
			} else {
				if (currentState == State.QUOTE) {
					currentState = State.IN_QUOTE;
				}
			}
			break;
		case ',':
		case '\n':
			if (currentState != State.IN_QUOTE) {
				handler.cell(currentRow, currentCol, buffer, currentStart, i - currentStart);
				currentStart = i  + 1;
				currentState = State.NONE;
				if (c == ',') {
					currentCol ++;
				} else {
					currentCol = 0;
					currentRow ++;
				}
			}
			break;
		}
	}

	private void shiftBuffer() {
		// shift buffer consumer data
		bufferOffset = bufferLength - currentStart;
		
		// if buffer tight double the size
		if (bufferOffset > bufferLength >> 1) {
			// double buffer size
			char[] newbuffer = new char[buffer.length << 1];
			System.arraycopy(buffer, currentStart, newbuffer, 0, bufferOffset);
			buffer = newbuffer;
		} else {
			System.arraycopy(buffer, currentStart, buffer, 0, bufferOffset);
		}
		currentStart = 0;
	}
}
