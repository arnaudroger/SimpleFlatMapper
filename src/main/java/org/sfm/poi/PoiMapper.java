package org.sfm.poi;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.sfm.map.*;
import org.sfm.poi.impl.SheetIterator;
import org.sfm.utils.RowHandler;

import java.util.Iterator;

//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.sfm.poi.impl.SheetSpliterator;
//IFJAVA8_END

public class PoiMapper<T> implements Mapper<Row, T> {

    private final Mapper<Row, T> mapper;
    private final int startRow = 0;

    private final RowHandlerErrorHandler rowHandlerErrorHandler;
    private final MappingContextFactory<Row> mappingContextFactory;

    public PoiMapper(Mapper<Row, T> mapper, RowHandlerErrorHandler rowHandlerErrorHandler, MappingContextFactory<Row> mappingContextFactory) {
        this.mapper = mapper;
        this.rowHandlerErrorHandler = rowHandlerErrorHandler;
        this.mappingContextFactory = mappingContextFactory;
    }

    public Iterator<T> iterator(Sheet sheet) {
        return iterator(startRow, sheet);
    }

    public Iterator<T> iterator(int startRow, Sheet sheet) {
        return new SheetIterator<T>(this, startRow, sheet, newMappingContext());
    }

    public <RH extends RowHandler<T>> RH forEach(Sheet sheet, RH rowHandler) {
        return forEach(startRow, sheet, rowHandler);
    }

    public <RH extends RowHandler<T>> RH forEach(int startRow, Sheet sheet, RH rowHandler) {
        MappingContext<Row> mappingContext = newMappingContext();
        for(int rowNum = startRow; rowNum <= sheet.getLastRowNum(); rowNum++) {
            T object = map(sheet.getRow(rowNum), mappingContext);
            try {
                rowHandler.handle(object);
            } catch(Exception e) {
                rowHandlerErrorHandler.handlerError(e, object);
            }
        }
        return rowHandler;
    }

    //IFJAVA8_START
    public Stream<T> stream(Sheet sheet) {
        return stream(startRow, sheet);
    }

    public Stream<T> stream(int startRow, Sheet sheet) {
        return StreamSupport.stream(new SheetSpliterator<T>(this, startRow, sheet, newMappingContext()), false);
    }
    //IFJAVA8_END


    @Override
    public T map(Row source) throws MappingException {
        return mapper.map(source);
    }

    @Override
    public T map(Row source, MappingContext<Row> context) throws MappingException {
        return mapper.map(source, context);
    }

    @Override
    public void mapTo(Row source, T target, MappingContext<Row> context) throws Exception {
        mapper.mapTo(source, target, context);
    }

    private MappingContext<Row> newMappingContext() {
        return mappingContextFactory.newContext();
    }
}
