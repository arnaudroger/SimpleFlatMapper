package org.sfm.poi.impl;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;

import java.util.Iterator;

public class SheetIterator<T> implements Iterator<T> {

    private final Mapper<Row, T> mapper;
    private final Sheet sheet;
    private final MappingContext<Row> mappingContext;

    private int rowNum;

    public SheetIterator(Mapper<Row, T> mapper, int startRow, Sheet sheet, MappingContext<Row> mappingContext) {
        this.mapper = mapper;
        this.rowNum = startRow;
        this.sheet = sheet;
        this.mappingContext = mappingContext;
    }

    @Override
    public boolean hasNext() {
        return rowNum <= sheet.getLastRowNum();
    }

    @Override
    public T next() {
        Row row = sheet.getRow(rowNum);
        rowNum++;
        return mapper.map(row, mappingContext);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
