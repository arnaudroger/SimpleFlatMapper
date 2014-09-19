package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;
import org.sfm.reflect.EnumHelper;

public class EnumCellValueReader<E extends Enum<E>> implements CellValueReader<E> {

	
	final static byte ZERO = '0';
	final static byte NINE = '9';
	
	private final StringCellValueReader stringCellValueReader = new StringCellValueReader();
	private final Class<E> enumClass;
	private final E[] values;
	
	
	public EnumCellValueReader(Class<E> enumClass) {
		super();
		this.enumClass = enumClass;
		this.values = EnumHelper.getValues(enumClass);
	}

	@Override
	public E read(byte[] bytes, int offset, int length) {
		
		int n = parsePositiveNumber(bytes, offset, length);
		if (n >= 0 && n < values.length) {
			return values[n];
		} else {
			return Enum.valueOf(enumClass, stringCellValueReader.read(bytes, offset, length));
		}
	}

	private int parsePositiveNumber(byte[] bytes, int offset, int length) {
		int n = 0;
		for(int i = offset; i< offset + length ; i++) {
			byte b = bytes[i];
			if (b >= ZERO && b <= NINE) {
				n = n * 10 + b - ZERO;
			} else {
				return -1;
			}
		}
		return n;
	}

}
