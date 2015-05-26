package org.sfm.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.BooleanGetter;
import org.sfm.reflect.primitive.DoubleGetter;

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
