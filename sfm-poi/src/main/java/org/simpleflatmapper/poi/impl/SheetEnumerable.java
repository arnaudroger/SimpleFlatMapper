package org.simpleflatmapper.poi.impl;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.util.Enumerable;

public class SheetEnumerable<T> implements Enumerable<T> {

    private final SourceMapper<Row, T> mapper;
    private final Sheet sheet;
    private final MappingContext<? super Row> mappingContext;

    private int rowNum;
    private int lastRowNum;
    private T currentValue;

    public SheetEnumerable(SourceMapper<Row, T> mapper, int startRow, Sheet sheet, MappingContext<? super Row> mappingContext) {
        this.mapper = mapper;
        this.rowNum = startRow;
        this.lastRowNum = sheet.getLastRowNum();
        this.sheet = sheet;
        this.mappingContext = mappingContext;
    }

    @Override
    public boolean next() {
        currentValue = null;
        while(rowNum <= lastRowNum) {
            Row row = sheet.getRow(rowNum);
            rowNum ++;
            if (row != null) {
                currentValue = mapper.map(row, mappingContext);
                return true;
            }
        }
        return false;
    }

    @Override
    public T currentValue() {
        return currentValue;
    }

}
