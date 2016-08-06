package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.IntGetter;

public class PoiIntegerGetter implements Getter<Row, Integer>, IntGetter<Row> {

    private final int index;

    public PoiIntegerGetter(int index) {
        this.index = index;
    }

    @Override
    public Integer get(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return (int)cell.getNumericCellValue();
        } else {
            return null;
        }
    }

    @Override
    public int getInt(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return (int)cell.getNumericCellValue();
        } else {
            return 0;
        }
    }
}
