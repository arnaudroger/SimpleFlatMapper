package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;

public class PoiDoubleGetter implements Getter<Row, Double>, DoubleGetter<Row> {

    private final int index;

    public PoiDoubleGetter(int index) {
        this.index = index;
    }

    @Override
    public Double get(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
            return cell.getNumericCellValue();
        } else {
            return null;
        }
    }

    @Override
    public double getDouble(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return cell.getNumericCellValue();
        } else {
            return 0;
        }
    }
}
