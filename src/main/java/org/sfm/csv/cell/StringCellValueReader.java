package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;


public class StringCellValueReader implements CellValueReader<String> {

	final static byte QUOTE = '"';
	@Override
	public String read(byte[] bytes, int offset, int length) {
		
		if (bytes[offset] == QUOTE) {
			StringBuilder sb = new StringBuilder(length);
			boolean lastWasQuote = false;
			for(int i = offset + 1; i < offset + length -1; i++) {
				if (bytes[i] != QUOTE || lastWasQuote) {
					sb.append((char)bytes[i]);
					lastWasQuote = false;
				} else {
					lastWasQuote = true;
				}
			}
			if (bytes[offset + length -1] != QUOTE) {
				sb.append((char)bytes[offset + length -1]);
			}
			return sb.toString();
		}
		
		return new String(bytes, offset, length);
	}

}
