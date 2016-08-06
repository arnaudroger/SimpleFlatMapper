package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;

public class PoiShortGetter implements Getter<Row, Short>, ShortGetter<Row> {

    private final int index;

    public PoiShortGetter(int index) {
        this.index = index;
    }

    @Override
    public Short get(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return (short)cell.getNumericCellValue();
        } else {
            return null;
        }
    }

    @Override
    public short getShort(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return (short)cell.getNumericCellValue();
        } else {
            return 0;
        }
    }
}
