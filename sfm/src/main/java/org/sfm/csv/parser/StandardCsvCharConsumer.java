package org.sfm.csv.parser;


import java.io.IOException;

/**
 * Consume the charBuffer.
 */
public final class StandardCsvCharConsumer extends CsvCharConsumer {

	private static final int NOTHING = 8;
	private static final int IN_CR = 4;
	private static final int QUOTE = 2;
	private static final int IN_QUOTE = 1;
	private static final int NONE = 0;
	private static final int TURN_OFF_NOTHING = ~NOTHING;
	private static final int TURN_OFF_IN_CR_MASK = ~IN_CR;
	private static final int ALL_QUOTES = QUOTE | IN_QUOTE;

	private static final char QUOTE_CHAR = '"';

	private final CharBuffer csvBuffer;
	private int _currentIndex;
	private int _currentState = NONE;

	public StandardCsvCharConsumer(CharBuffer csvBuffer) {
		this.csvBuffer = csvBuffer;
	}

	@Override
	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferSize();
		char[] chars = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		int currentState = _currentState;
		while(currentIndex  < bufferLength) {
			char character = chars[currentIndex];
			currentState = consumeOneChar(cellConsumer, currentIndex, currentState, character);
			currentIndex++;
		}
		_currentState = currentState;
		_currentIndex = currentIndex;
	}

	private int consumeOneChar(CellConsumer cellConsumer, int currentIndex, int currentState, char character) {
		switch(character) {
            case ',':
				return newCellIfNotInQuote(currentIndex, currentState, cellConsumer);
            case '\n':
				return handleEndOfLineLF(currentIndex, currentState, cellConsumer);
            case '\r':
                return handleEndOfLineCR(currentIndex, currentState, cellConsumer);
            case QUOTE_CHAR:
				return quote(currentState);
        }
		return currentState & TURN_OFF_IN_CR_MASK;
	}

	@Override
	public boolean consumeToNextRow(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferSize();
		char[] chars = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		int currentState = _currentState;
		while(currentIndex  < bufferLength) {
			char character = chars[currentIndex];
			switch(character) {
				case ',':
					currentState = newCellIfNotInQuote(currentIndex, currentState, cellConsumer);
					break;
				case '\n':
					currentState = handleEndOfLineLF(currentIndex, currentState | NOTHING, cellConsumer);
					if (currentState == NONE) {
						_currentState = currentState;
						_currentIndex = currentIndex + 1;
						return true;
					}
					currentState &= TURN_OFF_NOTHING;
					break;
				case '\r':
					currentState = handleEndOfLineCR(currentIndex, currentState | NOTHING, cellConsumer);
					if (currentState == IN_CR) {
						_currentState = currentState;
						_currentIndex = currentIndex + 1;
						return true;
					}
					currentState &= TURN_OFF_NOTHING;
					break;
				case QUOTE_CHAR:
					currentState = quote(currentState);
					break;
				default:
					currentState &= TURN_OFF_IN_CR_MASK;
			}
			currentIndex++;
		}
		_currentState = currentState;
		_currentIndex = currentIndex;
		return false;
	}


	private int newCellIfNotInQuote(int currentIndex, int currentState, CellConsumer cellConsumer) {
		if ((currentState &  IN_QUOTE) != 0) return currentState & TURN_OFF_IN_CR_MASK;
		return newCell(currentIndex, cellConsumer);
	}

	private int handleEndOfLineLF(int currentIndex, int currentState, CellConsumer cellConsumer) {
		final int inQuoteAndCr = currentState & (IN_QUOTE | IN_CR);
		if (inQuoteAndCr == IN_CR) {
			// we had a preceding cr so shift the mark
			csvBuffer.mark(currentIndex + 1);
		} else if (inQuoteAndCr == 0) {
			return endOfRow(currentIndex, cellConsumer);
		}
		return currentState & TURN_OFF_IN_CR_MASK;
	}

	private int handleEndOfLineCR(int currentIndex, int currentState, CellConsumer cellConsumer) {
		if ((currentState &  IN_QUOTE) == 0) {
			endOfRow(currentIndex, cellConsumer);
			return IN_CR;
		}
		return currentState;
	}

	private int endOfRow(int currentIndex, CellConsumer cellConsumer) {
		newCell(currentIndex, cellConsumer);
		cellConsumer.endOfRow();
		return NONE;
	}

	private int quote(int currentState) {
		return (currentState ^ ((currentState & ALL_QUOTES) == 0 ? IN_QUOTE : ALL_QUOTES)) & TURN_OFF_IN_CR_MASK;
	}

	private int newCell(int end, final CellConsumer cellConsumer) {
		char[] charBuffer = csvBuffer.getCharBuffer();
		int strStart = csvBuffer.getMark();
		int strEnd = end;
		if (charBuffer[strStart] == QUOTE_CHAR) {
			strStart ++;
			strEnd -= unescape(charBuffer, strStart, end);
		}
		cellConsumer.newCell(charBuffer, strStart, strEnd - strStart);
		csvBuffer.mark(end + 1);
		return NONE;
	}

	private int unescape(final char[] chars, final int offset, final int end) {
		int skipIndex = 0;
		boolean escaped = false;
		for (int i = offset; i < end - 1 ; i++) {
			int correctedIndex = i - skipIndex;
			escaped = QUOTE_CHAR == chars[correctedIndex] && !escaped;
			if (escaped) {
				System.arraycopy(chars, correctedIndex + 1, chars, correctedIndex, end - 1 - i);
				skipIndex ++;
			}
		}

		// if last is not quote add to shifted char
		if (QUOTE_CHAR == chars[end - skipIndex - 1] && !escaped) {
			  skipIndex ++;
		}

		return skipIndex;
	}

	@Override
	public final void finish(CellConsumer cellConsumer) {
		if (isNotAllConsumedFromMark()) {
			newCell(_currentIndex, cellConsumer);
		}
		cellConsumer.end();
	}

	@Override
	public final boolean refillBuffer() throws IOException {
		_currentIndex -= csvBuffer.shiftBufferToMark();
		return csvBuffer.fillBuffer();
	}

	private boolean isNotAllConsumedFromMark() {
		return _currentIndex > csvBuffer.getMark();
	}



}
