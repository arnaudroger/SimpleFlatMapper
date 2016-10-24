package org.simpleflatmapper.csv.parser;


import java.io.IOException;

/**
 * Consume the charBuffer.
 */
public final class CharConsumer {

	private static final int LAST_CHAR_WAS_SEPARATOR = 4;
	private static final int LAST_CHAR_WAS_CR = 2;
	private static final int ESCAPED = 1;
	private static final int NONE = 0;
	private static final int TURN_OFF_LAST_CHAR_MASK = ~(LAST_CHAR_WAS_CR|LAST_CHAR_WAS_SEPARATOR);

	private final CharBuffer _csvBuffer;
	private int _currentIndex = 0;
	private int _currentState = NONE;
	private int cellStart = 0;

	private final CellTransformer cellTransformer;
	private final TextFormat _textFormat;

	public CharConsumer(CharBuffer csvBuffer, TextFormat textFormat, CellTransformer cellTransformer) {
		this._csvBuffer = csvBuffer;
		this._textFormat = textFormat;
		this.cellTransformer = cellTransformer;
	}

	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		final TextFormat textFormat = _textFormat;

		final char[] chars = _csvBuffer.getCharBuffer();
		final int bufferSize = _csvBuffer.getBufferSize();

		int currentState = _currentState;

		int currentIndex;
		for(currentIndex = _currentIndex; currentIndex  < bufferSize; currentIndex++) {
			char character = chars[currentIndex];
			if (textFormat.isNotEscapeCharacter(character)) {
				if (isNotEscaped(currentState)) {
					if (textFormat.isSeparator(character)) {
						newCell(chars, currentIndex, cellConsumer);
						currentState = LAST_CHAR_WAS_SEPARATOR;
						continue;
					} else if (character == '\n') {
						if (lastCharWasNotCr(currentState)) {
							endOfRow(chars, currentIndex, cellConsumer);
							currentState = NONE;
							continue;
						}
						cellStart = currentIndex + 1;
					} else if (character == '\r') {
						endOfRow(chars, currentIndex, cellConsumer);
						currentState = LAST_CHAR_WAS_CR;
						continue;
					}
				}
				currentState &= TURN_OFF_LAST_CHAR_MASK;
			} else {
				currentState ^= ESCAPED;
			}
		}
		_currentState = currentState;
		_currentIndex = currentIndex;
	}

	public void endOfRow(char[] chars, int currentIndex, CellConsumer cellConsumer) {
		newCell(chars, currentIndex, cellConsumer);
		cellConsumer.endOfRow();
	}

	public void newCell(char[] chars, int currentIndex, CellConsumer cellConsumer) {
		cellTransformer.newCell(chars, cellStart, currentIndex, cellConsumer);
		cellStart = currentIndex + 1;
	}

	public final boolean consumeToNextRow(CellConsumer cellConsumer) {
		final TextFormat textFormat = _textFormat;

		final char[] chars = _csvBuffer.getCharBuffer();
		final int bufferSize = _csvBuffer.getBufferSize();

		int currentState = _currentState;

		int currentIndex;
		for(currentIndex = _currentIndex; currentIndex  < bufferSize; currentIndex++) {
			char character = chars[currentIndex];
			if (textFormat.isNotEscapeCharacter(character)) {
				if (isNotEscaped(currentState)) {
					if (textFormat.isSeparator(character)) {
						newCell(chars, currentIndex, cellConsumer);
						currentState = LAST_CHAR_WAS_SEPARATOR;
						continue;
					} else if (character == '\n') {
						if (lastCharWasNotCr(currentState)) {
							if (endOfRowReturnValue(chars, currentIndex, cellConsumer)) {
								exitOnState(currentIndex, NONE);
								return true;
							}
							currentState = NONE;
							continue;
						}
						cellStart = currentIndex + 1;
					} else if (character == '\r') {
						if (endOfRowReturnValue(chars, currentIndex, cellConsumer)) {
							exitOnState(currentIndex, LAST_CHAR_WAS_CR);
							return true;
						}
						currentState = LAST_CHAR_WAS_CR;
						continue;
					}
				}
				currentState &= TURN_OFF_LAST_CHAR_MASK;
			} else {
				currentState ^= ESCAPED;
			}
		}

		_currentState = currentState;
		_currentIndex = currentIndex;

		return false;
	}

	private void exitOnState(int currentIndex, int none) {
		_currentState = none;
		_currentIndex = currentIndex + 1;
	}

	public boolean endOfRowReturnValue(char[] chars, int currentIndex, CellConsumer cellConsumer) {
		newCell(chars, currentIndex, cellConsumer);
		return cellConsumer.endOfRow();
	}

	public final void finish(CellConsumer cellConsumer) {
		if ( _currentIndex > cellStart
				|| lastCharWasSeparator(_currentState)) {
			newCell(_csvBuffer.getCharBuffer(), _currentIndex, cellConsumer);
			_currentState = NONE;
		}
		cellConsumer.end();
	}

	public final int refillBuffer() throws IOException {
		shiftBufferToMark();
		return _csvBuffer.fillBuffer();
	}

	public void shiftBufferToMark() throws BufferOverflowException {
		int m = Math.max(0, _csvBuffer.shiftBufferToMark(cellStart));
		_currentIndex -= m;
		cellStart -= m;
	}

	private static boolean isNotEscaped(int currentState) {
		return (currentState & ESCAPED) == 0;
	}

	private static boolean lastCharWasNotCr(int currentState) {
		return (currentState & LAST_CHAR_WAS_CR) == 0;
	}

	private static boolean lastCharWasSeparator(int currentState) {
		return (currentState & LAST_CHAR_WAS_SEPARATOR) != 0;
	}
}
