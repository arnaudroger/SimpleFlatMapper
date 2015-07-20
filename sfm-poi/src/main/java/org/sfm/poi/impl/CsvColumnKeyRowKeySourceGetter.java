package org.sfm.poi.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.context.KeySourceGetter;

import java.sql.SQLException;

public class CsvColumnKeyRowKeySourceGetter implements KeySourceGetter<CsvColumnKey, Row> {
    @Override
    public Object getValue(CsvColumnKey key, Row source) throws SQLException {
        final Cell cell = source.getCell(key.getIndex());
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    return null;
                case Cell.CELL_TYPE_BOOLEAN:
                    return cell.getBooleanCellValue();
                case Cell.CELL_TYPE_NUMERIC:
                    return cell.getNumericCellValue();
                default:
                    return cell.getStringCellValue();
            }
        }
        return null;
    }
}
