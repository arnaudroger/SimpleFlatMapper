package org.sfm.csv.parser;

import java.io.IOException;
import java.io.Reader;

public abstract class AbstractCsvCharConsumer implements CsvCharConsumer {
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
	public final void parseAll(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferLength();
		for(int i = _currentIndex; i  < bufferLength; i++) {
			char c = csvBuffer.getChar(i);
			consumeOneChar(c, i, cellConsumer);
		}
		_currentIndex = bufferLength;
	}

	protected abstract void consumeOneChar(char c, int i, CellConsumer cellConsumer);

	/**
	 * use bit mask to testing if == IN_CR
	 */
	protected final void turnOffCrFlag() {
		currentState = currentState & TURN_OFF_IN_CR_MASK;
	}

	protected final void newCellIfNotInQuote(int currentIndex, CellConsumer cellConsumer) {
		if (isInQuote())
			return;

		newCell(currentIndex, cellConsumer);
	}

	private boolean isInQuote() {
		return currentState == IN_QUOTE;
	}

	/**
	 *
	 * @return true if endOfLine
	 */
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
			currentState = currentState ^ ALL_QUOTES;
		}
	}

	private void newCell(int currentIndex, CellConsumer cellConsumer) {
		char[] charBuffer = csvBuffer.getCharBuffer();
		int start = csvBuffer.getMark();
		int length = currentIndex - start;

		if (charBuffer[start] == quoteChar()) {
			start ++;
			length = unescape(charBuffer, start, length);
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
	public final boolean fillBuffer(Reader reader) throws IOException {
		shiftCurrentIndex(csvBuffer.shiftBufferToMark());
		return csvBuffer.fillBuffer(reader);
	}

	private boolean isAllConsumedFromMark(int bufferIndex) {
		return (bufferIndex) <  (csvBuffer.getMark() + 1)  ;
	}

	private int unescape(char[] chars, int offset, int length) {
		final char quoteChar = quoteChar();


		int j = offset + 1;
		boolean notEscaped = true;

		for(int i = offset + 1; i < offset + length -1; i++) {
			notEscaped = chars[i] != quoteChar || !notEscaped;
			if (notEscaped) {
				chars[j++] = chars[i];
			}
		}

		if (chars[offset + length -1] != quoteChar) {
			chars[j++] = chars[offset + length -1];
		}

		return j - offset - 1;
	}
}
