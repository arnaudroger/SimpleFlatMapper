package org.sfm.csv;

public class LongCellValueTransformer implements CellValueTransfomer<Long> {

	final static byte ZERO = '0';
	@Override
	public Long transform(byte[] bytes, int offset, int length) {
		long n = 0;
		for(int i = offset; i < length; i++) {
			n  += bytes[i] - ZERO;
		}
		return new Long(n);
	}

}
