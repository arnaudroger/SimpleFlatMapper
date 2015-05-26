package org.sfm.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
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
            return cell.getStringCellValue();
        } else {
            return null;
        }
    }
}
