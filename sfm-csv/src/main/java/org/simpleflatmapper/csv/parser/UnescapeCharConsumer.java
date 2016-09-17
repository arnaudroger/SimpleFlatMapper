package org.simpleflatmapper.csv.parser;



/**
 * Consume the charBuffer.
 */
public class UnescapeCharConsumer extends CharConsumer {


	protected final char separatorChar;
	protected final char escapeChar;

	public UnescapeCharConsumer(CharBuffer csvBuffer, char separatorChar, char escapeChar) {
		super(csvBuffer);
		this.separatorChar = separatorChar;
		this.escapeChar = escapeChar;
	}

	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferSize();
		char[] chars = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		int currentState = _currentState;
		for(;currentIndex  < bufferLength; currentIndex++) {
			char character = chars[currentIndex];
			if (character != escapeChar) {
				if ((currentState & ESCAPED) == 0) {
					if (character == separatorChar) {
						newCell(currentIndex, cellConsumer);
						currentState = LAST_CHAR_WAS_SEPARATOR;
						continue;
					} else if (character == '\n') {
						if ((currentState & LAST_CHAR_WAS_CR) == 0) {
							endOfRow(currentIndex, cellConsumer);
							currentState = NONE;
							continue;
						} else {
							csvBuffer.mark = (currentIndex + 1);
						}
					} else if (character == '\r') {
						endOfRow(currentIndex, cellConsumer);
						currentState = LAST_CHAR_WAS_CR;
						continue;
					}
				}
				currentState &= TURN_OFF_LAST_CHAR_MASK;
			} else {
				currentState ^= ESCAPED;
			}
		}
		_currentState = currentState;
		_currentIndex = currentIndex;
	}

	public final boolean consumeToNextRow(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferSize();
		char[] chars = csvBuffer.getCharBuffer();
		int currentIndex = _currentIndex;
		int currentState = _currentState;
		for(;currentIndex  < bufferLength; currentIndex ++) {
			char character = chars[currentIndex];

			if (character != escapeChar) {
				if ((currentState & ESCAPED) == 0) {
					if (character == separatorChar) {
						newCell(currentIndex, cellConsumer);
						currentState = NONE;
						continue;
					} else if (character == '\n') {
						if ((currentState & LAST_CHAR_WAS_CR) == 0) {
							if(endOfRow(currentIndex, cellConsumer)) {
								_currentIndex = currentIndex + 1;
								_currentState = NONE;
								return true;
							}
							currentState = NONE;
							continue;
						} else {
							csvBuffer.mark(currentIndex + 1);
						}
					} else if (character == '\r') {
						if(endOfRow(currentIndex, cellConsumer)) {
							_currentIndex = currentIndex + 1;
							_currentState = LAST_CHAR_WAS_CR;
							return true;
						}
						currentState = LAST_CHAR_WAS_CR;
						continue;
					}
				}
				currentState &= TURN_OFF_LAST_CHAR_MASK;
			} else {
				currentState ^= ESCAPED;
			}
		}
		_currentState = currentState;
		_currentIndex = currentIndex;
		return false;
	}

	protected void newCell(int end, final CellConsumer cellConsumer) {
		char[] chars = csvBuffer.buffer;
		int strStart = csvBuffer.mark;
		int strEnd = end;

		if (strStart < strEnd && chars[strStart] == escapeChar) {
			strStart ++;
			strEnd = unescape(chars, strStart, strEnd);
		}
		cellConsumer.newCell(chars, strStart, strEnd - strStart);

		csvBuffer.mark = (end + 1);
	}

	protected final int unescape(final char[] chars, final int start, final int end) {
		for(int i = start; i < end - 1; i ++) {
			if (chars[i] == escapeChar) {
				return removeEscapeChars(chars, end, i);
			}
		}

		if (start < end && escapeChar == chars[end - 1]) {
			return end - 1;
		}

		return end;
	}

	private int removeEscapeChars(final char[] chars, final int end, final int firstEscapeChar) {
		int j = firstEscapeChar;
		boolean escaped = true;
		for(int i = firstEscapeChar + 1;i < end; i++) {
			escaped = chars[i] == escapeChar && ! escaped;
			if (!escaped) {
				chars[j++] = chars[i];
			}
		}
		return j;
	}
}
