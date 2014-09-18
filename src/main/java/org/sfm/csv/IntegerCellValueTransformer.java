package org.sfm.csv;

public class IntegerCellValueTransformer implements CellValueTransfomer<Integer> {

	final static byte ZERO = '0';
	@Override
	public Integer transform(byte[] bytes, int offset, int length) {
		int n = 0;
		for(int i = offset; i < length; i++) {
			n  += bytes[i] - ZERO;
		}
		return new Integer(n);
	}

}
