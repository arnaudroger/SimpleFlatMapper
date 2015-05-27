package org.sfm.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.sfm.reflect.Getter;

public class PoiStringGetter implements Getter<Row, String> {

    private final int index;

    public PoiStringGetter(int index) {
        this.index = index;
    }

    @Override
    public String get(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            switch(cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    return formatedNumber(cell);
                default:
                return cell.getStringCellValue();
            }
        } else {
            return null;
        }
    }

    private String formatedNumber(Cell cell) {
        DataFormatter df = new DataFormatter();
        return df.formatCellValue(cell);
    }
}
