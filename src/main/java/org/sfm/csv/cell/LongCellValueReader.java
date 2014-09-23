package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;


public class LongCellValueReader implements CellValueReader<Long> {

	final static byte ZERO = '0';
	final static byte NINE = '9';
	final static byte NEGSIGN = '-';
	
	@Override
	public Long read(byte[] bytes, int offset, int length) {
		long n = parseLong(bytes, offset, length);
		return new Long(n);
	}

	public static long parseLong(byte[] bytes, int offset, int length) {
		long n = 0;
		boolean negative = false;
		for(int i = offset; i < offset + length; i++) {
			byte b = bytes[i];
			if (b >= ZERO && b <= NINE) {
				n  = n * 10 +  bytes[i] - ZERO;
			} else {
				if (b == NEGSIGN && i == offset) {
					negative = true;
				} else {
					throw new ParsingException("Cannot parse " + new String(bytes, offset, length) + " as an int");
				}
			}
		}
		if (negative) {
			n = 0 - n;
		}
		return n;
	}

}
