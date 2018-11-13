package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.poi.RowMapper;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.EnumerableIterator;

import java.util.Iterator;

//IFJAVA8_START
import org.simpleflatmapper.util.EnumerableSpliterator;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END

public class StaticSheetMapper<T> implements RowMapper<T>, SourceFieldMapper<Row, T> {

    private final ContextualSourceFieldMapper<Row, T> mapper;
    private final int startRow = 0;

    private final ConsumerErrorHandler consumerErrorHandler;
    private final MappingContextFactory<? super Row> mappingContextFactory;

    public StaticSheetMapper(ContextualSourceFieldMapper<Row, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super Row> mappingContextFactory) {
        this.mapper = mapper;
        this.consumerErrorHandler = consumerErrorHandler;
        this.mappingContextFactory = mappingContextFactory;
    }

    @Override
    public Iterator<T> iterator(Sheet sheet) {
        return iterator(startRow, sheet);
    }

    @Override
    public Iterator<T> iterator(int startRow, Sheet sheet) {
        return new EnumerableIterator<T>(new SheetEnumerable<T>(this, startRow, sheet, newMappingContext()));
    }

    @Override
    public Enumerable<T> enumerate(Sheet sheet) {
        return enumerate(startRow, sheet);
    }

    @Override
    public Enumerable<T> enumerate(int startRow, Sheet sheet) {
        return new SheetEnumerable<T>(this, startRow, sheet, newMappingContext());


    }

    @Override
    public <RH extends CheckedConsumer<? super T>> RH forEach(Sheet sheet, RH consumer) {
        return forEach(startRow, sheet, consumer);
    }

    @Override
    public <RH extends CheckedConsumer<? super T>> RH forEach(int startRow, Sheet sheet, RH consumer) {
        MappingContext<? super Row> mappingContext = newMappingContext();
        SourceMapper<Row, T> lMapper = this.mapper;
        for(int rowNum = startRow; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row != null) {
                T object = lMapper.map(row, mappingContext);
                try {
                    consumer.accept(object);
                } catch (Exception e) {
                    consumerErrorHandler.handlerError(e, object);
                }
            }
        }
        return consumer;
    }

    //IFJAVA8_START
    @Override
    public Stream<T> stream(Sheet sheet) {
        return stream(startRow, sheet);
    }

    @Override
    public Stream<T> stream(int startRow, Sheet sheet) {
        return StreamSupport.stream(new EnumerableSpliterator<T>(new SheetEnumerable<T>(this, startRow, sheet, newMappingContext())), false);
    }
    //IFJAVA8_END


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
