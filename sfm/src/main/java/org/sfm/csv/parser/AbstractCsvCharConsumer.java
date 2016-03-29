package org.sfm.csv.parser;

import java.io.IOException;

public abstract class AbstractCsvCharConsumer extends CsvCharConsumer {
	private static final int IN_QUOTE = 4;
	private static final int IN_CR = 2;
	private static final int QUOTE = 1;
	private static final int NONE = 0;
	private static final int TURN_OFF_IN_CR_MASK = ~IN_CR;
	private static final int ALL_QUOTES = QUOTE | IN_QUOTE;
	protected final CharBuffer csvBuffer;

	protected int _currentIndex;
	private int currentState = NONE;

	public AbstractCsvCharConsumer(CharBuffer csvBuffer) {
		this.csvBuffer = csvBuffer;
	}

	@Override
	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferSize();
		char[] buffer = csvBuffer.getCharBuffer();
		for(int i = _currentIndex; i  < bufferLength; i++) {
			consumeOneChar(buffer[i], i, cellConsumer);
		}
		_currentIndex = bufferLength;
	}

	protected abstract void consumeOneChar(char c, int i, CellConsumer cellConsumer);

	/**
	 * use bit mask to testing if == IN_CR
	 */
	protected final void turnOffCrFlag() {
		currentState &= TURN_OFF_IN_CR_MASK;
	}

	protected final void newCellIfNotInQuote(int currentIndex, CellConsumer cellConsumer) {
		if (isInQuote())
			return;

		newCell(currentIndex, cellConsumer);
	}

	private boolean isInQuote() {
		return currentState == IN_QUOTE;
	}

	protected final boolean handleEndOfLineLF(int currentIndex, CellConsumer cellConsumer) {
		if (!isInQuote()) {
			if (currentState != IN_CR) {
				endOfRow(currentIndex, cellConsumer);
				return true;
			} else {
				// we had a preceding cr so shift the marl
				csvBuffer.mark(currentIndex + 1);
			}
		}
		return false;
	}

	protected final boolean handleEndOfLineCR(int currentIndex, CellConsumer cellConsumer) {
		if (!isInQuote()) {
			endOfRow(currentIndex, cellConsumer);
			currentState = IN_CR;
			return true;
		}
		return false;
	}

	private void endOfRow(int currentIndex, CellConsumer cellConsumer) {
		newCell(currentIndex, cellConsumer);
		cellConsumer.endOfRow();
	}

	protected final void quote(int currentIndex) {
		if (isAllConsumedFromMark(currentIndex)) {
			currentState = IN_QUOTE;
		} else {
			currentState ^= ALL_QUOTES;
		}
	}

	private void newCell(int currentIndex, CellConsumer cellConsumer) {
		char[] charBuffer = csvBuffer.getCharBuffer();
		int start = csvBuffer.getMark();
		int length = currentIndex - start;

		if (charBuffer[start] == quoteChar()) {
			length = unescape(charBuffer, start, length);
			start++;
		}

		cellConsumer.newCell(charBuffer, start, length);
		csvBuffer.mark(currentIndex + 1);
		currentState = NONE;
	}

	@Override
	public final void finish(CellConsumer cellConsumer) {
		if (!isAllConsumedFromMark(_currentIndex)) {
			newCell(_currentIndex, cellConsumer);
		}
		cellConsumer.end();
	}

	private void shiftCurrentIndex(int mark) {
		_currentIndex -= mark;
	}

	@Override
	public final boolean refillBuffer() throws IOException {
		shiftCurrentIndex(csvBuffer.shiftBufferToMark());
		return csvBuffer.fillBuffer();
	}

	private boolean isAllConsumedFromMark(int bufferIndex) {
		return (bufferIndex) <  (csvBuffer.getMark() + 1)  ;
	}

	private int unescape(final char[] chars, final int offset, final int length) {
		final char quoteChar = quoteChar();

		int start = offset + 1;
		int shiftedIndex = start;
		boolean notEscaped = true;

		int lastCharacter = offset + length - 1;

		// copy chars apart from escape chars
		for(int i = start; i < lastCharacter; i++) {
			notEscaped = chars[i] != quoteChar || !notEscaped;
			if (notEscaped) {
				chars[shiftedIndex++] = chars[i];
			}
		}

		// if last is not quote add to shifted char
		if (chars[(lastCharacter)] != quoteChar || !notEscaped) {
			chars[shiftedIndex++] = chars[(lastCharacter)];
		}

		return shiftedIndex - start;
	}
}
