package org.sfm.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

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
