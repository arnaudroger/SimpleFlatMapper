package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.ParsingException;


public class LongCellValueReader implements CellValueReader<Long> {
	
	final static char CZERO = '0';
	final static char CNINE = '9';
	final static char CNEGSIGN = '-';
	
	@Override
	public Long read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		long n = parseLong(chars, offset, length);
		return new Long(n);
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
