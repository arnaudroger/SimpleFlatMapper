package org.sfm.csv.parser;

import java.io.IOException;
import java.io.Reader;

public final class CsvReader {

	public static final int STOP = 8;
	public static final int IN_QUOTE = 4;
	public static final int IN_CR = 2;
	public static final int QUOTE = 1;
	public static final int NONE = 0;

	public static final int TURN_OFF_IN_CR_MASK = ~IN_CR;
	public static final int ALL_QUOTES = QUOTE | IN_QUOTE;

	private char[] buffer;
	private final CharsCellHandler handler;
	private final Reader reader;
	private int bufferLength;
	
	private int currentState = NONE;

	private int currentStart = 0;
	private int bufferOffset = 0;
	private int bufferIndex;

	public CsvReader(final int bufferSize, final CharsCellHandler handler, final Reader reader) {
		this.buffer = new char[bufferSize];
		this.handler = handler;
		this.reader = reader;
	}

	/**
	 * parse cvs
	 * 
	 * @return
	 * @throws IOException
	 */
	public void parse()
			throws IOException {
		
		if(isStopped()) {
			unstop();
			consumeBytes();
			if (isStopped()) {
				return;
			}
			shiftBuffer();
		}
		
		while ( 
			 (bufferLength = reader.read(buffer, bufferOffset, buffer.length- bufferOffset)) != -1
				) {
			bufferLength += bufferOffset;
			bufferIndex = 0;
			consumeBytes();
			
			if (isStopped()) {
				return;
			}

			shiftBuffer();
		}
		
		if (bufferOffset > 0) {
			handler.newCell(buffer, 0, bufferOffset);
			bufferLength = 0;
			bufferOffset = 0;
		}
		handler.end();
	}

	private int unstop() {
		return currentState &= ~STOP;
	}

	private void consumeBytes() {
		while (bufferIndex < bufferLength && isNotStopped()) {
			handleChar(buffer[bufferIndex], bufferIndex);
			bufferIndex++;
		}
	}

	private boolean isNotStopped() {
		return (currentState & STOP) == 0;
	}
	private boolean isStopped() {
		return (currentState & STOP) != 0;
	}
	private void handleChar(final char c, final int bufferIndex) {
		switch (c) {
		case ',':
			newCellIfNotInQuote(bufferIndex);
			break;
		case '\n':
			handleEndOfLineLF(bufferIndex);
			break;
		case '\r':
			handleEndOfLineCR(bufferIndex);
			return;
		case '"':
			quote(bufferIndex);
			break;
		}
		currentState = currentState & TURN_OFF_IN_CR_MASK;
	}

	private void newCellIfNotInQuote(final int bufferIndex) {
		if (isInQuote())
			return;
		
		newCell(bufferIndex);
	}

	private void handleEndOfLineLF(final int bufferIndex) {
		if (isInQuote())
			return;

		if (currentState != IN_CR) {
			endOfRow(bufferIndex);
		} else {
			currentStart++;
		}
	}

	private boolean isInQuote() {
		return currentState == IN_QUOTE;
	}

	private void handleEndOfLineCR(final int bufferIndex) {
		if (isInQuote())
			return;

		endOfRow(bufferIndex);
		currentState = IN_CR;
	}

	private void endOfRow(int bufferIndex) {
		newCell(bufferIndex);
		if (!handler.endOfRow()) {
			currentState |= STOP;
		}
	}

	private void quote(final int i) {
		if (currentStart == i) {
			currentState = IN_QUOTE;
		} else {
			currentState = currentState ^ ALL_QUOTES;
		}
	}

	private void newCell(final int i) {
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
