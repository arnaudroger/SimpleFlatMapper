package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.util.EnumHelper;

public class EnumCellValueReader<E extends Enum<E>> implements CellValueReader<E> {

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
	public E read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		
		int n = parsePositiveNumber(chars, offset, length);
		if (n >= 0 && n < values.length) {
			return values[n];
		} else {
			return Enum.valueOf(enumClass, stringCellValueReader.read(chars, offset, length, parsingContext));
		}
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

    @Override
    public String toString() {
        return "EnumCellValueReader{" +
                "enumClass=" + enumClass +
                '}';
    }
}
