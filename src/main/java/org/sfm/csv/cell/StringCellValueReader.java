package org.sfm.csv.cell;

import java.nio.charset.Charset;

import org.sfm.csv.CellValueReader;
import org.sfm.text.StringDecoder;
import org.sfm.text.StringDecoderFactory;


public class StringCellValueReader implements CellValueReader<String> {

	final static byte QUOTE = '"';
	final static char CQUOTE = '"';
	
	private final StringDecoder stringDecoder = StringDecoderFactory.newStringDecoder(Charset.forName("UTF-8")); 
	
	@Override
	public String read(byte[] bytes, int offset, int length) {
		return readString(bytes, offset, length);
	}

	@Override
	public String read(char[] chars, int offset, int length) {
		return readString(chars, offset, length);
	}
	
	public String readString(byte[] bytes, int offset, int length) {
		if (bytes[offset] == QUOTE) {
			return unescape(bytes, offset, length);
		}
		return decode(bytes, offset, length);
	}

	private String unescape(byte[] bytes, int offset, int length) {
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
		return decode(newBytes, 0, j);
	}

	private String decode(byte[] bytes, int offset, int length) {
		return stringDecoder.decode(bytes, offset, length);
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
