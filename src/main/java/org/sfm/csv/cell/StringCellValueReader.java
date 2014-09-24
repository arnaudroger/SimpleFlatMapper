package org.sfm.csv.cell;

import java.nio.charset.Charset;

import org.sfm.csv.CellValueReader;


public class StringCellValueReader implements CellValueReader<String> {

	final static byte QUOTE = '"';
	final static Charset charset = Charset.forName("UTF-8");
	
	@Override
	public String read(byte[] bytes, int offset, int length) {
		return readString(bytes, offset, length);
	}

	@Override
	public String read(char[] chars, int offset, int length) {
		return readString(chars, offset, length);
	}
	
	public static String readString(byte[] bytes, int offset, int length) {
		if (bytes[offset] == QUOTE) {
			byte[] newBytes = new byte[length];
			boolean lastWasQuote = false;
			int j = 0;
			for(int i = offset + 1; i < offset + length -1; i++) {
				if (bytes[i] != QUOTE || lastWasQuote) {
					newBytes[j++] = bytes[i];
					lastWasQuote = false;
				} else {
					lastWasQuote = true;
				}
			}
			if (bytes[offset + length -1] != QUOTE) {
				newBytes[j++] = bytes[offset + length -1];
			}
			return new String(newBytes, 0, j, charset);
		}
		
		return new String(bytes, offset, length, charset);
	}

	public static String readString(char[] chars, int offset, int length) {
		if (chars[offset] == QUOTE) {
			char[] newChars = new char[length];
			boolean lastWasQuote = false;
			int j = 0;
			for(int i = offset + 1; i < offset + length -1; i++) {
				if (chars[i] != QUOTE || lastWasQuote) {
					newChars[j++] = chars[i];
					lastWasQuote = false;
				} else {
					lastWasQuote = true;
				}
			}
			if (chars[offset + length -1] != QUOTE) {
				newChars[j++] = chars[offset + length -1];
			}
			return new String(newChars, 0, j);
		}
		
		return new String(chars, offset, length);	}

}
