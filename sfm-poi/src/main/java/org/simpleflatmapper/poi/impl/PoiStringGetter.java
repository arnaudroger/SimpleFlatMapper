package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.Getter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PoiStringGetter implements Getter<Row, String> {

    private final int index;

    private final DataFormatter dataFormatter = new DataFormatter();
    private final Lock lock = new ReentrantLock();

    public PoiStringGetter(int index) {
        this.index = index;
    }

    @Override
    public String get(Row target) throws Exception {
        final Cell cell = target.getCell(index);
        if (cell != null) {
            switch(cell.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                case Cell.CELL_TYPE_NUMERIC:
                    return formatCell(cell);
                default:
                return cell.getStringCellValue();
            }
        } else {
            return null;
        }
    }


    /*
     * hopefully lock will not be contented.
     * really need to cache dataFormater or will be very costly.
     */
    private String formatCell(Cell cell) {
        lock.lock();
        try {
            return dataFormatter.formatCellValue(cell);
        } finally {
            lock.unlock();
        }
    }
}
