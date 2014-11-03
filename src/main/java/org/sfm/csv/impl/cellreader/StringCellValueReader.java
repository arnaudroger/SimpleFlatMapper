package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class StringCellValueReader implements CellValueReader<String> {

	final static char CQUOTE = '"';
	
	@Override
	public String read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return readString(chars, offset, length);
	}
	
	public static String readString(char[] chars, int offset, int length) {
		if (chars[offset] == CQUOTE) {
			return unescape(chars, offset, length);
		} else {
			return new String(chars, offset, length);
		}
	}

	
	public static String unescape(char[] chars, int offset, int length) {
		boolean notEscaped = true;
		int j = offset + 1;
		for(int i = offset + 1; i < offset + length -1; i++) {
			notEscaped = chars[i] != CQUOTE || !notEscaped;
			if (notEscaped) {
				chars[j++] = chars[i];
			}
		}
		if (chars[offset + length -1] != CQUOTE) {
			chars[j++] = chars[offset + length -1];
		}
		return new String(chars, offset + 1, j  - offset -1);
	}
}
