package org.sfm.csv.parser;


import java.io.IOException;

/**
 * Consume the charBuffer.
 */
public final class StandardCsvCharConsumer extends CsvCharConsumer {

	private static final int NOTHING = 8;
	private static final int IN_CR = 4;
	private static final int IN_QUOTE = 1;
	private static final int NONE = 0;
	private static final int TURN_OFF_NOTHING = ~NOTHING;
	private static final int TURN_OFF_IN_CR_MASK = ~IN_CR;

	private static final char QUOTE_CHAR = '"';

	private final CharBuffer csvBuffer;
	private int _currentIndex;
	private int _currentState = NONE;

	public StandardCsvCharConsumer(CharBuffer csvBuffer) {
		this.csvBuffer = csvBuffer;
	}

	@Override
	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferSize();
		char[] chars = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		int currentState = _currentState;
		while(currentIndex  < bufferLength) {
			char character = chars[currentIndex];
			currentState = consumeOneChar(cellConsumer, currentIndex, currentState, character);
			currentIndex++;
		}
		_currentState = currentState;
		_currentIndex = currentIndex;
	}

	private int consumeOneChar(CellConsumer cellConsumer, int currentIndex, int currentState, char character) {

		if (character == QUOTE_CHAR) {
			return (currentState ^ IN_QUOTE) & TURN_OFF_IN_CR_MASK;
		}

		if ((currentState &  IN_QUOTE) == 0) {
			if (character == ',') {
				return newCell(currentIndex, cellConsumer);
			} else if (character == '\n') {
				return handleEndOfLineLF(currentIndex, currentState, cellConsumer);
			} else if (character == '\r') {
				return handleEndOfLineCR(currentIndex, cellConsumer);
			}
		}

		return currentState & TURN_OFF_IN_CR_MASK;
	}

	private int handleEndOfLineLF(int currentIndex, int currentState, CellConsumer cellConsumer) {
		if ((currentState &  IN_CR) != 0) {
			csvBuffer.mark(currentIndex + 1);
			return currentState & TURN_OFF_IN_CR_MASK;
        } else {
			endOfRow(currentIndex, cellConsumer);
			return NONE;
        }
	}

	@Override
	public boolean consumeToNextRow(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferSize();
		char[] chars = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		int currentState = _currentState;
		for(;currentIndex  < bufferLength; currentIndex ++) {
			char character = chars[currentIndex];

			if ((currentState &  IN_QUOTE) == 0) {
				if (character == ',') {
					currentState = newCell(currentIndex, cellConsumer);
					continue;
				} else if (character == '\n') {
					if ((currentState &  IN_CR) == 0) {
						endOfRow(currentIndex, cellConsumer);
						_currentState = NONE;
						_currentIndex = currentIndex + 1;
						return true;
					} else {
						csvBuffer.mark(currentIndex + 1);
					}
				} else if (character == '\r') {
					endOfRow(currentIndex, cellConsumer);
					_currentState = IN_CR;
					_currentIndex = currentIndex + 1;
					return true;
				}
			}

			if (character == QUOTE_CHAR) {
				currentState = (currentState ^ IN_QUOTE);
			}

			currentState &= TURN_OFF_IN_CR_MASK;
		}
		_currentState = currentState;
		_currentIndex = currentIndex;
		return false;
	}


	private int handleEndOfLineCR(int currentIndex, CellConsumer cellConsumer) {
		endOfRow(currentIndex, cellConsumer);
		return IN_CR;
	}

	private void endOfRow(int currentIndex, CellConsumer cellConsumer) {
		newCell(currentIndex, cellConsumer);
		cellConsumer.endOfRow();
	}

	private int newCell(int end, final CellConsumer cellConsumer) {
		char[] charBuffer = csvBuffer.getCharBuffer();
		int strStart = csvBuffer.getMark();
		int strEnd = end;
		if (charBuffer[strStart] == QUOTE_CHAR) {
			strStart ++;
			strEnd = unescape(charBuffer, strStart, end);
		}
		cellConsumer.newCell(charBuffer, strStart, strEnd - strStart);
		csvBuffer.mark(end + 1);
		return NONE;
	}

	private int unescape(final char[] chars, final int offset, final int end) {
		for(int i = offset; i < end - 1; i ++) {
			if (chars[i] == QUOTE_CHAR) {
				return removeEscapeChars(chars, end, i);
			}
		}

		if (QUOTE_CHAR == chars[end - 1]) {
			return end - 1;
		}

		return end;
	}

	private int removeEscapeChars(final char[] chars, final int end, final int firstEscapeChar) {
		int j = firstEscapeChar;
		boolean escaped = true;
		for(int i = firstEscapeChar + 1;i < end; i++) {
            escaped = chars[i] == QUOTE_CHAR  && ! escaped;
            if (!escaped) {
                chars[j++] = chars[i];
            }
        }
		return j;
	}

	@Override
	public final void finish(CellConsumer cellConsumer) {
		if ( _currentIndex > csvBuffer.getMark()) {
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
