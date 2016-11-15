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
	private static final int UNESCAPED_AREA = 1;
	private static final int NONE                        = 0;

	private static final int TURN_OFF_LAST_CHAR_MASK = ~(LAST_CHAR_WAS_CR|LAST_CHAR_WAS_SEPARATOR);
	public static final char LF = '\n';
	public static final char CR = '\r';
	public static final char SPACE = ' ';

	private final CharBuffer csvBuffer;
	private final char escapeChar;
	private final char separatorChar;
	private final CellPreProcessor cellPreProcessor;
	private final boolean notIgnoringLeadingSpace;

	private int _currentIndex = 0;
	private int _currentState = NONE;

	public CharConsumer(CharBuffer csvBuffer, TextFormat textFormat, CellPreProcessor cellPreProcessor) {
		this.csvBuffer = csvBuffer;
		this.cellPreProcessor = cellPreProcessor;
		this.escapeChar = textFormat.escapeChar;
		this.separatorChar = textFormat.separatorChar;
		this.notIgnoringLeadingSpace = !cellPreProcessor.ignoreLeadingSpace();
	}

	public final void consumeAllBuffer(final CellConsumer cellConsumer) {

		final boolean notIgnoringLeadingSpace = this.notIgnoringLeadingSpace;
		final int escapeChar = this.escapeChar;
		final int separatorChar = this.separatorChar;

		int currentState = _currentState;
		int currentIndex = _currentIndex;

		final char[] chars = csvBuffer.buffer;
		final int bufferSize =  Math.min(csvBuffer.bufferSize, chars.length);

		for(; currentIndex  < bufferSize; currentIndex++) {
			final char character = chars[currentIndex];

			final boolean unescapedArea = (currentState & UNESCAPED_AREA) == 0;

			if (character == separatorChar) { // separator
				if (unescapedArea) {
					cellPreProcessor.newCell(chars, csvBuffer.mark, currentIndex, cellConsumer);
					csvBuffer.mark = currentIndex + 1;
					currentState = LAST_CHAR_WAS_SEPARATOR;
					continue;
				}
			} else if (character == LF) { // \n
				if (unescapedArea) {
					if ((currentState & LAST_CHAR_WAS_CR) == 0) {
						cellPreProcessor.newCell(chars, csvBuffer.mark, currentIndex, cellConsumer);
						cellConsumer.endOfRow();
					}
					csvBuffer.mark = currentIndex + 1;
					currentState = NONE;
					continue;
				}
			} else if (character == CR) { // \r
				if (unescapedArea) {
					cellPreProcessor.newCell(chars, csvBuffer.mark, currentIndex, cellConsumer);
					cellConsumer.endOfRow();
					csvBuffer.mark = currentIndex + 1;
					currentState = LAST_CHAR_WAS_CR;
					continue;
				}
			} else if (character == escapeChar) { // escape
				currentState =
						(((currentState ^ DATA) & (ESCAPED | DATA)) != 0) ?
						(currentState ^ UNESCAPED_AREA) | ESCAPED : currentState;
				continue;
			}

			currentState = (currentState & TURN_OFF_LAST_CHAR_MASK)
					| ((notIgnoringLeadingSpace || character != SPACE) ? DATA : 0);
		}

		_currentState = currentState;
		_currentIndex = currentIndex;
	}

	public final boolean consumeToNextRow(CellConsumer cellConsumer) {
		final boolean notIgnoringLeadingSpace = this.notIgnoringLeadingSpace;
		final int escapeChar = this.escapeChar;
		final int separatorChar = this.separatorChar;

		int currentState = _currentState;
		int currentIndex = _currentIndex;

		final char[] chars = csvBuffer.buffer;
		final int bufferSize =  Math.min(csvBuffer.bufferSize, chars.length);

		for(; currentIndex  < bufferSize; currentIndex++) {
			final char character = chars[currentIndex];

			final boolean unescapedArea = (currentState & UNESCAPED_AREA) == 0;

			if (character == separatorChar) { // separator
				if (unescapedArea) {
					cellPreProcessor.newCell(chars, csvBuffer.mark, currentIndex, cellConsumer);
					csvBuffer.mark = currentIndex + 1;
					currentState = LAST_CHAR_WAS_SEPARATOR;
					continue;
				}
			} else if (character == LF) { // \n
				if (unescapedArea) {
					if ((currentState & LAST_CHAR_WAS_CR) == 0) {
						cellPreProcessor.newCell(chars, csvBuffer.mark, currentIndex, cellConsumer);
						csvBuffer.mark = currentIndex + 1;
						currentState = NONE;
						if (cellConsumer.endOfRow()) {
							_currentIndex = currentIndex + 1;
							_currentState = currentState;
							return true;
						}
						continue;
					}
					csvBuffer.mark = currentIndex + 1;
					currentState = NONE;
					continue;
				}
			} else if (character == CR) { // \r
				if (unescapedArea) {
					cellPreProcessor.newCell(chars, csvBuffer.mark, currentIndex, cellConsumer);
					csvBuffer.mark = currentIndex + 1;
					currentState = LAST_CHAR_WAS_CR;
					if (cellConsumer.endOfRow()) {
						_currentIndex = currentIndex + 1;
						_currentState = currentState;
						return true;
					}
					continue;
				}
			} else if (character == escapeChar) { // escape
				currentState =
						(((currentState ^ DATA) & (ESCAPED | DATA)) != 0) ?
								(currentState ^ UNESCAPED_AREA) | ESCAPED : currentState;
				continue;
			}

			currentState = (currentState & TURN_OFF_LAST_CHAR_MASK)
					| ((notIgnoringLeadingSpace || character != SPACE) ? DATA : 0);
		}

		_currentState = currentState;
		_currentIndex = currentIndex;

		return false;

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

	public final boolean refillBuffer() throws IOException {
		return csvBuffer.fillBuffer() >= 0;
	}

	public final void shiftBufferToMark() throws BufferOverflowException {
		_currentIndex -= csvBuffer.shiftBufferToMark();
	}

	private boolean hasUnconsumedData() {
		return _currentIndex > csvBuffer.mark;
	}

}
