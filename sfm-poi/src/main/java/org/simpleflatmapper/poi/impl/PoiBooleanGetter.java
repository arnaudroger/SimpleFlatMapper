package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;

public class PoiBooleanGetter implements Getter<Row, Boolean>, BooleanGetter<Row> {

    private final int index;

    public PoiBooleanGetter(int index) {
        this.index = index;
    }

    @Override
    public Boolean get(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return cell.getBooleanCellValue();
        } else {
            return null;
        }
    }

    @Override
    public boolean getBoolean(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return cell.getBooleanCellValue();
        } else {
            return false;
        }
    }
}
