package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;


public class StringCellValueReader implements CellValueReader<String> {

	final static byte QUOTE = '"';
	final static char CQUOTE = '"';
	
	@Override
	public String read(char[] chars, int offset, int length) {
		return readString(chars, offset, length);
	}
	
	public static String readString(char[] chars, int offset, int length) {
		if (chars[offset] == CQUOTE) {
			return unescape(chars, offset, length);
		}
		
		return new String(chars, offset, length);	
	}

	private static String unescape(char[] chars, int offset, int length) {
		char[] newChars = new char[length];
		boolean lastWasQuote = false;
		int j = 0;
		for(int i = offset + 1; i < offset + length -1; i++) {
			if (chars[i] != CQUOTE || lastWasQuote) {
				newChars[j++] = chars[i];
				lastWasQuote = false;
			} else {
				lastWasQuote = true;
			}
		}
		if (chars[offset + length -1] != CQUOTE) {
			newChars[j++] = chars[offset + length -1];
		}
		return new String(newChars, 0, j);
	}

}
