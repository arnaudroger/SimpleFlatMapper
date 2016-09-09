package org.simpleflatmapper.csv.parser;



/**
 * Consume the charBuffer.
 */
public final class ConfigurableCsvCharConsumer extends CsvCharConsumer {


	private final char separatorChar;
	private final char escapeChar;


	public ConfigurableCsvCharConsumer(CharBuffer csvBuffer, char separatorChar, char escapeChar) {
		super(csvBuffer);
		this.separatorChar = separatorChar;
		this.escapeChar = escapeChar;
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
		return escapeChar;
	}

	@Override
	protected char separatorChar() {
		return separatorChar;
	}
}

