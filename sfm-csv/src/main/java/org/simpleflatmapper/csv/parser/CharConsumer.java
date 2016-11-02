package org.simpleflatmapper.csv.parser;


import java.io.IOException;

/**
 * Consume the charBuffer.
 */
public abstract class CharConsumer {

	private static final int DATA                        = 16;
	private static final int ESCAPED                     = 8;
	private static final int LAST_CHAR_WAS_SEPARATOR     = 4;
	private static final int LAST_CHAR_WAS_CR            = 2;
	private static final int LAST_CHAR_WAS_ESCAPE        = 1;
	private static final int NONE                        = 0;

	private static final int TURN_OFF_LAST_CHAR_MASK = ~(LAST_CHAR_WAS_CR|LAST_CHAR_WAS_SEPARATOR);

	private final CharBuffer _csvBuffer;

	private int _currentIndex = 0;
	private int _currentState = NONE;

	public CharConsumer(CharBuffer csvBuffer) {
		this._csvBuffer = csvBuffer;
	}

	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		final char[] chars = _csvBuffer.getCharBuffer();
		final int bufferSize = _csvBuffer.getBufferSize();

		int currentState = _currentState;

		int currentIndex;
		for(currentIndex = _currentIndex; currentIndex  < bufferSize; currentIndex++) {
			char character = chars[currentIndex];
			if (isNotEscapeCharacter(character)) {
				if (isCharEscaped(currentState)) {
					if (isSeparator(character)) {
						newCell(chars, currentIndex, cellConsumer);
						currentState = LAST_CHAR_WAS_SEPARATOR;
						continue;
					} else if (character == '\n') {
						if (lastCharWasNotCr(currentState)) {
							endOfRow(chars, currentIndex, cellConsumer);
							currentState = NONE;
							continue;
						}
						startNextCell(currentIndex);
						currentState = NONE;
						continue;
					} else if (character == '\r') {
						endOfRow(chars, currentIndex, cellConsumer);
						currentState = LAST_CHAR_WAS_CR;
						continue;
					}
				}
				currentState &= TURN_OFF_LAST_CHAR_MASK;
				currentState |= (isNotIgnoringLeadingSpace() || character != ' ') ? DATA : 0;
			} else if (canEscaped(currentState)) {
				currentState = (currentState ^ LAST_CHAR_WAS_ESCAPE) | ESCAPED;
			}
		}
		_currentState = currentState;
		_currentIndex = currentIndex;
	}

	public final boolean consumeToNextRow(CellConsumer cellConsumer) {
		final char[] chars = _csvBuffer.getCharBuffer();
		final int bufferSize = _csvBuffer.getBufferSize();

		int currentState = _currentState;

		int currentIndex;
		for(currentIndex = _currentIndex; currentIndex  < bufferSize; currentIndex++) {
			char character = chars[currentIndex];
			if (isNotEscapeCharacter(character)) {
				if (isCharEscaped(currentState)) {
					if (isSeparator(character)) {
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
						startNextCell(currentIndex);
						currentState = NONE;
						continue;
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
				currentState |= (isNotIgnoringLeadingSpace() || character != ' ') ? DATA : 0;
			} else if (canEscaped(currentState)) {
				currentState = (currentState ^ LAST_CHAR_WAS_ESCAPE) | ESCAPED;
			}
		}

		_currentState = currentState;
		_currentIndex = currentIndex;

		return false;
	}

	public final void finish(CellConsumer cellConsumer) {
		if ( hasUnconsumedData()
				|| lastCharWasSeparator(_currentState)) {
			newCell(_csvBuffer.getCharBuffer(), _currentIndex, cellConsumer);
			_currentState = NONE;
		}
		cellConsumer.end();
	}

	protected abstract boolean isSeparator(char character);

	protected abstract boolean isNotEscapeCharacter(char character);

	protected abstract void pushCell(char[] chars, int start, int end, CellConsumer cellConsumer);

	protected abstract boolean isNotIgnoringLeadingSpace();

	public final boolean refillBuffer() throws IOException {
		return _csvBuffer.fillBuffer() >= 0;
	}

	public final void shiftBufferToMark() throws BufferOverflowException {
		_currentIndex -= _csvBuffer.shiftBufferToMark();
	}

	private void endOfRow(char[] chars, int currentIndex, CellConsumer cellConsumer) {
		newCell(chars, currentIndex, cellConsumer);
		cellConsumer.endOfRow();
	}

	private boolean endOfRowReturnValue(char[] chars, int currentIndex, CellConsumer cellConsumer) {
		newCell(chars, currentIndex, cellConsumer);
		return cellConsumer.endOfRow();
	}

	private void newCell(char[] chars, int currentIndex, CellConsumer cellConsumer) {
		pushCell(chars, _csvBuffer.mark, currentIndex, cellConsumer);
		startNextCell(currentIndex);
	}

	private void startNextCell(int currentIndex) {
		_csvBuffer.mark = currentIndex + 1;
	}

	private boolean hasUnconsumedData() {
		return _currentIndex > _csvBuffer.mark;
	}

	private void exitOnState(int currentIndex, int none) {
		_currentState = none;
		_currentIndex = currentIndex + 1;
	}

	private static boolean canEscaped(int currentState) {
		return ((currentState ^ DATA) & (ESCAPED | DATA)) != 0;
	}

	private static boolean isEscaped(int currentState) {
		return (currentState & ESCAPED) != 0;
	}

	private static boolean isCharEscaped(int currentState) {
		return (currentState & LAST_CHAR_WAS_ESCAPE) == 0;
	}

	private static boolean lastCharWasNotCr(int currentState) {
		return (currentState & LAST_CHAR_WAS_CR) == 0;
	}

	private static boolean lastCharWasSeparator(int currentState) {
		return (currentState & LAST_CHAR_WAS_SEPARATOR) != 0;
	}
}
