package org.sfm.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;
import org.sfm.reflect.primitive.FloatGetter;

public class PoiFloatGetter implements Getter<Row, Float>, FloatGetter<Row> {

    private final int index;

    public PoiFloatGetter(int index) {
        this.index = index;
    }

    @Override
    public Float get(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return (float)cell.getNumericCellValue();
        } else {
            return null;
        }
    }

    @Override
    public float getFloat(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return  (float)cell.getNumericCellValue();
        } else {
            return 0;
        }
    }
}
