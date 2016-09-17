package org.simpleflatmapper.csv.parser;



/**
 * Consume the charBuffer.
 */
public class TrimAndUnescapeCharConsumer extends UnescapeCharConsumer {

	public TrimAndUnescapeCharConsumer(CharBuffer csvBuffer, char separatorChar, char escapeChar) {
		super(csvBuffer, separatorChar, escapeChar);
	}

	@Override
	protected void newCell(int end, final CellConsumer cellConsumer) {
		char[] chars = csvBuffer.getCharBuffer();
		int strStart = csvBuffer.getMark();
		int strEnd = end;

		strEnd = strEnd(strStart, strEnd, chars);
		strStart = strStart(strStart, strEnd, chars);

		if (strStart < strEnd && chars[strStart] == escapeChar) {
			strStart ++;
			strEnd = unescape(chars, strStart, strEnd);
		}
		cellConsumer.newCell(chars, strStart, strEnd - strStart);

		csvBuffer.mark(end + 1);
	}

	private int strEnd(int start, int end, char[] chars) {
		for(; start < end && chars[end - 1] == ' '; end--)
			;
		return end;
	}

	private int strStart(int start, int end, char[] chars) {
		for(;start < end && chars[start] == ' '; start++)
			;
		return start;
	}

}
