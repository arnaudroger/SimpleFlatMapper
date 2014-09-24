package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;


public class IntegerCellValueReader implements CellValueReader<Integer> {

	public final static byte BZERO = '0';
	public final static byte BNINE = '9';
	public final static byte BNEGSIGN = '-';

	public final static char CZERO = '0';
	public final static char CNINE = '9';
	public final static char CNEGSIGN = '-';

	@Override
	public Integer read(byte[] bytes, int offset, int length) {
		return new Integer(parseInt(bytes, offset, length));
	}

	@Override
	public Integer read(char[] chars, int offset, int length) {
		return new Integer(parseInt(chars, offset, length));
	}
	
	public static int parseInt(byte[] bytes, int offset, int length) {
		int n = 0;
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
	
	public static int parseInt(char[] chars, int offset, int length) {
		int n = 0;
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
