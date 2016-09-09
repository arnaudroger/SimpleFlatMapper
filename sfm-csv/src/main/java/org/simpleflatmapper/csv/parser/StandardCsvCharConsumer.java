package org.simpleflatmapper.csv.parser;



/**
 * Consume the charBuffer.
 */
public final class StandardCsvCharConsumer extends CsvCharConsumer {


	public static final char ESCAPE_CHAR = '"';
	public static final char SEPARATOR_CHAR = ',';

	public StandardCsvCharConsumer(CharBuffer csvBuffer) {
		super(csvBuffer);
	}


	@Override
	protected int strEnd(int strStart, int end, char[] charBuffer) {
		return end;
	}

	@Override
	protected int strStart(int strStart, int strEnd, char[] charBuffer) {
		return strStart;
	}

	@Override
	protected char escapeChar() {
		return ESCAPE_CHAR;
	}

	@Override
	protected char separatorChar() {
		return SEPARATOR_CHAR;
	}
}
