package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.MappingContext;

import java.util.Spliterator;
import java.util.function.Consumer;

public class SheetSpliterator<T> implements Spliterator<T> {

    private final StaticSheetMapper<T> mapper;
    private final Sheet sheet;
    private final MappingContext<? super Row> mappingContext;

    private int rowNum;

    public SheetSpliterator(StaticSheetMapper<T> mapper, int startRow, Sheet sheet, MappingContext<? super Row> mappingContext) {
        this.mapper = mapper;
        this.rowNum = startRow;
        this.sheet = sheet;
        this.mappingContext = mappingContext;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        Sheet lSheet = this.sheet;
        StaticSheetMapper<T> lMapper = this.mapper;
        MappingContext<? super Row> lMappingContext = this.mappingContext;
        for(int i = rowNum; i <= lSheet.getLastRowNum(); i++) {
            action.accept(lMapper.map(lSheet.getRow(i), lMappingContext));
        }
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        Sheet lSheet = this.sheet;
        if (rowNum <= lSheet.getLastRowNum()) {
            action.accept(mapper.map(lSheet.getRow(rowNum), mappingContext));
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
