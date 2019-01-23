package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.mapper.UnorderedJoinMapperEnumerable;
import org.simpleflatmapper.poi.RowMapper;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.EnumerableIterator;

import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.simpleflatmapper.util.EnumerableSpliterator;
//IFJAVA8_END

public class UnorderedJoinSheetMapper<T> implements RowMapper<T> {

    private final ContextualSourceFieldMapper<Row, T> mapper;
    private final int startRow = 0;

    private final ConsumerErrorHandler consumerErrorHandler;
    private final MappingContextFactory<? super Row> mappingContextFactory;

    public UnorderedJoinSheetMapper(ContextualSourceFieldMapper<Row, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super Row> mappingContextFactory) {
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
        return new EnumerableIterator<T>(enumerable(startRow, sheet, newMappingContext()));
    }

    @Override
    public Enumerable<T> enumerate(Sheet sheet) {
        return enumerate(startRow, sheet);
    }

    @Override
    public Enumerable<T> enumerate(int startRow, Sheet sheet) {
        return enumerable(startRow, sheet, newMappingContext());
    }

    private Enumerable<T> enumerable(int startRow, Sheet sheet, MappingContext<? super Row> mappingContext) {
        return new UnorderedJoinMapperEnumerable<Row, T>(mapper, mappingContext, new RowEnumerable(startRow, sheet));
    }

    @Override
    public <RH extends CheckedConsumer<? super T>> RH forEach(Sheet sheet, RH consumer) {
        return forEach(startRow, sheet, consumer);
    }

    @Override
    public <RH extends CheckedConsumer<? super T>> RH forEach(int startRow, Sheet sheet, RH consumer) {
        MappingContext<? super Row> mappingContext = newMappingContext();

        Enumerable<T> enumarable = enumerable(startRow, sheet, mappingContext);

        while(enumarable.next()) {
            try {
                consumer.accept(enumarable.currentValue());
            } catch(Exception e) {
                consumerErrorHandler.handlerError(e, enumarable.currentValue());
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
        return StreamSupport.stream(new EnumerableSpliterator<T>(enumerable(startRow, sheet, newMappingContext())), false);
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


    private MappingContext<? super Row> newMappingContext() {
        return mappingContextFactory.newContext();
    }
}
