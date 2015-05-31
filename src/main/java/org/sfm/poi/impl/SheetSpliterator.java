package org.sfm.poi.impl;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.sfm.map.MappingContext;

import java.util.Spliterator;
import java.util.function.Consumer;

public class SheetSpliterator<T> implements Spliterator<T> {

    private final StaticSheetMapper<T> mapper;
    private final Sheet sheet;
    private final MappingContext<Row> mappingContext;

    private int rowNum;

    public SheetSpliterator(StaticSheetMapper<T> mapper, int startRow, Sheet sheet, MappingContext<Row> mappingContext) {
        this.mapper = mapper;
        this.rowNum = startRow;
        this.sheet = sheet;
        this.mappingContext = mappingContext;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        for(int i = rowNum; i <= sheet.getLastRowNum(); i++) {
            action.accept(mapper.map(sheet.getRow(i), mappingContext));
        }
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (rowNum <= sheet.getLastRowNum()) {
            action.accept(mapper.map(sheet.getRow(rowNum), mappingContext));
            rowNum++;
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return sheet.getLastRowNum();
    }

    @Override
    public int characteristics() {
        return Spliterator.ORDERED | Spliterator.NONNULL;
    }
}
