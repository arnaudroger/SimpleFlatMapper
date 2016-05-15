package org.sfm.csv.parser;


/**
 * Consume the charBuffer.
 */
public final class ConfigurableTrimCsvCharConsumer extends AbstractCsvCharConsumer {

	private final char separatorChar;

	public ConfigurableTrimCsvCharConsumer(CharBuffer csvBuffer, char separatorChar, char quoteChar) {
		super(csvBuffer, quoteChar);
		this.separatorChar = separatorChar;
	}

	@Override
	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.bufferSize;
		char[] chars = csvBuffer.buffer;
		int currentIndex = _currentIndex;
		while(currentIndex  < bufferLength) {
			consumeOneChar(currentIndex, chars[currentIndex], cellConsumer);
			currentIndex++;
		}

		_currentIndex = currentIndex;
	}

	private void consumeOneChar(int currentIndex, char character, CellConsumer cellConsumer) {
		if (character == separatorChar) {
			newCellIfNotInQuote(currentIndex, cellConsumer);
		} else if (character ==  '\n') {
				handleEndOfLineLF(currentIndex, cellConsumer);
		} else if (character == '\r') {
			handleEndOfLineCR(currentIndex, cellConsumer);
			return;
		} else if (character == quoteChar) {
			quote(currentIndex);
		} else if (character != ' ') {
			currentState |= HAS_CONTENT;
		}
		turnOffCrFlag();
	}


	protected final void quote(int currentIndex) {
		if (isAllConsumedFromMark(currentIndex)) {
			currentState |= IN_QUOTE;
		} else if ((currentState & (HAS_CONTENT | ALL_QUOTES)) != 0) {
			currentState ^= ALL_QUOTES;
		} else {
			currentState |= IN_QUOTE;
			csvBuffer.mark(currentIndex);
		}
	}

	private boolean isAllConsumedFromMark(int bufferIndex) {
		return (bufferIndex) <  (csvBuffer.getMark() + 1)  ;
	}


	@Override
	public boolean consumeToNextRow(CellConsumer cellConsumer) {

		int bufferLength = csvBuffer.getBufferSize();
		char[] buffer = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		for(; currentIndex  < bufferLength; currentIndex++) {

			char character = buffer[currentIndex];

			if (character == separatorChar) {
				newCellIfNotInQuote(currentIndex, cellConsumer);
			} else if (character ==  '\n') {
				if (handleEndOfLineLF(currentIndex, cellConsumer)) {
					_currentIndex = currentIndex + 1;
					turnOffCrFlag();
					return true;
				}
			} else if (character == '\r') {
				if (handleEndOfLineCR(currentIndex, cellConsumer)) {
					_currentIndex = currentIndex + 1;
					return true;
				}
			} else if (character == quoteChar) {
				quote(currentIndex);
			} else if (character != ' ') {
				currentState |= HAS_CONTENT;
			}
			turnOffCrFlag();
		}

		_currentIndex = currentIndex;
		return false;
	}

	@Override
	protected void newCell(int currentIndex, CellConsumer cellConsumer) {
		char[] charBuffer = csvBuffer.getCharBuffer();
		int start = csvBuffer.getMark();
		int length = currentIndex - start;

		if (charBuffer[start] == quoteChar) {
			length = unescape(charBuffer, start, length, quoteChar);
			start++;
		} else {
			int newStart = firstNonSpaceChar(charBuffer, start, length);
			length = length - newStart + start;
			start = newStart;

			length = lastNonSpaceChar(charBuffer, start + length, start) - start;
		}

		cellConsumer.newCell(charBuffer, start, length);
		csvBuffer.mark(currentIndex + 1);
		currentState = NONE;
	}

	private int lastNonSpaceChar(char[] charBuffer, int end, int start) {
		for(int i = end; i > start; i--) {
			if (charBuffer[i - 1] != ' ') return i;
		}
		return start;
	}

	private int firstNonSpaceChar(char[] charBuffer, int start, int length) {
		for(int i = start; i < start + length; i++) {
			if (charBuffer[i] != ' ') return i;
		}
		return start + length;
	}

	protected int unescape(final char[] chars, final int offset, final int length, char quoteChar) {
		int start = offset + 1;
		int shiftedIndex = start;
		boolean notEscaped = true;

		int lastCharacter = offset + length - 1;

		while(chars[lastCharacter]  == ' ' && lastCharacter > offset) {
			lastCharacter --;
		}

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
