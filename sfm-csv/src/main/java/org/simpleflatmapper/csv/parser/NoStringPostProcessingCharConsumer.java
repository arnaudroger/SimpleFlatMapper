package org.simpleflatmapper.csv.parser;



/**
 * Consume the charBuffer.
 */
public class NoStringPostProcessingCharConsumer extends UnescapeCharConsumer {

	public NoStringPostProcessingCharConsumer(CharBuffer csvBuffer, char separatorChar, char escapeChar) {
		super(csvBuffer, separatorChar, escapeChar);
	}

	@Override
	protected void newCell(int end, final CellConsumer cellConsumer) {
		char[] chars = csvBuffer.getCharBuffer();
		int strStart = csvBuffer.getMark();
		cellConsumer.newCell(chars, strStart, end - strStart);
		csvBuffer.mark(end + 1);
	}
}
