package org.sfm.text;

import java.io.IOException;
import java.io.InputStream;

public final class InputStreamCsvParser {
	
	private static final byte CARRIAGE_RETURN = '\n';
	private static final byte COMMA = ',';
	private static final byte QUOTES = '"';

	static enum State {
		IN_QUOTE, QUOTE, NONE
	}
	private byte[] buffer;

	int bufferLength;
	State currentState = State.NONE;
	long currentRow = 0, currentCol = 0;

	int currentStart =0;
	int bufferOffset = 0;

	
	public InputStreamCsvParser(int bufferSize) {
		buffer = new byte[bufferSize];
	}
	
	/**
	 * parse cvs from input stream assumes character encoding for '"', ',' and '\n' match utf8
	 * @param is
	 * @param handler
	 * @return
	 * @throws IOException
	 */
	public void parse(InputStream is, BytesCellHandler handler) throws IOException {
		byte c = 0;
		
		while((bufferLength = is.read(buffer, bufferOffset, buffer.length - bufferOffset)) != -1) {
			c = consumeBytes(handler);
		}
		
		if (bufferOffset > 0 || c == ',' ) {
			handler.cell(currentRow, currentCol, buffer, 0, bufferOffset);
		}
	}


	private byte consumeBytes(BytesCellHandler handler) {
		bufferLength += bufferOffset;
		
		byte c = 0;
		for(int i = 0; i < bufferLength; i++) {
			c = buffer[i];
			handleByte(handler, c, i);
		}
		
		shiftBuffer();
		
		return c;
	}

	private void handleByte(BytesCellHandler handler, byte c, int i) {
		if (c ==  QUOTES) {
			if (currentStart == i) {
				currentState = State.IN_QUOTE;
			} else if (currentState == State.IN_QUOTE) {
				currentState = State.QUOTE;
			} else {
				if (currentState == State.QUOTE) {
					currentState = State.IN_QUOTE;
				}
			}
		} else if (c == COMMA) {
			if (currentState != State.IN_QUOTE) {
				handler.cell(currentRow, currentCol, buffer, currentStart, i - currentStart);
				currentStart = i  + 1;
				currentState = State.NONE;
				currentCol ++;
			}
		} else if (c == CARRIAGE_RETURN) {
			if (currentState != State.IN_QUOTE) {
				handler.cell(currentRow, currentCol, buffer, currentStart, i - currentStart);
				currentStart = i  + 1;
				currentState = State.NONE;
				currentCol = 0;
				currentRow ++;
			}
		}
	}

	private void shiftBuffer() {
		// shift buffer consumer data
		bufferOffset = bufferLength - currentStart;
		
		// if buffer tight double the size
		if (bufferOffset > bufferLength >> 1) {
			// double buffer size
			byte[] newbuffer = new byte[buffer.length << 1];
			System.arraycopy(buffer, currentStart, newbuffer, 0, bufferOffset);
			buffer = newbuffer;
		} else {
			System.arraycopy(buffer, currentStart, buffer, 0, bufferOffset);
		}
		currentStart = 0;
	}
}
