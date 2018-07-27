package org.simpleflatmapper.poi.impl;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.util.Enumerable;

public class RowEnumerable implements Enumerable<Row> {


    private int currentRowIndex;
    private final Sheet sheet;

    public RowEnumerable(int startRow, Sheet sheet) {
        this.currentRowIndex = startRow - 1;
        this.sheet = sheet;
    }

    @Override
    public boolean next() {
        if (currentRowIndex < sheet.getLastRowNum()) {
            currentRowIndex ++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Row currentValue() {
        return sheet.getRow(currentRowIndex);
    }
}
