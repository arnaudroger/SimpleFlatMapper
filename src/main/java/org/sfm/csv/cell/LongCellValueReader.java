package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;


public class LongCellValueReader implements CellValueReader<Long> {

	final static byte BZERO = '0';
	final static byte BNINE = '9';
	final static byte BNEGSIGN = '-';
	
	final static char CZERO = '0';
	final static char CNINE = '9';
	final static char CNEGSIGN = '-';
	
	@Override
	public Long read(byte[] bytes, int offset, int length) {
		long n = parseLong(bytes, offset, length);
		return new Long(n);
	}

	@Override
	public Long read(char[] chars, int offset, int length) {
		long n = parseLong(chars, offset, length);
		return new Long(n);
	}
	
	public static long parseLong(byte[] bytes, int offset, int length) {
		long n = 0;
		boolean negative = false;
		for(int i = offset; i < offset + length; i++) {
			byte b = bytes[i];
			if (b >= BZERO && b <= BNINE) {
				n  = n * 10 +  bytes[i] - BZERO;
			} else {
				if (b == BNEGSIGN && i == offset) {
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

	public static long parseLong(char[] chars, int offset, int length) {
		long n = 0;
		boolean negative = false;
		for(int i = offset; i < offset + length; i++) {
			char b = chars[i];
			if (b >= CZERO && b <= CNINE) {
				n  = n * 10 +  chars[i] - CZERO;
			} else {
				if (b == CNEGSIGN && i == offset) {
					negative = true;
				} else {
					throw new ParsingException("Cannot parse " + new String(chars, offset, length) + " as an int");
				}
			}
		}
		if (negative) {
			n = 0 - n;
		}
		return n;
	}
}
