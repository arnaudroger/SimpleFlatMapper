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
	public CharBuffer charBuffer() {
		return csvBuffer;
	}

	@Override
	public final void consumeAllBuffer(final CellConsumer cellConsumer) {

		final boolean notIgnoreLeadingSpace = !ignoreLeadingSpace();
		final boolean yamlComment = yamlComment();
		final char escapeChar = escapeChar();
		final char separatorChar = separatorChar();
		final char quoteChar = quoteChar();
		final int separatorFingerPrint = (separatorChar & CR & LF);
		final int separatorFingerPrintMask = separatorFingerPrint | ((~separatorChar) & (~CR) & (~LF));

		int currentState = _currentState;
		int currentIndex = _currentIndex;

		final char[] chars = csvBuffer.buffer;
		final int bufferSize = csvBuffer.bufferSize;

		final CellPreProcessor cellPreProcessor = this.cellPreProcessor;
		if (bufferSize > chars.length) throw new ArrayIndexOutOfBoundsException();

		try {
			
			mainloop:
			while (currentIndex < bufferSize) {
				// unescaped loop
				if ((currentState & (QUOTED_AREA| COMMENTED)) == 0) {
					nonquotedloop:
					while (currentIndex < bufferSize) {
						final char character = chars[currentIndex];
						final int cellEnd = currentIndex;

						currentIndex++;
						if (character == separatorChar) { // separator
							cellPreProcessor.newCell(chars, csvBuffer.cellStartMark, cellEnd, cellConsumer, currentState);
							csvBuffer.cellStartMark = currentIndex;
							currentState = LAST_CHAR_WAS_SEPARATOR | ROW_DATA;
							continue;
						} else if (character == LF) { // \n
							if ((currentState & LAST_CHAR_WAS_CR) == 0) {
								cellPreProcessor.newCell(chars, csvBuffer.cellStartMark, cellEnd, cellConsumer, currentState);
								cellConsumer.endOfRow();
							}
							markEndOfRow(currentIndex);
							currentState = NONE;
							continue;
						} else if (character == CR) { // \r
							cellPreProcessor.newCell(chars, csvBuffer.cellStartMark, cellEnd, cellConsumer, currentState);
							cellConsumer.endOfRow();
							markEndOfRow(currentIndex);
							currentState = LAST_CHAR_WAS_CR;
							continue;
						}

						if ((currentState & (QUOTED | CELL_DATA)) == (CELL_DATA)) {
							while (currentIndex < bufferSize) {
								final char c = chars[currentIndex];
								final int ce = currentIndex;
								currentIndex++;
								if (((c & separatorFingerPrintMask) == separatorFingerPrint) && (c == separatorChar || c == LF || c == CR)) { // separator
									cellPreProcessor.newCell(chars, csvBuffer.cellStartMark, ce, cellConsumer, currentState);
									if (c == separatorChar) {
										currentState = LAST_CHAR_WAS_SEPARATOR | ROW_DATA;
									} else {
										currentState = (c == LF ? NONE : LAST_CHAR_WAS_CR);
										cellConsumer.endOfRow();
										csvBuffer.rowStartMark = currentIndex;
									}
									csvBuffer.cellStartMark = currentIndex;
									continue nonquotedloop;
								}
							}
							return;
						}

						if (((currentState ^ CELL_DATA) & (QUOTED | CELL_DATA)) != 0 && character == quoteChar) { // no cell data | quoted
							currentState =
									QUOTED_AREA
											| QUOTED
											| ((currentState & QUOTED) << 5); // if already quoted it's a double quot need to escape QUOTED << 5 is  CONTAINS_ESCAPED_CHAR
							break;
						}

						if (yamlComment && (currentState & (CELL_DATA | ROW_DATA)) == 0 && character == COMMENT) {
							currentState |= COMMENTED;
							break;
						}

						currentState &= TURN_OFF_LAST_CHAR_MASK;

						if (notIgnoreLeadingSpace || character != SPACE) {
							currentState |= CELL_DATA;
						}
					}
				} else if ((currentState & (QUOTED_AREA)) != 0){
					// escaped area
					while (currentIndex < bufferSize) {
						if ((currentState & ESCAPED) == 0) {
							char c = chars[currentIndex++];
							if (c == quoteChar) {
								currentState &= TURN_OFF_QUOTED_AREA;
								continue mainloop;
							} else if (c == escapeChar) {
								currentState |= ESCAPED | CONTAINS_ESCAPED_CHAR;
							}
						} else {
							currentState &= TURN_OFF_ESCAPED;
						}
					}
					return;
				} else { // comment
					int nextEndOfLineChar = findNexEndOfLineChar(chars, currentIndex, bufferSize);
					if (nextEndOfLineChar != -1) {
						cellPreProcessor.newCell(chars, csvBuffer.cellStartMark, nextEndOfLineChar, cellConsumer, currentState);
						cellConsumer.endOfRow();
						currentIndex = nextEndOfLineChar + 1;
						markEndOfRow(currentIndex);
						currentState = chars[nextEndOfLineChar] == CR ? LAST_CHAR_WAS_CR : NONE;
					} else {
						currentIndex = bufferSize;
					}
				}
			}
		} finally {
			_currentState = currentState;
			_currentIndex = currentIndex;
		}
	}

	@Override
	public final boolean consumeToNextRow(CellConsumer cellConsumer) {
		final boolean notIgnoreLeadingSpace = !ignoreLeadingSpace();
		final boolean yamlComment = yamlComment();
		final char escapeChar = escapeChar();
		final char separatorChar = separatorChar();
		final char quoteChar = quoteChar();
		
		final int separatorFingerPrint = (separatorChar & CR & LF);
		final int separatorFingerPrintMask = separatorFingerPrint | ((~separatorChar) & (~CR) & (~LF));

		int currentState = _currentState;
		int currentIndex = _currentIndex;
		
		final char[] chars = csvBuffer.buffer;
		final int bufferSize = csvBuffer.bufferSize;
		final CellPreProcessor cellPreProcessor = this.cellPreProcessor;
		if (bufferSize > chars.length) throw new ArrayIndexOutOfBoundsException();

		try {
			
			mainloop:
			while (currentIndex < bufferSize) {
				// unescaped loop
				if ((currentState & (QUOTED_AREA | COMMENTED)) == 0) {
					nonquotesloop:
					while (currentIndex < bufferSize) {
						final char character = chars[currentIndex];
						final int cellEnd = currentIndex;

						currentIndex++;

						if (character == separatorChar) { // separator
							cellPreProcessor.newCell(chars, csvBuffer.cellStartMark, cellEnd, cellConsumer, currentState);
							csvBuffer.cellStartMark = currentIndex;
							currentState = LAST_CHAR_WAS_SEPARATOR | ROW_DATA;
							continue;
						} else if (character == LF) { // \n
							if ((currentState & LAST_CHAR_WAS_CR) == 0) {
								cellPreProcessor.newCell(chars, csvBuffer.cellStartMark, cellEnd, cellConsumer, currentState);
								if (cellConsumer.endOfRow()) {
									markEndOfRow(currentIndex);
									currentState = NONE;
									return true;
								}
							}
							markEndOfRow(currentIndex);
							currentState = NONE;
							continue;
						} else if (character == CR) { // \r
							cellPreProcessor.newCell(chars, csvBuffer.cellStartMark, cellEnd, cellConsumer, currentState);
							currentState = LAST_CHAR_WAS_CR;
							if (cellConsumer.endOfRow()) {
								markEndOfRow(currentIndex);
								return true;
							}
							markEndOfRow(currentIndex);
							continue;
						}
							
						if ((currentState & (QUOTED | CELL_DATA)) == (CELL_DATA)) {
							// unquoted cell looks for separator
							while (currentIndex < bufferSize) {
								final char c = chars[currentIndex];
								final int ce = currentIndex;
								currentIndex++;
								if (((c & separatorFingerPrintMask) == separatorFingerPrint)
										&& (c == separatorChar || c == LF || c == CR)) { // separator
									cellPreProcessor.newCell(chars, csvBuffer.cellStartMark, ce, cellConsumer, currentState);
									if (c == separatorChar) {
										currentState = LAST_CHAR_WAS_SEPARATOR | ROW_DATA;
									} else {
										currentState = c == LF ? NONE : LAST_CHAR_WAS_CR;
										if (cellConsumer.endOfRow()) {
											markEndOfRow(currentIndex);
											return true;
										}
										csvBuffer.rowStartMark = currentIndex;
									}
									csvBuffer.cellStartMark = currentIndex;
									continue nonquotesloop;
								}
							}
							return false;
						}
						
						if (((currentState ^ CELL_DATA) & (QUOTED | CELL_DATA)) != 0 && character == quoteChar) { 
							// no cell data | quoted  quote is first character
							currentState =
									QUOTED_AREA
											| QUOTED
											| ((currentState & QUOTED) << 5); // if already quoted it's a double quot need to escape QUOTED << 5 is  CONTAINS_ESCAPED_CHAR
							break;
						} 
						
						if ((currentState & (CELL_DATA | ROW_DATA)) == 0 && yamlComment && character == COMMENT) { 
							// no cell data or row data comment is first character
							currentState |= COMMENTED;
							break;
						}

						currentState &= TURN_OFF_LAST_CHAR_MASK;

						if (notIgnoreLeadingSpace || character != SPACE) {
							currentState |= CELL_DATA;
						}
					}
				} else if ((currentState & QUOTED_AREA) != 0){
					// escaped area
					while (currentIndex < bufferSize) {
						if ((currentState & ESCAPED) == 0) {
							char c = chars[currentIndex++];
							if (c == quoteChar) {
								currentState &= TURN_OFF_QUOTED_AREA;
								continue mainloop;
							} else if (c == escapeChar) {
								currentState |= ESCAPED | CONTAINS_ESCAPED_CHAR;
							}
						} else {
							currentState &= TURN_OFF_ESCAPED;
						}
					}
					return false;
				} else {
					int nextEndOfLineChar = findNexEndOfLineChar(chars, currentIndex, bufferSize);
					if (nextEndOfLineChar != -1) {
						currentIndex = nextEndOfLineChar + 1;
						cellPreProcessor.newCell(chars, csvBuffer.cellStartMark, nextEndOfLineChar, cellConsumer, currentState);
						currentState = chars[nextEndOfLineChar] == CR ? LAST_CHAR_WAS_CR : NONE;
						if (cellConsumer.endOfRow()) {
							markEndOfRow(currentIndex);
							return true;
						}
						markEndOfRow(currentIndex);
					} else {
						currentIndex = bufferSize;
					}
				}
			}
			return false;
		} finally {
			_currentState = currentState;
			_currentIndex = currentIndex;
		}


	}

	private void markEndOfRow(int currentIndex) {
		CharBuffer csvBuffer = this.csvBuffer;
		csvBuffer.cellStartMark = currentIndex;
		csvBuffer.rowStartMark = currentIndex;
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
			cellPreProcessor.newCell(csvBuffer.buffer, csvBuffer.cellStartMark, _currentIndex, cellConsumer, _currentState);
			csvBuffer.cellStartMark = _currentIndex + 1;
			_currentState = NONE;
		}
		cellConsumer.end();
	}

	private boolean hasUnconsumedData() {
		return _currentIndex > csvBuffer.cellStartMark;
	}

	@Override
	public boolean shiftAndRead(boolean keepRow) throws IOException {
		if (csvBuffer.isConstant()) return false;
		
		int shiftFrom = keepRow ? csvBuffer.rowStartMark : Math.min(csvBuffer.cellStartMark, csvBuffer.bufferSize);
		
		boolean b = csvBuffer.shiftAndRead(shiftFrom);
		_currentIndex -= shiftFrom;
		return b;
	}
}
