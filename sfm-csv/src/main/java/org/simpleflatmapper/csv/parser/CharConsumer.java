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
		final int bufferSize = Math.min(_csvBuffer.getBufferSize(), chars.length); // hint

		int currentIndex = _currentIndex;
		int currentState = _currentState;

		for(;currentIndex  < bufferSize; currentIndex++) {
			char character = chars[currentIndex];
			if (textFormat.isNotEscapeCharacter(character)) {
				if (isEscaped(currentState)) {
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

		final int bufferSize = _csvBuffer.getBufferSize();
		final char[] chars = _csvBuffer.getCharBuffer();

		int currentIndex = _currentIndex;
		int currentState = _currentState;

		for(;currentIndex  < bufferSize; currentIndex++) {
			char character = chars[currentIndex];
			if (textFormat.isNotEscapeCharacter(character)) {
				if (isEscaped(currentState)) {
					if (textFormat.isSeparator(character)) {
						newCell(chars, currentIndex, cellConsumer);
						currentState = LAST_CHAR_WAS_SEPARATOR;
						continue;
					} else if (character == '\n') {
						if (lastCharWasNotCr(currentState)) {
							if (endOfRowReturnValue(chars, currentIndex, cellConsumer)) {
								_currentState = NONE;
								_currentIndex = currentIndex + 1;
								return true;
							}
							currentState = NONE;
							continue;
						}
						cellStart = currentIndex + 1;
					} else if (character == '\r') {
						if (endOfRowReturnValue(chars, currentIndex, cellConsumer)) {
							_currentState = LAST_CHAR_WAS_CR;
							_currentIndex = currentIndex + 1;
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

	public boolean endOfRowReturnValue(char[] chars, int currentIndex, CellConsumer cellConsumer) {
		newCell(chars, currentIndex, cellConsumer);
		return cellConsumer.endOfRow();
	}

	public final void finish(CellConsumer cellConsumer) {
		if ( _currentIndex > cellStart
				|| lastCharWasSeparator()) {
			newCell(_csvBuffer.getCharBuffer(), _currentIndex, cellConsumer);
			_currentState = NONE;
		}
		cellConsumer.end();
	}

	public final boolean refillBuffer() throws IOException {
		if (_csvBuffer.shiftBufferToMark(cellStart)) {
			_currentIndex -= cellStart;
			cellStart = 0;
			return _csvBuffer.fillBuffer();
		} else {
			return false;
		}
	}

	private boolean isEscaped(int currentState) {
		return (currentState & ESCAPED) == 0;
	}



	private boolean lastCharWasNotCr(int currentState) {
		return (currentState & LAST_CHAR_WAS_CR) == 0;
	}

	private boolean lastCharWasSeparator() {
		return (_currentState & LAST_CHAR_WAS_SEPARATOR) != 0;
	}
}
