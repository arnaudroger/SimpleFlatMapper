package org.simpleflatmapper.poi.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.Getter;

public class PoiEnumGetter<E extends Enum<E>> implements Getter<Row, E> {

    private final int index;
    private final Class<E> enumClass;
    private final E[] values;
    public PoiEnumGetter(int index, Class<E> enumClass) {
        this.index = index;
        this.enumClass = enumClass;
        this.values = enumClass.getEnumConstants();
    }

    @Override
    public E get(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BLANK : return null;
                case Cell.CELL_TYPE_STRING : return Enum.valueOf(enumClass, cell.getStringCellValue());
                case Cell.CELL_TYPE_NUMERIC : return values[(int)cell.getNumericCellValue()];
                default:
                    throw new UnsupportedOperationException("Cannot convert cell to enum");
            }
        } else {
            return null;
        }
    }

}