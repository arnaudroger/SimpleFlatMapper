package org.simpleflatmapper.csv.parser;


import java.io.IOException;

/**
 * Consume the charBuffer.
 */
public final class CharConsumer {

	private static final int DATA                        = 16;
	private static final int ESCAPED                     = 8;
	private static final int LAST_CHAR_WAS_SEPARATOR     = 4;
	private static final int LAST_CHAR_WAS_CR            = 2;
	private static final int ESCAPED_AREA = 1;
	private static final int NONE                        = 0;

	private static final int TURN_OFF_LAST_CHAR_MASK = ~(LAST_CHAR_WAS_CR|LAST_CHAR_WAS_SEPARATOR);
	private static final int TURN_OFF_ESCAPED_AREA = ~(ESCAPED_AREA);

	private static final char LF = '\n';
	private static final char CR = '\r';
	private static final char SPACE = ' ';

	private final CharBuffer csvBuffer;
	private final TextFormat textFormat;
	private final CellPreProcessor cellPreProcessor;

	private int _currentIndex = 0;
	private int _currentState = NONE;

	public CharConsumer(CharBuffer csvBuffer, TextFormat textFormat, CellPreProcessor cellPreProcessor) {
		this.csvBuffer = csvBuffer;
		this.cellPreProcessor = cellPreProcessor;
		this.textFormat = textFormat;
	}

	public final void consumeAllBuffer(final CellConsumer cellConsumer) {

		final boolean notIgnoreLeadingSpace = !cellPreProcessor.ignoreLeadingSpace();
		final char escapeChar = textFormat.escapeChar;
		final char separatorChar = textFormat.separatorChar;

		int currentState = _currentState;
		int currentIndex = _currentIndex;

		final char[] chars = csvBuffer.buffer;
		final int bufferSize =  csvBuffer.bufferSize;

		while(currentIndex < bufferSize) {
			// unescaped loop
			if ((currentState & ESCAPED_AREA) == 0) {
				while(currentIndex < bufferSize) {
					final char character = chars[currentIndex];
					final int cellEnd = currentIndex;

					currentIndex ++;

					if (character == separatorChar) { // separator
						cellPreProcessor.newCell(chars, csvBuffer.mark, cellEnd, cellConsumer);
						csvBuffer.mark = currentIndex;
						currentState = LAST_CHAR_WAS_SEPARATOR;
						continue;
					} else if (character == LF) { // \n
						if ((currentState & LAST_CHAR_WAS_CR) == 0) {
							cellPreProcessor.newCell(chars, csvBuffer.mark, cellEnd, cellConsumer);
							cellConsumer.endOfRow();
						}
						csvBuffer.mark = currentIndex;
						currentState = NONE;
						continue;
					} else if (character == CR) { // \r
						cellPreProcessor.newCell(chars, csvBuffer.mark, cellEnd, cellConsumer);
						csvBuffer.mark = currentIndex;
						currentState = LAST_CHAR_WAS_CR;
						cellConsumer.endOfRow();
						continue;
					} else if (character == escapeChar) { // has no data or is already escaped
						if (((currentState ^ DATA) & (ESCAPED | DATA)) != 0) {
							currentState = ESCAPED_AREA | ESCAPED;
							break;
						}
					}

					currentState &= TURN_OFF_LAST_CHAR_MASK;
					if (notIgnoreLeadingSpace || character != SPACE) {
						currentState |= DATA;
					}
				}
			} else {
				int nextEscapeChar = findNextEscapeChar(chars, currentIndex, bufferSize, escapeChar);
				if (nextEscapeChar >= 0) {
					currentIndex = nextEscapeChar + 1;
					currentState &= TURN_OFF_ESCAPED_AREA;
				} else {
					currentIndex = bufferSize;
				}
			}
		}

		_currentState = currentState;
		_currentIndex = currentIndex;
	}


	public final boolean consumeToNextRow(CellConsumer cellConsumer) {
		final boolean notIgnoreLeadingSpace = !cellPreProcessor.ignoreLeadingSpace();
		final char escapeChar = textFormat.escapeChar;
		final char separatorChar = textFormat.separatorChar;

		int currentState = _currentState;
		int currentIndex = _currentIndex;

		final char[] chars = csvBuffer.buffer;
		final int bufferSize =  csvBuffer.bufferSize;

		while(currentIndex < bufferSize) {
			// unescaped loop
			if ((currentState & ESCAPED_AREA) == 0) {
				while(currentIndex < bufferSize) {
					final char character = chars[currentIndex];
					final int cellEnd = currentIndex;

					currentIndex ++;

					if (character == separatorChar) { // separator
						cellPreProcessor.newCell(chars, csvBuffer.mark, cellEnd, cellConsumer);
						csvBuffer.mark = currentIndex;
						currentState = LAST_CHAR_WAS_SEPARATOR;
						continue;
					} else if (character == LF) { // \n
						if ((currentState & LAST_CHAR_WAS_CR) == 0) {
							cellPreProcessor.newCell(chars, csvBuffer.mark, cellEnd, cellConsumer);
							if (cellConsumer.endOfRow()) {
								csvBuffer.mark = currentIndex;
								_currentState = NONE;
								_currentIndex = currentIndex;
								return true;
							}
						}
						csvBuffer.mark = currentIndex;
						currentState = NONE;
						continue;
					} else if (character == CR) { // \r
						cellPreProcessor.newCell(chars, csvBuffer.mark, cellEnd, cellConsumer);
						csvBuffer.mark = currentIndex;
						currentState = LAST_CHAR_WAS_CR;
						if (cellConsumer.endOfRow()) {
							_currentState = currentState;
							_currentIndex = currentIndex;
							return true;
						}
						continue;
					} else if (character == escapeChar) { // has no data or is already escaped
						if (((currentState ^ DATA) & (ESCAPED | DATA)) != 0) {
							currentState = ESCAPED_AREA | ESCAPED;
							break;
						}
					}

					currentState &= TURN_OFF_LAST_CHAR_MASK;
					if (notIgnoreLeadingSpace || character != SPACE) {
						currentState |= DATA;
					}
				}
			} else {
				int nextEscapeChar = findNextEscapeChar(chars, currentIndex, bufferSize, escapeChar);
				if (nextEscapeChar >= 0) {
					currentIndex = nextEscapeChar + 1;
					currentState &= TURN_OFF_ESCAPED_AREA;
				} else {
					currentIndex = bufferSize;
				}
			}
		}

		_currentState = currentState;
		_currentIndex = currentIndex;

		return false;

	}

	private int findNextEscapeChar(char[] chars, int currentIndex, int bufferSize, char escapeChar) {
		for(int i = currentIndex; i < bufferSize; i++) {
			if (chars[i] == escapeChar) return i;
		}
		return -1;
	}

	public final void finish(CellConsumer cellConsumer) {
		if ( hasUnconsumedData()
				|| (_currentState & LAST_CHAR_WAS_SEPARATOR) != 0) {
			cellPreProcessor.newCell(csvBuffer.buffer, csvBuffer.mark, _currentIndex, cellConsumer);
			csvBuffer.mark = _currentIndex + 1;
			_currentState = NONE;
		}
		cellConsumer.end();
	}

	private boolean hasUnconsumedData() {
		return _currentIndex > csvBuffer.mark;
	}

	public boolean next() throws IOException {
		int mark = csvBuffer.mark;
		boolean b = csvBuffer.next();
		_currentIndex -= mark - csvBuffer.mark;
		return b;
	}
}
