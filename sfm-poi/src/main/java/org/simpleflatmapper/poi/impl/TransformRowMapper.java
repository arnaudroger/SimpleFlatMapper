package org.simpleflatmapper.poi.impl;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.TransformEnumerable;
import org.simpleflatmapper.poi.RowMapper;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.TransformCheckedConsumer;
import org.simpleflatmapper.util.TransformIterator;

import java.util.Iterator;

public class TransformRowMapper<I, O> implements RowMapper<O> {
    private final Function<I, O> transform;
    private final RowMapper<I> delegate;

    public TransformRowMapper(RowMapper<I> delegate, Function<I, O> transform) {
        this.delegate = delegate;
        this.transform = transform;
    }

    @Override
    public O map(Row source) throws MappingException {
        return transform.apply(delegate.map(source));
    }

    @Override
    public O map(Row source, MappingContext<? super Row> context) throws MappingException {
        return transform.apply(delegate.map(source, context));
    }

    @Override
    public Iterator<O> iterator(Sheet sheet) {
        return new TransformIterator<I, O>(delegate.iterator(sheet), transform);
    }

    @Override
    public Iterator<O> iterator(int startRow, Sheet sheet) {
        return new TransformIterator<I, O>(delegate.iterator(startRow, sheet), transform);
    }

    @Override
    public Enumerable<O> enumerate(Sheet sheet) {
        return new TransformEnumerable<I, O>(delegate.enumerate(sheet), transform);
    }

    @Override
    public Enumerable<O> enumerate(int startRow, Sheet sheet) {
        return new TransformEnumerable<I, O>(delegate.enumerate(startRow, sheet), transform);
    }

    @Override
    public <RH extends CheckedConsumer<? super O>> RH forEach(Sheet sheet, RH consumer) {
        delegate.forEach(sheet, new TransformCheckedConsumer<I, O>(consumer, transform));
        return consumer;
    }

    @Override
    public <RH extends CheckedConsumer<? super O>> RH forEach(int startRow, Sheet sheet, RH consumer) {
        delegate.forEach(startRow, sheet, new TransformCheckedConsumer<I, O>(consumer, transform));
        return consumer;
    }


    //IFJAVA8_START
    @Override
    public java.util.stream.Stream<O> stream(Sheet sheet)  {
        return delegate.stream(sheet).map(transform::apply);
    }
    public java.util.stream.Stream<O> stream(int startRow, Sheet sheet)  {
        return delegate.stream(startRow, sheet).map(transform::apply);
    }
    //IFJAVA8_END

}
