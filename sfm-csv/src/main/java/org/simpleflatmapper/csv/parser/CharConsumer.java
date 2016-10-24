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
	private int _cellStart = 0;

	private final TextFormat _textFormat;
	private final CellTransformer _cellTransformer;

	public CharConsumer(CharBuffer csvBuffer, TextFormat textFormat, CellTransformer cellTransformer) {
		this._csvBuffer = csvBuffer;
		this._textFormat = textFormat;
		this._cellTransformer = cellTransformer;
	}

	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		final TextFormat textFormat = _textFormat;
		final CellTransformer cellTransformer = _cellTransformer;

		final int bufferSize = _csvBuffer.getBufferSize();
		final char[] chars = _csvBuffer.getCharBuffer();

		int currentIndex = _currentIndex;
		int currentState = _currentState;
		int cellStart = _cellStart;

		for(;currentIndex  < bufferSize; currentIndex++) {
			char character = chars[currentIndex];
			if (textFormat.isNotEscapeCharacter(character)) {
				if (isEscaped(currentState)) {
					if (textFormat.isSeparator(character)) {
						cellTransformer.newCell(chars, cellStart, currentIndex, cellConsumer);
						cellStart = currentIndex + 1;
						currentState = LAST_CHAR_WAS_SEPARATOR;
						continue;
					} else if (character == '\n') {
						if (lastCharWasNotCr(currentState)) {
							cellTransformer.newCell(chars, cellStart, currentIndex, cellConsumer);
							cellConsumer.endOfRow();
							currentState = NONE;
							cellStart = currentIndex + 1;
							continue;
						}
						cellStart = currentIndex + 1;
					} else if (character == '\r') {
						cellTransformer.newCell(chars, cellStart, currentIndex, cellConsumer);
						cellConsumer.endOfRow();
						currentState = LAST_CHAR_WAS_CR;
						cellStart = currentIndex + 1;
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
		_cellStart = cellStart;
	}

	public final boolean consumeToNextRow(CellConsumer cellConsumer) {
		final TextFormat textFormat = _textFormat;
		final CellTransformer cellTransformer = _cellTransformer;

		final int bufferSize = _csvBuffer.getBufferSize();
		final char[] chars = _csvBuffer.getCharBuffer();

		int currentIndex = _currentIndex;
		int currentState = _currentState;
		int cellStart = _cellStart;

		for(;currentIndex  < bufferSize; currentIndex++) {
			char character = chars[currentIndex];
			if (textFormat.isNotEscapeCharacter(character)) {
				if (isEscaped(currentState)) {
					if (textFormat.isSeparator(character)) {
						cellTransformer.newCell(chars, cellStart, currentIndex, cellConsumer);
						cellStart = currentIndex + 1;
						currentState = LAST_CHAR_WAS_SEPARATOR;
						continue;
					} else if (character == '\n') {
						if (lastCharWasNotCr(currentState)) {
							cellTransformer.newCell(chars, cellStart, currentIndex, cellConsumer);
							boolean b = cellConsumer.endOfRow();
							currentState = NONE;
							cellStart = currentIndex + 1;

							if (b) {
								_currentState = currentState;
								_currentIndex = currentIndex + 1;
								_cellStart = cellStart;
								return true;
							}

							continue;
						}
						cellStart = currentIndex + 1;
					} else if (character == '\r') {

						cellTransformer.newCell(chars, cellStart, currentIndex, cellConsumer);
						boolean b = cellConsumer.endOfRow();

						currentState = LAST_CHAR_WAS_CR;
						cellStart = currentIndex + 1;

						if (b) {
							_currentState = currentState;
							_currentIndex = currentIndex + 1;
							_cellStart = cellStart;
							return true;
						}
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
		_cellStart = cellStart;

		return false;
	}

	public final void finish(CellConsumer cellConsumer) {
		if ( _currentIndex > _cellStart
				|| lastCharWasSeparator()) {
			_cellTransformer.newCell(_csvBuffer.getCharBuffer(), _cellStart, _currentIndex, cellConsumer);
			_cellStart = _currentIndex + 1;
			_currentState = NONE;
		}
		cellConsumer.end();
	}

	public final boolean refillBuffer() throws IOException {
		if (_csvBuffer.supportsShift()) {
			_csvBuffer.shiftBufferToMark(_cellStart);
			_currentIndex -= _cellStart;
			_cellStart = 0;
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
