package org.sfm.csv.parser;

import java.io.IOException;
import java.io.Reader;

public final class ReaderCsvParser {

	public static final int IN_QUOTE = 4;
	public static final int IN_CR = 2;
	public static final int QUOTE = 1;
	public static final int NONE = 0 ;
	
	public static final int TURN_OFF_IN_CR_MASK = ~IN_CR;
	public static final int ALL_QUOTES = QUOTE | IN_QUOTE;
	
	
	private char[] buffer;

	private int bufferLength;
	private int currentState = NONE;

	private int currentStart = 0;
	private int bufferOffset = 0;

	public ReaderCsvParser(final int bufferSize) {
		buffer = new char[bufferSize];
	}
	
	/**
	 * parse cvs from input stream assumes character encoding for '"', ',' and
	 * '\n' match utf8
	 * 
	 * @param is
	 * @param handler
	 * @return
	 * @throws IOException
	 */
	public void parse(final Reader is, final CharsCellHandler handler)
			throws IOException {
		while ((bufferLength = is.read(buffer, bufferOffset, buffer.length
				- bufferOffset)) != -1) {
			bufferLength += bufferOffset;

			if (!consumeBytes(handler)) {
				return;
			}
			shiftBuffer();
		}

		if (bufferOffset > 0) {
			handler.newCell(buffer, 0, bufferOffset);
		}
		handler.end();
	}

	private boolean consumeBytes(final CharsCellHandler handler) {
		for (int i = 0; i < bufferLength; i++) {
			char c = buffer[i];
			if (!handleChar(handler, c, i)) {
				return false;
			}
		}
		return true;
	}

	private boolean handleChar(final CharsCellHandler handler, final char c,
			final int i) {
		 if (c == ',') {
			if (currentState != IN_QUOTE) {
				newCell(handler, i);
			}
		} else if (c == '\n' || c == '\r') {
			if (currentState != IN_QUOTE ) {
				return handleEndOfLine(c, handler, i);
			}
		} else if (c == '"') {
			quote(i);
		}
		 
		currentState = currentState & TURN_OFF_IN_CR_MASK;
		 
		return true;
	}

	private boolean handleEndOfLine(final char c, final CharsCellHandler handler, final int i) {
		if (c == '\r') { 
			newCell(handler, i);
			currentState = IN_CR;
			return handler.endOfRow();
		}else if (currentState != IN_CR) {
			newCell(handler, i);
			return handler.endOfRow();
		} else {
			currentStart = i + 1;
			currentState = NONE;
			return true;
		}
	}

	public void quote(final int i) {
		if (currentStart == i) {
			currentState = IN_QUOTE;
		} else {
			currentState = currentState ^ ALL_QUOTES;
		}
	}

	public void newCell(final CharsCellHandler handler, final int i) {
		handler.newCell(buffer, currentStart, i - currentStart);
		currentStart = i + 1;
		currentState = NONE;
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
