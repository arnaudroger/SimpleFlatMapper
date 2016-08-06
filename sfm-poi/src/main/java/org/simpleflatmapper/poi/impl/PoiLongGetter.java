package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.LongGetter;

public class PoiLongGetter implements Getter<Row, Long>, LongGetter<Row> {

    private final int index;

    public PoiLongGetter(int index) {
        this.index = index;
    }

    @Override
    public Long get(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return (long)cell.getNumericCellValue();
        } else {
            return null;
        }
    }

    @Override
    public long getLong(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return (long)cell.getNumericCellValue();
        } else {
            return 0;
        }
    }
}
