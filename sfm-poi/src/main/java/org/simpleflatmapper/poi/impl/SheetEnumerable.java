package org.simpleflatmapper.poi.impl;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.util.Enumerable;

import java.util.Iterator;

public class SheetEnumerable<T> implements Enumerable<T> {

    private final SourceMapper<Row, T> mapper;
    private final Sheet sheet;
    private final MappingContext<? super Row> mappingContext;

    private int rowNum;

    public SheetEnumerable(SourceMapper<Row, T> mapper, int startRow, Sheet sheet, MappingContext<? super Row> mappingContext) {
        this.mapper = mapper;
        this.rowNum = startRow;
        this.sheet = sheet;
        this.mappingContext = mappingContext;
    }

    @Override
    public boolean next() {
        int n = rowNum + 1;
        if (rowNum <= sheet.getLastRowNum()) {
            rowNum = n;
            return true;
        }
        return false;
    }

    @Override
    public T currentValue() {
        Row row = sheet.getRow(rowNum);
        return mapper.map(row, mappingContext);
    }

}
