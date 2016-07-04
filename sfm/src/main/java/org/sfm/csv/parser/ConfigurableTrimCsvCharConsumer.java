package org.sfm.csv.parser;


import java.io.IOException;

/**
 * Consume the charBuffer.
 */
public final class ConfigurableTrimCsvCharConsumer extends CsvCharConsumer {

	private static final int HAS_CONTENT = 16;
	private static final int NOTHING = 8;
	private static final int IN_CR = 4;
	private static final int QUOTE = 2;
	private static final int IN_QUOTE = 1;
	private static final int NONE = 0;
	private static final int TURN_OFF_NOTHING = ~NOTHING;
	private static final int TURN_OFF_IN_CR_MASK = ~IN_CR;
	private static final int ALL_QUOTES = QUOTE | IN_QUOTE;

	private final char separatorChar;
	private final char escapeChar;

	private final CharBuffer csvBuffer;
	private int _currentIndex;
	private int _currentState = NONE;

	public ConfigurableTrimCsvCharConsumer(CharBuffer csvBuffer, char separatorChar, char escapeChar) {
		this.separatorChar = separatorChar;
		this.escapeChar = escapeChar;
		this.csvBuffer = csvBuffer;
	}

	@Override
	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferSize();
		char[] chars = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		int currentState = _currentState;
		while(currentIndex  < bufferLength) {
			currentState = consumeOneChar(cellConsumer, currentIndex, currentState, chars[currentIndex]);
			currentIndex++;
		}

		_currentState = currentState;
		_currentIndex = currentIndex;
	}

	private int consumeOneChar(CellConsumer cellConsumer, int currentIndex, int currentState, char character) {
		if (character == separatorChar) {
			return newCellIfNotInQuote(currentIndex, currentState, cellConsumer);
		} else if (character ==  '\n') {
			return handleEndOfLineLF(currentIndex, currentState, cellConsumer);
		} else if (character == '\r') {
			return handleEndOfLineCR(currentIndex, currentState, cellConsumer);
		} else if (character == escapeChar) {
			return quote(currentState, currentIndex);
		} else if (character != ' ') {
			return currentState | HAS_CONTENT & TURN_OFF_IN_CR_MASK;
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
			if (character == separatorChar) {
				currentState = newCellIfNotInQuote(currentIndex, currentState, cellConsumer);
			} else if (character ==  '\n') {
				currentState = handleEndOfLineLF(currentIndex, currentState | NOTHING, cellConsumer);
				if (currentState == NONE) {
					_currentState = currentState;
					_currentIndex = currentIndex + 1;
					return true;
				}
				currentState &= TURN_OFF_NOTHING;
			} else if (character == '\r') {
				currentState = handleEndOfLineCR(currentIndex, currentState | NOTHING, cellConsumer);
				if (currentState == IN_CR) {
					_currentState = currentState;
					_currentIndex = currentIndex + 1;
					return true;
				}
				currentState &= TURN_OFF_NOTHING;
			} else if (character == escapeChar) {
				currentState = quote(currentState, currentIndex);
			}else if (character != ' ') {
             currentState = currentState | HAS_CONTENT & TURN_OFF_IN_CR_MASK;
			} else {
				currentState = currentState & TURN_OFF_IN_CR_MASK;
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

	private int quote(int currentState, int currentIndex) {
		if ((currentState & ALL_QUOTES) == 0) {
			if ((currentState & HAS_CONTENT) == 0) {
				csvBuffer.mark(currentIndex);
			}
			return (currentState ^ IN_QUOTE ) & TURN_OFF_IN_CR_MASK;

		} else {
			return (currentState ^  ALL_QUOTES) & TURN_OFF_IN_CR_MASK;

		}
	}

	private int newCell(int end, final CellConsumer cellConsumer) {
		char[] charBuffer = csvBuffer.getCharBuffer();
		int start = csvBuffer.getMark();

		if (charBuffer[start] != escapeChar) {
			int newStart = firstNonSpaceChar(charBuffer, start, end);
			int newEnd = lastNonSpaceChar(charBuffer, newStart, end) ;
			cellConsumer.newCell(charBuffer, newStart, newEnd - newStart);
		} else {
			newQuotedCell(charBuffer, start, end, cellConsumer);
		}

		csvBuffer.mark(end + 1);
		return NONE;

	}

	private void newQuotedCell(final char[] chars, final int offset, final int end, CellConsumer cellConsumer) {
		int start = offset + 1;

		int correctedEnd = end;
		while(chars[correctedEnd - 1]  == ' ' && correctedEnd > offset) {
			correctedEnd --;
		}

		boolean escaped = false;
		// copy chars apart from escape chars
		int skipIndex = 0;

		for (int i = start; i < correctedEnd - 1 ; i++) {
			int correctedIndex = i - skipIndex;
			escaped = escapeChar == chars[correctedIndex] && !escaped;
			if (escaped) {
				skipIndex ++;
				System.arraycopy(chars, correctedIndex + 1, chars, correctedIndex, correctedEnd - 1 - i);
			}
		}

		// if last is not quote add to shifted char
		if (escapeChar == chars[correctedEnd - skipIndex - 1] && !escaped) {
			skipIndex ++;
		}

		cellConsumer.newCell(chars, start, correctedEnd - start - skipIndex);
	}

	@Override
	public final void finish(CellConsumer cellConsumer) {
		int currentIndex = _currentIndex;
		if (isNotAllConsumedFromMark(currentIndex)) {
			newCell(currentIndex, cellConsumer);
		}
		cellConsumer.end();
	}

	@Override
	public final boolean refillBuffer() throws IOException {
		_currentIndex -= csvBuffer.shiftBufferToMark();
		return csvBuffer.fillBuffer();
	}

	private boolean isNotAllConsumedFromMark(int bufferIndex) {
		return (bufferIndex) >  (csvBuffer.getMark())  ;
	}

	private int lastNonSpaceChar(char[] charBuffer,int start, int end) {
		for(int i = end; i > start; i--) {
			if (charBuffer[i - 1] != ' ') return i;
		}
		return start;
	}

	private int firstNonSpaceChar(char[] charBuffer, int start, int end) {
		for(int i = start; i < end; i++) {
			if (charBuffer[i] != ' ') return i;
		}
		return end;
	}
}
