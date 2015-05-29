package org.sfm.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.sfm.reflect.Getter;

import java.util.Date;

public class PoiDateGetter implements Getter<Row, Date> {

    private final int index;

    public PoiDateGetter(int index) {
        this.index = index;
    }

    @Override
    public Date get(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            return cell.getDateCellValue();
        } else {
            return null;
        }
    }

}
