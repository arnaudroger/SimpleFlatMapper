package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.mapper.JoinMapperEnumarable;
import org.simpleflatmapper.poi.RowMapper;
import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.EnumarableIterator;
import org.simpleflatmapper.util.CheckedConsumer;

import java.util.Iterator;

//IFJAVA8_START
import org.simpleflatmapper.util.EnumarableSpliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END

public class JoinSheetMapper<T> implements RowMapper<T> {

    private final Mapper<Row, T> mapper;
    private final int startRow = 0;

    private final ConsumerErrorHandler consumerErrorHandler;
    private final MappingContextFactory<? super Row> mappingContextFactory;

    public JoinSheetMapper(Mapper<Row, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super Row> mappingContextFactory) {
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
        return new EnumarableIterator<T>(enumerable(startRow, sheet, newMappingContext()));
    }

    private Enumarable<T> enumerable(int startRow, Sheet sheet, MappingContext<? super Row> mappingContext) {
        return new JoinMapperEnumarable<Row, T>(mapper, mappingContext, new RowEnumarable(startRow, sheet));
    }

    @Override
    public <RH extends CheckedConsumer<T>> RH forEach(Sheet sheet, RH consumer) {
        return forEach(startRow, sheet, consumer);
    }

    @Override
    public <RH extends CheckedConsumer<T>> RH forEach(int startRow, Sheet sheet, RH consumer) {
        MappingContext<? super Row> mappingContext = newMappingContext();

        Enumarable<T> enumarable = enumerable(startRow, sheet, mappingContext);

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
        return StreamSupport.stream(new EnumarableSpliterator<T>(enumerable(startRow, sheet, newMappingContext())), false);
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
