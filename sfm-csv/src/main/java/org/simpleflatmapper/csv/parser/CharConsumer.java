package org.simpleflatmapper.csv.parser;


import java.io.IOException;

/**
 * Consume the charBuffer.
 */
public class CharConsumer {

	private static final int DATA                        = 16;
	private static final int ESCAPED                     = 8;
	private static final int LAST_CHAR_WAS_SEPARATOR     = 4;
	private static final int LAST_CHAR_WAS_CR            = 2;
	private static final int ESCAPED_AREA = 1;
	private static final int NONE                        = 0;

	private static final int TURN_OFF_LAST_CHAR_MASK = ~(LAST_CHAR_WAS_CR|LAST_CHAR_WAS_SEPARATOR);
	public static final char LF = '\n';
	public static final char CR = '\r';
	public static final char SPACE = ' ';

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
		final int escapeChar = textFormat.escapeChar;
		final int separatorChar = textFormat.separatorChar;

		int currentState = _currentState;
		int currentIndex = _currentIndex;

		final char[] chars = csvBuffer.buffer;
		final int bufferSize =  csvBuffer.bufferSize;

		for(; currentIndex  < bufferSize; currentIndex++) {
			final char character = chars[currentIndex];

			if (character != escapeChar) {
				if ((currentState & ESCAPED_AREA) == 0) {
					if (character == separatorChar) { // separator
						newCell(cellConsumer, currentIndex, chars);
						currentState = LAST_CHAR_WAS_SEPARATOR;
						continue;
					} else if (character == LF) { // \n
						if ((currentState & LAST_CHAR_WAS_CR) == 0) {
							endOfRow(cellConsumer, currentIndex, chars);
						}
						csvBuffer.mark = currentIndex + 1;
						currentState = NONE;
						continue;
					} else if (character == CR) { // \r
						endOfRow(cellConsumer, currentIndex, chars);
						csvBuffer.mark = currentIndex + 1;
						currentState = LAST_CHAR_WAS_CR;
						continue;
					}
				}
				currentState &= TURN_OFF_LAST_CHAR_MASK;
				if (notIgnoreLeadingSpace || character != SPACE) {
					currentState  |= DATA;
				}
			} else if(((currentState ^ DATA) & (ESCAPED | DATA)) != 0){ // escape
				currentState = (currentState ^ ESCAPED_AREA) | ESCAPED;
			}
		}

		_currentState = currentState;
		_currentIndex = currentIndex;
	}

	public final boolean consumeToNextRow(CellConsumer cellConsumer) {
		final boolean notIgnoreLeadingSpace = !cellPreProcessor.ignoreLeadingSpace();
		final int escapeChar = textFormat.escapeChar;
		final int separatorChar = textFormat.separatorChar;

		int currentState = _currentState;
		int currentIndex = _currentIndex;

		final char[] chars = csvBuffer.buffer;
		final int bufferSize = csvBuffer.bufferSize;

		for(; currentIndex  < bufferSize; currentIndex++) {
			final char character = chars[currentIndex];

			if (character != escapeChar) {
				if ((currentState & ESCAPED_AREA) == 0) {
					if (character == separatorChar) { // separator
						newCell(cellConsumer, currentIndex, chars);
						currentState = LAST_CHAR_WAS_SEPARATOR;
						continue;
					} else if (character == LF) { // \n
						if ((currentState & LAST_CHAR_WAS_CR) == 0) {
							if (bEndOfRow(cellConsumer, currentIndex, chars)) {
								csvBuffer.mark = currentIndex + 1;
								_currentIndex = currentIndex + 1;
								_currentState = NONE;
								return true;
							}
						}
						csvBuffer.mark = currentIndex + 1;
						currentState = NONE;
						continue;
					} else if (character == CR) { // \r
						if (bEndOfRow(cellConsumer, currentIndex, chars)) {
							csvBuffer.mark = currentIndex + 1;
							_currentIndex = currentIndex + 1;
							_currentState = LAST_CHAR_WAS_CR;
							return true;
						}
						csvBuffer.mark = currentIndex + 1;
						currentState = LAST_CHAR_WAS_CR;
						continue;
					}
				}
				currentState &= TURN_OFF_LAST_CHAR_MASK;
				if (notIgnoreLeadingSpace || character != SPACE) {
					currentState  |= DATA;
				}
			} else if(((currentState ^ DATA) & (ESCAPED | DATA)) != 0){ // escape
				currentState = (currentState ^ ESCAPED_AREA) | ESCAPED;
			}
		}

		_currentState = currentState;
		_currentIndex = currentIndex;

		return false;

	}

	public final void finish(CellConsumer cellConsumer) {
		if ( hasUnconsumedData()
				|| (_currentState & LAST_CHAR_WAS_SEPARATOR) != 0) {
			newCell(cellConsumer, _currentIndex, csvBuffer.buffer);
			_currentState = NONE;
		}
		cellConsumer.end();
	}

	public final boolean refillBuffer() throws IOException {
		return csvBuffer.fillBuffer() >= 0;
	}

	public final void shiftBufferToMark() throws BufferOverflowException {
		_currentIndex -= csvBuffer.shiftBufferToMark();
	}

	private void endOfRow(CellConsumer cellConsumer, int currentIndex, char[] chars) {
		cellPreProcessor.newCell(chars, csvBuffer.mark, currentIndex, cellConsumer);
		cellConsumer.endOfRow();
	}
	private boolean bEndOfRow(CellConsumer cellConsumer, int currentIndex, char[] chars) {
		cellPreProcessor.newCell(chars, csvBuffer.mark, currentIndex, cellConsumer);
		return cellConsumer.endOfRow();
	}

	private void newCell(CellConsumer cellConsumer, int currentIndex, char[] chars) {
		cellPreProcessor.newCell(chars, csvBuffer.mark, currentIndex, cellConsumer);
		csvBuffer.mark = currentIndex + 1;
	}


	private boolean hasUnconsumedData() {
		return _currentIndex > csvBuffer.mark;
	}

}
