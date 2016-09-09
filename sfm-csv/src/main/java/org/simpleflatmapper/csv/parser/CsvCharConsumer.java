package org.simpleflatmapper.csv.parser;


import java.io.IOException;

/**
 * Consume the charBuffer.
 */
public abstract class CsvCharConsumer {

	private static final int LAST_CHAR_WAS_SEPARATOR = 4;
	private static final int LAST_CHAR_WAS_CR = 2;
	private static final int ESCAPED = 1;
	private static final int NONE = 0;
	private static final int TURN_OFF_LAST_CHAR_MASK = ~(LAST_CHAR_WAS_CR|LAST_CHAR_WAS_SEPARATOR);

	private final CharBuffer csvBuffer;
	private int _currentIndex;
	private int _currentState = NONE;

	public CsvCharConsumer(CharBuffer csvBuffer) {
		this.csvBuffer = csvBuffer;
	}

	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.bufferSize;
		char[] chars = csvBuffer.buffer;
		int currentIndex = _currentIndex;
		int currentState = _currentState;
		for(;currentIndex  < bufferLength; currentIndex++) {
			char character = chars[currentIndex];

			if (character == escapeChar()) {
				currentState = (currentState ^ ESCAPED);
			} else if ((currentState & ESCAPED) == 0) {
				if (character == separatorChar()) {
					newCell(currentIndex, cellConsumer);
					currentState = LAST_CHAR_WAS_SEPARATOR;
					continue;
				} else if (character == '\n') {
					if ((currentState & LAST_CHAR_WAS_CR) == 0) {
						endOfRow(currentIndex, cellConsumer);
						currentState = NONE;
						continue;
					} else {
						csvBuffer.mark = currentIndex + 1;
					}
				} else if (character == '\r') {
					endOfRow(currentIndex, cellConsumer);
					currentState = LAST_CHAR_WAS_CR;
					continue;
				}
			}

			currentState &= TURN_OFF_LAST_CHAR_MASK;
		}
		_currentState = currentState;
		_currentIndex = currentIndex;
	}

	public boolean consumeToNextRow(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferSize();
		char[] chars = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		int currentState = _currentState;
		for(;currentIndex  < bufferLength; currentIndex ++) {
			char character = chars[currentIndex];

			if (character == escapeChar()) {
				currentState =  (currentState ^ ESCAPED);
			} else if ((currentState & ESCAPED) == 0) {
				if (character == separatorChar()) {
					newCell(currentIndex, cellConsumer);
					currentState = NONE;
					continue;
				} else if (character == '\n') {
					if ((currentState & LAST_CHAR_WAS_CR) == 0) {
						endOfRow(currentIndex, cellConsumer);
						_currentState = NONE;
						_currentIndex = currentIndex + 1;
						return true;
					} else {
						csvBuffer.mark = currentIndex + 1;
					}
				} else if (character == '\r') {
					endOfRow(currentIndex, cellConsumer);
					_currentState = LAST_CHAR_WAS_CR;
					_currentIndex = currentIndex + 1;
					return true;
				}
			}

			currentState &= TURN_OFF_LAST_CHAR_MASK;
		}
		_currentState = currentState;
		_currentIndex = currentIndex;
		return false;
	}

	private void endOfRow(int currentIndex, CellConsumer cellConsumer) {
		newCell(currentIndex, cellConsumer);
		cellConsumer.endOfRow();
	}

	private void newCell(int end, final CellConsumer cellConsumer) {
		char[] charBuffer = csvBuffer.buffer;

		int strStart = csvBuffer.mark;
		int strEnd = strEnd(strStart, end, charBuffer);
		strStart = strStart(strStart, strEnd, charBuffer);

		if (strStart < strEnd && charBuffer[strStart] == escapeChar()) {
			strStart ++;
			strEnd = unescape(charBuffer, strStart, strEnd);
		}
		cellConsumer.newCell(charBuffer, strStart, strEnd - strStart);
		csvBuffer.mark = (end + 1);
	}


	private int unescape(final char[] chars, final int offset, final int end) {
		for(int i = offset; i < end - 1; i ++) {
			if (chars[i] == escapeChar()) {
				return removeEscapeChars(chars, end, i);
			}
		}

		if (offset < end && escapeChar() == chars[end - 1]) {
			return end - 1;
		}

		return end;
	}

	private int removeEscapeChars(final char[] chars, final int end, final int firstEscapeChar) {
		int j = firstEscapeChar;
		boolean escaped = true;
		for(int i = firstEscapeChar + 1;i < end; i++) {
            escaped = chars[i] == escapeChar() && ! escaped;
            if (!escaped) {
                chars[j++] = chars[i];
            }
        }
		return j;
	}

	public final void finish(CellConsumer cellConsumer) {
		if ( _currentIndex > csvBuffer.mark || (_currentState & LAST_CHAR_WAS_SEPARATOR) != 0) {
			newCell(_currentIndex, cellConsumer);
		}
		cellConsumer.end();
	}

	public final boolean refillBuffer() throws IOException {
		_currentIndex -= csvBuffer.shiftBufferToMark();
		return csvBuffer.fillBuffer();
	}

	protected abstract int strEnd(int strStart, int end, char[] charBuffer);
	protected abstract int strStart(int strStart, int strEnd, char[] charBuffer);
	protected abstract char escapeChar();
	protected abstract char separatorChar();
}
