package org.sfm.poi.impl;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.sfm.map.*;
import org.sfm.map.context.MappingContextFactory;
import org.sfm.map.mapper.JoinMapperEnumarable;
import org.sfm.poi.RowMapper;
import org.sfm.utils.Enumarable;
import org.sfm.utils.EnumarableIterator;
import org.sfm.utils.RowHandler;

import java.util.Iterator;

/*IFJAVA8_START
import org.sfm.utils.EnumarableSpliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
IFJAVA8_END*/

public class JoinSheetMapper<T> implements RowMapper<T> {

    private final Mapper<Row, T> mapper;
    private final int startRow = 0;

    private final RowHandlerErrorHandler rowHandlerErrorHandler;
    private final MappingContextFactory<? super Row> mappingContextFactory;

    public JoinSheetMapper(Mapper<Row, T> mapper, RowHandlerErrorHandler rowHandlerErrorHandler, MappingContextFactory<? super Row> mappingContextFactory) {
        this.mapper = mapper;
        this.rowHandlerErrorHandler = rowHandlerErrorHandler;
        this.mappingContextFactory = mappingContextFactory;
    }

    @Override
    public Iterator<T> iterator(Sheet sheet) {
        return iterator(startRow, sheet);
    }

    @Override
    public Iterator<T> iterator(int startRow, Sheet sheet) {
        return new EnumarableIterator<T>(enumerable(startRow, sheet, newMappingContext()));
    }

    private Enumarable<T> enumerable(int startRow, Sheet sheet, MappingContext<? super Row> mappingContext) {
        return new JoinMapperEnumarable<Row, T>(mapper, mappingContext, new RowEnumarable(startRow, sheet));
    }

    @Override
    public <RH extends RowHandler<T>> RH forEach(Sheet sheet, RH rowHandler) {
        return forEach(startRow, sheet, rowHandler);
    }

    @Override
    public <RH extends RowHandler<T>> RH forEach(int startRow, Sheet sheet, RH rowHandler) {
        MappingContext<? super Row> mappingContext = newMappingContext();

        Enumarable<T> enumarable = enumerable(startRow, sheet, mappingContext);

        while(enumarable.next()) {
            try {
                rowHandler.handle(enumarable.currentValue());
            } catch(Exception e) {
                rowHandlerErrorHandler.handlerError(e, enumarable.currentValue());
            }
        }

        return rowHandler;
    }

    /*IFJAVA8_START
    @Override
    public Stream<T> stream(Sheet sheet) {
        return stream(startRow, sheet);
    }

    @Override
    public Stream<T> stream(int startRow, Sheet sheet) {
        return StreamSupport.stream(new EnumarableSpliterator<T>(enumerable(startRow, sheet, newMappingContext())), false);
    }
    IFJAVA8_END*/


    @Override
    public T map(Row source) throws MappingException {
        return mapper.map(source);
    }

    @Override
    public T map(Row source, MappingContext<? super Row> context) throws MappingException {
        return mapper.map(source, context);
    }

    @Override
    public void mapTo(Row source, T target, MappingContext<? super Row> context) throws Exception {
        mapper.mapTo(source, target, context);
    }

    private MappingContext<? super Row> newMappingContext() {
        return mappingContextFactory.newContext();
    }
}
