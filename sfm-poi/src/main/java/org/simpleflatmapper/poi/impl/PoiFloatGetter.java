package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;

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
