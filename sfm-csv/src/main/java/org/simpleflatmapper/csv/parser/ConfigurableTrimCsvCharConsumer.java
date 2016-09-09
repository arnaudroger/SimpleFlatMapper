package org.simpleflatmapper.csv.parser;


/**
 * Consume the charBuffer.
 */
public final class ConfigurableTrimCsvCharConsumer extends CsvCharConsumer {


	private final char separatorChar;
	private final char escapeChar;


	public ConfigurableTrimCsvCharConsumer(CharBuffer csvBuffer, char separatorChar, char escapeChar) {
		super(csvBuffer);
		this.separatorChar = separatorChar;
		this.escapeChar = escapeChar;
	}

	@Override
	protected int strEnd(int start, int end, char[] charBuffer) {
		for(; start < end && charBuffer[end - 1] == ' '; end--)
			;
		return end;
	}
	@Override
	protected int strStart(int start, int end, char[] charBuffer) {
		for(;start < end && charBuffer[start] == ' '; start++)
			;
		return start;
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
