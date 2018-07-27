package org.simpleflatmapper.lightningcsv.parser;


import java.io.IOException;

/**
 * Consume the charBuffer.
 */
public final class ConfigurableCharConsumer extends AbstractCharConsumer {

	public static final int CONTAINS_ESCAPED_CHAR       = 256;
	public static final int ESCAPED                     = 128;
	public static final int ROW_DATA                    = 64;
	public static final int COMMENTED                   = 32;
	public static final int CELL_DATA                   = 16;
	public static final int QUOTED                      = 8;
	public static final int LAST_CHAR_WAS_SEPARATOR     = 4;
	public static final int LAST_CHAR_WAS_CR            = 2;
	public static final int QUOTED_AREA                 = 1;
	public static final int NONE                        = 0;

	private static final int TURN_OFF_LAST_CHAR_MASK = ~(LAST_CHAR_WAS_CR|LAST_CHAR_WAS_SEPARATOR);
	private static final int TURN_OFF_QUOTED_AREA = ~(QUOTED_AREA);
	private static final int TURN_OFF_ESCAPED = ~(ESCAPED);

	private static final char LF = '\n';
	private static final char CR = '\r';
	private static final char SPACE = ' ';
	private static final char COMMENT = '#';

	private final CharBuffer csvBuffer;
	private final TextFormat textFormat;
	private final CellPreProcessor cellPreProcessor;

	private int _currentIndex = 0;
	private int _currentState = NONE;

	public ConfigurableCharConsumer(CharBuffer csvBuffer, TextFormat textFormat, CellPreProcessor cellPreProcessor) {
		this.csvBuffer = csvBuffer;
		this.cellPreProcessor = cellPreProcessor;
		this.textFormat = textFormat;
	}

	@Override
	public final void consumeAllBuffer(final CellConsumer cellConsumer) {

		final boolean notIgnoreLeadingSpace = !ignoreLeadingSpace();
		final boolean yamlComment = yamlComment();
		final char escapeChar = escapeChar();
		final char separatorChar = separatorChar();
		final char quoteChar = quoteChar();

		int currentState = _currentState;
		int currentIndex = _currentIndex;

		final char[] chars = csvBuffer.buffer;
		final int bufferSize =  csvBuffer.bufferSize;

		while(currentIndex < bufferSize) {
			// unescaped loop
			if ((currentState & QUOTED_AREA) == 0) {
				if ((currentState & COMMENTED) == 0) {
					while (currentIndex < bufferSize) {
						final char character = chars[currentIndex];
						final int cellEnd = currentIndex;

						currentIndex++;

						if (character == separatorChar) { // separator
							cellPreProcessor.newCell(chars, csvBuffer.mark, cellEnd, cellConsumer, currentState);
							csvBuffer.mark = currentIndex;
							currentState = LAST_CHAR_WAS_SEPARATOR | ROW_DATA;
							continue;
						} else if (character == LF) { // \n
							if ((currentState & LAST_CHAR_WAS_CR) == 0) {
								cellPreProcessor.newCell(chars, csvBuffer.mark, cellEnd, cellConsumer, currentState);
								cellConsumer.endOfRow();
							}
							csvBuffer.mark = currentIndex;
							currentState = NONE;
							continue;
						} else if (character == CR) { // \r
							cellPreProcessor.newCell(chars, csvBuffer.mark, cellEnd, cellConsumer, currentState);
							csvBuffer.mark = currentIndex;
							currentState = LAST_CHAR_WAS_CR;
							cellConsumer.endOfRow();
							continue;
						} else if ((currentState & (QUOTED|CELL_DATA)) == (CELL_DATA)) {
							while(currentIndex < bufferSize) {
								final char c = chars[currentIndex];
								final int ce = currentIndex;
								currentIndex++;
								if (c == separatorChar || c == LF || c == CR) { // separator
									cellPreProcessor.newCell(chars, csvBuffer.mark, ce, cellConsumer, currentState);
									csvBuffer.mark = currentIndex;
									if (c == separatorChar) {
										currentState = LAST_CHAR_WAS_SEPARATOR | ROW_DATA;
									} else {
										currentState = (c == LF ? NONE : LAST_CHAR_WAS_CR );
										cellConsumer.endOfRow();
									}
									break;
								}
							}
							continue;
						} else if (((currentState ^ CELL_DATA) & (QUOTED | CELL_DATA)) != 0 && character == quoteChar) { // no cell data | quoted
							currentState =
									  QUOTED_AREA
									| QUOTED
									| ((currentState & QUOTED) << 5); // if already quoted it's a double quot need to escape QUOTED << 5 is  CONTAINS_ESCAPED_CHAR
							break;
						} else if (yamlComment && (currentState & (CELL_DATA | ROW_DATA)) == 0 && character == COMMENT) {
							currentState |= COMMENTED;
							break;
						}

						currentState &= TURN_OFF_LAST_CHAR_MASK;

						if (notIgnoreLeadingSpace || character != SPACE) {
							currentState |= CELL_DATA;
						}
					}
				} else { // comment
					int nextEndOfLineChar = findNexEndOfLineChar(chars, currentIndex, bufferSize);
					if (nextEndOfLineChar != -1) {
						cellPreProcessor.newCell(chars, csvBuffer.mark, nextEndOfLineChar, cellConsumer, currentState);
						cellConsumer.endOfRow();
						currentIndex = nextEndOfLineChar + 1;
						csvBuffer.mark = currentIndex;
						currentState = chars[nextEndOfLineChar] == CR ? LAST_CHAR_WAS_CR : NONE;
					} else {
						currentIndex = bufferSize;
					}
				}
			} else {
				// escaped area
				while(currentIndex < bufferSize) {
					if ((currentState & ESCAPED) == 0) {
						char c = chars[currentIndex];
						if (c == quoteChar) {
							currentIndex++;
							currentState &= TURN_OFF_QUOTED_AREA;
							break;
						} else if (c == escapeChar) {
							currentState |= ESCAPED | CONTAINS_ESCAPED_CHAR;
						}
					} else {
						currentState &= TURN_OFF_ESCAPED;
					}
					currentIndex ++;
				}
			}
		}

		_currentState = currentState;
		_currentIndex = currentIndex;
	}

	@Override
	public final boolean consumeToNextRow(CellConsumer cellConsumer) {
		final boolean notIgnoreLeadingSpace = !ignoreLeadingSpace();
		final boolean yamlComment = yamlComment();
		final char escapeChar = escapeChar();
		final char separatorChar = separatorChar();
		final char quoteChar = quoteChar();

		int currentState = _currentState;
		int currentIndex = _currentIndex;

		final char[] chars = csvBuffer.buffer;
		final int bufferSize =  csvBuffer.bufferSize;

		while(currentIndex < bufferSize) {
			// unescaped loop
			if ((currentState & QUOTED_AREA) == 0) {
				if ((currentState & COMMENTED) == 0) {
					while(currentIndex < bufferSize) {
						final char character = chars[currentIndex];
						final int cellEnd = currentIndex;

						currentIndex ++;

						if (character == separatorChar) { // separator
							cellPreProcessor.newCell(chars, csvBuffer.mark, cellEnd, cellConsumer, currentState);
							csvBuffer.mark = currentIndex;
							currentState = LAST_CHAR_WAS_SEPARATOR | ROW_DATA;
							continue;
						} else if (character == LF) { // \n
							if ((currentState & LAST_CHAR_WAS_CR) == 0) {
								cellPreProcessor.newCell(chars, csvBuffer.mark, cellEnd, cellConsumer, currentState);
								if (cellConsumer.endOfRow()) {
									csvBuffer.mark = currentIndex;
									_currentState = NONE;
									_currentIndex = currentIndex;
									return true;
								}
							}
							csvBuffer.mark = currentIndex;
							currentState = NONE;
							continue;
						} else if (character == CR) { // \r
							cellPreProcessor.newCell(chars, csvBuffer.mark, cellEnd, cellConsumer, currentState);
							csvBuffer.mark = currentIndex;
							currentState = LAST_CHAR_WAS_CR;
							if (cellConsumer.endOfRow()) {
								_currentState = currentState;
								_currentIndex = currentIndex;
								return true;
							}
							continue;
						} else if ((currentState & (QUOTED|CELL_DATA)) == (CELL_DATA)) {
							while(currentIndex < bufferSize) {
								final char c = chars[currentIndex];
								final int ce = currentIndex;
								currentIndex++;
								if (c == separatorChar || c == LF || c == CR) { // separator
									cellPreProcessor.newCell(chars, csvBuffer.mark, ce, cellConsumer, currentState);
									csvBuffer.mark = currentIndex;
									if (c == separatorChar) {
										currentState = LAST_CHAR_WAS_SEPARATOR | ROW_DATA;
									} else {
										currentState = c == LF ? NONE : LAST_CHAR_WAS_CR;
										if (cellConsumer.endOfRow()) {
											_currentState = currentState;
											_currentIndex = currentIndex;
											return true;
										}
									}
									break;
								}
							}
							continue;
						} else if (((currentState ^ CELL_DATA) & (QUOTED | CELL_DATA)) != 0 && character == quoteChar ) { // no cell data | quoted
							currentState =
									QUOTED_AREA
											| QUOTED
											| ((currentState & QUOTED) << 5); // if already quoted it's a double quot need to escape QUOTED << 5 is  CONTAINS_ESCAPED_CHAR
							break;
						} else if ((currentState & (CELL_DATA | ROW_DATA)) == 0 && yamlComment && character == COMMENT) { // no cell data or row data
							currentState |= COMMENTED;
							break;
						}

						currentState &= TURN_OFF_LAST_CHAR_MASK;

						if (notIgnoreLeadingSpace || character != SPACE) {
							currentState |= CELL_DATA;
						}
					}
				} else {
					int nextEndOfLineChar = findNexEndOfLineChar(chars, currentIndex, bufferSize);
					if (nextEndOfLineChar != -1) {
						currentIndex = nextEndOfLineChar + 1;
						cellPreProcessor.newCell(chars, csvBuffer.mark, nextEndOfLineChar, cellConsumer, currentState);
						csvBuffer.mark = currentIndex;
						currentState = chars[nextEndOfLineChar] == CR ? LAST_CHAR_WAS_CR : NONE;
						if (cellConsumer.endOfRow()) {
							_currentState = currentState;
							_currentIndex = currentIndex;
							return true;
						}
					} else {
						currentIndex = bufferSize;
					}
				}
			} else {
				// escaped area
				while(currentIndex < bufferSize) {
					if ((currentState & ESCAPED) == 0) {
						char c = chars[currentIndex];
						if (c == quoteChar) {
							currentIndex++;
							currentState &= TURN_OFF_QUOTED_AREA;
							break;
						} else if (c == escapeChar) {
							currentState |= ESCAPED | CONTAINS_ESCAPED_CHAR;
						}
					} else {
						currentState &= TURN_OFF_ESCAPED;
					}
					currentIndex ++;
				}
			}
		}

		_currentState = currentState;
		_currentIndex = currentIndex;

		return false;
	}

	private char quoteChar() {
		return textFormat.quoteChar;
	}

	private boolean yamlComment() {
		return textFormat.yamlComment;
	}

	private char separatorChar() {
		return textFormat.separatorChar;
	}

	private char escapeChar() {
		return textFormat.escapeChar;
	}

	private boolean ignoreLeadingSpace() {
		return cellPreProcessor.ignoreLeadingSpace();
	}

	private int _moveToNextSeparator(char[] chars, char separatorChar, int currentIndex, int bufferSize) {
		while(currentIndex < bufferSize) {
			final char c = chars[currentIndex];
			if (c == separatorChar || c == CR || c == LF) {
				return currentIndex;
			}
			currentIndex++;
		}
		return currentIndex;
	}

	private int findNexEndOfLineChar(char[] chars, int start, int end) {
		for(int i = start; i < end; i++) {
			char c = chars[i];
			if (c == CR || c == LF) return i;
		}
		return -1;
	}

	@Override
	public final void finish(CellConsumer cellConsumer) {
		if ( hasUnconsumedData()
				|| (_currentState & LAST_CHAR_WAS_SEPARATOR) != 0) {
			cellPreProcessor.newCell(csvBuffer.buffer, csvBuffer.mark, _currentIndex, cellConsumer, _currentState);
			csvBuffer.mark = _currentIndex + 1;
			_currentState = NONE;
		}
		cellConsumer.end();
	}

	private boolean hasUnconsumedData() {
		return _currentIndex > csvBuffer.mark;
	}

	@Override
	public boolean next() throws IOException {
		int mark = csvBuffer.mark;
		boolean b = csvBuffer.next();
		_currentIndex -= mark - csvBuffer.mark;
		return b;
	}
}
