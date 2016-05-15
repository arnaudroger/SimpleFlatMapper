package org.sfm.csv.parser;

import java.io.IOException;

public abstract class AbstractCsvCharConsumer extends CsvCharConsumer {
	protected static final int HAS_CONTENT = 8;
	protected static final int IN_QUOTE = 4;
	protected static final int IN_CR = 2;
	protected static final int QUOTE = 1;
	protected static final int NONE = 0;
	protected static final int TURN_OFF_IN_CR_MASK = ~IN_CR;
	protected static final int ALL_QUOTES = QUOTE | IN_QUOTE;

	protected final CharBuffer csvBuffer;
	protected final char quoteChar;
	protected int _currentIndex;
	protected int currentState = NONE;

	public AbstractCsvCharConsumer(CharBuffer csvBuffer, char quoteChar) {
		this.csvBuffer = csvBuffer;
		this.quoteChar = quoteChar;
	}

	/**
	 * use bit mask to testing if == IN_CR
	 */
	protected final void turnOffCrFlag() {
		currentState &= TURN_OFF_IN_CR_MASK;
	}

	protected final void newCellIfNotInQuote(int currentIndex, CellConsumer cellConsumer) {
		if ((currentState &  IN_QUOTE) != 0) return;
		newCell(currentIndex, cellConsumer);
	}

	protected final boolean handleEndOfLineLF(int currentIndex, CellConsumer cellConsumer) {
		final int inQuoteAndCr = this.currentState & (IN_QUOTE | IN_CR);
		if (inQuoteAndCr == IN_CR) {
			// we had a preceding cr so shift the mark
			csvBuffer.mark(currentIndex + 1);
			return false;
		} else if (inQuoteAndCr == 0) {
			endOfRow(currentIndex, cellConsumer);
			return true;
		}
		return false;
	}

	protected final boolean handleEndOfLineCR(int currentIndex, CellConsumer cellConsumer) {
		if ((currentState &  IN_QUOTE) == 0) {
			endOfRow(currentIndex, cellConsumer);
			currentState |= IN_CR;
			return true;
		}
		return false;
	}

	private void endOfRow(int currentIndex, CellConsumer cellConsumer) {
		newCell(currentIndex, cellConsumer);
		cellConsumer.endOfRow();
	}

	protected void quote(int currentIndex) {
		if (isNotAllConsumedFromMark(currentIndex)) {
			currentState ^= ALL_QUOTES;
		} else {
			currentState |= IN_QUOTE;
		}
	}

	protected void newCell(int end, final CellConsumer cellConsumer) {
		char[] charBuffer = csvBuffer.getCharBuffer();
		int start = csvBuffer.getMark();

		if (charBuffer[start] != quoteChar) {
			cellConsumer.newCell(charBuffer, start, end - start);
		} else {
			newEscapedCell(charBuffer, start, end - start, cellConsumer);
		}

		csvBuffer.mark(end + 1);
		currentState = NONE;
	}

	private void newEscapedCell(final char[] chars, final int offset, final int length, CellConsumer cellConsumer) {
		int start = offset + 1;
		int shiftedIndex = start;
		boolean escaped = false;

		int lastCharacter = offset + length - 1;


		// copy chars apart from escape chars
		for(int i = start; i < lastCharacter; i++) {
			escaped = quoteChar == chars[i] && !escaped;
			if (!escaped) {
				chars[shiftedIndex++] = chars[i];
			}
		}

		// if last is not quote add to shifted char
		if (quoteChar != chars[lastCharacter] || escaped) {
			chars[shiftedIndex++] = chars[lastCharacter];
		}

		cellConsumer.newCell(chars, start, shiftedIndex - start);
	}

	@Override
	public final void finish(CellConsumer cellConsumer) {
		int currentIndex = _currentIndex;
		if (isNotAllConsumedFromMark(currentIndex)) {
			newCell(currentIndex, cellConsumer);
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

	protected final boolean isNotAllConsumedFromMark(int bufferIndex) {
		return (bufferIndex) >=  (csvBuffer.getMark() + 1)  ;
	}


}
