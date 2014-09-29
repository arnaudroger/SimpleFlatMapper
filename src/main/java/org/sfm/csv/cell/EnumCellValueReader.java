package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.DecoderContext;
import org.sfm.reflect.EnumHelper;

public class EnumCellValueReader<E extends Enum<E>> implements CellValueReader<E> {

	final static byte BZERO = '0';
	final static byte BNINE = '9';
	final static char CZERO = '0';
	final static char CNINE = '9';
	
	private final StringCellValueReader stringCellValueReader = new StringCellValueReader();
	private final Class<E> enumClass;
	private final E[] values;
	
	
	public EnumCellValueReader(Class<E> enumClass) {
		super();
		this.enumClass = enumClass;
		this.values = EnumHelper.getValues(enumClass);
	}

	@Override
	public E read(byte[] bytes, int offset, int length, DecoderContext decoderContext) {
		
		int n = parsePositiveNumber(bytes, offset, length);
		if (n >= 0 && n < values.length) {
			return values[n];
		} else {
			return Enum.valueOf(enumClass, stringCellValueReader.read(bytes, offset, length, decoderContext));
		}
	}

	@Override
	public E read(char[] chars, int offset, int length) {
		
		int n = parsePositiveNumber(chars, offset, length);
		if (n >= 0 && n < values.length) {
			return values[n];
		} else {
			return Enum.valueOf(enumClass, stringCellValueReader.read(chars, offset, length));
		}
	}
	
	private int parsePositiveNumber(byte[] bytes, int offset, int length) {
		int n = 0;
		for(int i = offset; i< offset + length ; i++) {
			byte b = bytes[i];
			if (b >= BZERO && b <= BNINE) {
				n = n * 10 + b - BZERO;
			} else {
				return -1;
			}
		}
		return n;
	}
	
	private int parsePositiveNumber(char[] chars, int offset, int length) {
		int n = 0;
		for(int i = offset; i< offset + length ; i++) {
			char b = chars[i];
			if (b >= CZERO && b <= CNINE) {
				n = n * 10 + b - CZERO;
			} else {
				return -1;
			}
		}
		return n;
	}

}
