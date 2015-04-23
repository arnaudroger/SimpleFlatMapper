package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.reflect.EnumHelper;

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
	public E read(CharSequence value, ParsingContext parsingContext) {
		
		int n = parsePositiveNumber(value);
		if (n >= 0 && n < values.length) {
			return values[n];
		} else {
			return Enum.valueOf(enumClass, stringCellValueReader.read(value, parsingContext));
		}
	}
	
	private int parsePositiveNumber(CharSequence value) {
		int n = 0;
		for(int i = 0; i< value.length() ; i++) {
			char b = value.charAt(i);
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
