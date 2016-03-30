package org.sfm.csv.parser;


/**
 * Consume the charBuffer.
 */
public final class ConfigurableTrimCsvCharConsumer extends AbstractCsvCharConsumer {

	private final char separatorChar;
	private final char quoteChar;

	public ConfigurableTrimCsvCharConsumer(CharBuffer csvBuffer, char separatorChar, char quoteChar) {
		super(csvBuffer);
		this.separatorChar = separatorChar;
		this.quoteChar = quoteChar;
	}

	protected void consumeOneChar(char character, int index, CellConsumer cellConsumer) {
		if (character == separatorChar) {
			newCellIfNotInQuote(index, cellConsumer);
		} else if (character ==  '\n') {
				handleEndOfLineLF(index, cellConsumer);
		} else if (character == '\r') {
			handleEndOfLineCR(index, cellConsumer);
			return;
		} else if (character == quoteChar) {
			quote(index);
		} else if (character != ' ') {
			currentState |= HAS_CONTENT;
		}
		turnOffCrFlag();
	}


	protected final void quote(int currentIndex) {
		if (isAllConsumedFromMark(currentIndex)) {
			currentState = IN_QUOTE;
		} else if ((currentState & (HAS_CONTENT | ALL_QUOTES)) == 0) {
			currentState = IN_QUOTE;
			csvBuffer.mark(currentIndex);
		} else {
			currentState ^= ALL_QUOTES;
		}
	}

	@Override
	public boolean consumeToNextRow(CellConsumer cellConsumer) {

		int bufferLength = csvBuffer.getBufferSize();
		for(int index = _currentIndex; index  < bufferLength; index++) {

			char character = csvBuffer.getChar(index);

			if (character == separatorChar) {
				newCellIfNotInQuote(index, cellConsumer);
			} else if (character ==  '\n') {
				if (handleEndOfLineLF(index, cellConsumer)) {
					_currentIndex = index + 1;
					turnOffCrFlag();
					return true;
				}
			} else if (character == '\r') {
				if (handleEndOfLineCR(index, cellConsumer)) {
					_currentIndex = index + 1;
					return true;
				}
			} else if (character == quoteChar) {
				quote(index);
			} else if (character != ' ') {
				currentState |= HAS_CONTENT;
			}
			turnOffCrFlag();
		}
		_currentIndex = bufferLength;

		return false;
	}

	protected void newCell(int currentIndex, CellConsumer cellConsumer) {
		char[] charBuffer = csvBuffer.getCharBuffer();
		int start = csvBuffer.getMark();
		int length = currentIndex - start;

		if (charBuffer[start] == quoteChar()) {
			length = unescape(charBuffer, start, length);
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
		return start + length - 1;
	}

	protected int unescape(final char[] chars, final int offset, final int length) {
		final char quoteChar = quoteChar();

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

	@Override
	public char quoteChar() {
		return quoteChar;
	}


}
