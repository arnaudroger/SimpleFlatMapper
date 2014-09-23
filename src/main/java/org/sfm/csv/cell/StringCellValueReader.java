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

}
