package org.sfm.csv.parser;


import java.io.IOException;

/**
 * Consume the charBuffer.
 */
public final class ConfigurableCsvCharConsumer extends CsvCharConsumer {

	private static final int IN_CR = 2;
	private static final int ESCAPED = 1;
	private static final int NONE = 0;
	private static final int TURN_OFF_IN_CR_MASK = ~IN_CR;

	private final char separatorChar;
	private final char escapeChar;

	private final CharBuffer csvBuffer;
	private int _currentIndex;
	private int _currentState = NONE;

	public ConfigurableCsvCharConsumer(CharBuffer csvBuffer, char separatorChar, char escapeChar) {
		this.separatorChar = separatorChar;
		this.escapeChar = escapeChar;
		this.csvBuffer = csvBuffer;
	}

	@Override
	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.bufferSize;
		char[] chars = csvBuffer.buffer;
		int currentIndex = _currentIndex;
		int currentState = _currentState;
		for(;currentIndex  < bufferLength; currentIndex++) {
			char character = chars[currentIndex];

			if (character == escapeChar) {
				currentState =  (currentState ^ ESCAPED);
			} else if ((currentState & ESCAPED) == 0) {
                if (character == separatorChar) {
                    newCell(currentIndex, cellConsumer);
					currentState = NONE;
                    continue;
                } else if (character == '\n') {
                    if ((currentState &  IN_CR) == 0) {
                        endOfRow(currentIndex, cellConsumer);
						currentState = NONE;
						continue;
                    } else {
                        csvBuffer.mark = currentIndex + 1;
                    }
                } else if (character == '\r') {
                    endOfRow(currentIndex, cellConsumer);
                    currentState = IN_CR;
                    continue;
                }
			}

			currentState &= TURN_OFF_IN_CR_MASK;
		}
		_currentState = currentState;
		_currentIndex = currentIndex;
	}

	@Override
	public boolean consumeToNextRow(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferSize();
		char[] chars = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		int currentState = _currentState;
		for(;currentIndex  < bufferLength; currentIndex ++) {
			char character = chars[currentIndex];

			if (character == escapeChar) {
				currentState = (currentState ^ ESCAPED);
			} else if ((currentState & ESCAPED) == 0) {
				if (character == separatorChar) {
					newCell(currentIndex, cellConsumer);
					currentState = NONE;
					continue;
				} else if (character == '\n') {
					if ((currentState &  IN_CR) == 0) {
						endOfRow(currentIndex, cellConsumer);
						_currentState = NONE;
						_currentIndex = currentIndex + 1;
						return true;
					} else {
						csvBuffer.mark = currentIndex + 1;
					}
				} else if (character == '\r') {
					endOfRow(currentIndex, cellConsumer);
					_currentState = IN_CR;
					_currentIndex = currentIndex + 1;
					return true;
				}
			}

			currentState &= TURN_OFF_IN_CR_MASK;
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
		int strEnd = end;
		if (strStart < strEnd && charBuffer[strStart] == escapeChar) {
			strStart ++;
			strEnd = unescape(charBuffer, strStart, strEnd);
		}
		cellConsumer.newCell(charBuffer, strStart, strEnd - strStart);
		csvBuffer.mark = (end + 1);
	}

	private int unescape(final char[] chars, final int offset, final int end) {
		for(int i = offset; i < end - 1; i ++) {
			if (chars[i] == escapeChar) {
				return removeEscapeChars(chars, end, i);
			}
		}

		if (offset < end && escapeChar == chars[end - 1]) {
			return end - 1;
		}

		return end;
	}

	private int removeEscapeChars(final char[] chars, final int end, final int firstEscapeChar) {
		int j = firstEscapeChar;
		boolean escaped = true;
		for(int i = firstEscapeChar + 1;i < end; i++) {
            escaped = chars[i] == escapeChar  && ! escaped;
            if (!escaped) {
                chars[j++] = chars[i];
            }
        }
		return j;
	}

	@Override
	public final void finish(CellConsumer cellConsumer) {
		if ( _currentIndex > csvBuffer.mark || (_currentIndex > 0 && csvBuffer.buffer[_currentIndex - 1] == separatorChar)) {
			newCell(_currentIndex, cellConsumer);
		}
		cellConsumer.end();
	}

	@Override
	public final boolean refillBuffer() throws IOException {
		_currentIndex -= csvBuffer.shiftBufferToMark();
		return csvBuffer.fillBuffer();
	}
}
