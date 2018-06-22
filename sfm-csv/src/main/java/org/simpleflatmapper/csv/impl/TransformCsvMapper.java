package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.lightningcsv.CsvReader;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.TransformCheckedConsumer;
import org.simpleflatmapper.util.TransformIterator;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

public class TransformCsvMapper<I, O> implements CsvMapper<O> {
    private final CsvMapper<I> delegate;
    private final Function<I, O> transformer;

    public TransformCsvMapper(CsvMapper<I> delegate, Function<I, O> transformer) {
        this.delegate = delegate;
        this.transformer = transformer;
    }

    @Override
    public <H extends CheckedConsumer<? super O>> H forEach(Reader reader, H handle) throws IOException, MappingException {
        delegate.forEach(reader, new TransformCheckedConsumer<I, O>(handle, transformer));
        return handle;
    }

    @Override
    public <H extends CheckedConsumer<? super O>> H forEach(CsvReader reader, H handle) throws IOException, MappingException {
        delegate.forEach(reader, new TransformCheckedConsumer<I, O>(handle, transformer));
        return handle;
    }

    @Override
    public <H extends CheckedConsumer<? super O>> H forEach(Reader reader, H handle, int skip) throws IOException, MappingException {
        delegate.forEach(reader, new TransformCheckedConsumer<I, O>(handle, transformer), skip);
        return handle;
    }

    @Override
    public <H extends CheckedConsumer<? super O>> H forEach(Reader reader, H handle, int skip, int limit) throws IOException, MappingException {
        delegate.forEach(reader, new TransformCheckedConsumer<I, O>(handle, transformer), skip, limit);
        return handle;
    }

    @Override
    public <H extends CheckedConsumer<? super O>> H forEach(CsvReader reader, H handle, int limit) throws IOException, MappingException {
        delegate.forEach(reader, new TransformCheckedConsumer<I, O>(handle, transformer), limit);
        return handle;
    }

    @Override
    public Iterator<O> iterator(Reader reader) throws IOException {
        return new TransformIterator<I, O>(delegate.iterator(reader), transformer);
    }

    @Override
    public Iterator<O> iterator(CsvReader reader) throws IOException {
        return new TransformIterator<I, O>(delegate.iterator(reader), transformer);
    }

    @Override
    public Iterator<O> iterator(Reader reader, int skip) throws IOException {
        return new TransformIterator<I, O>(delegate.iterator(reader, skip), transformer);
    }
    
//IFJAVA8_START
    @Override
    public Stream<O> stream(Reader reader) throws IOException {
        return delegate.stream(reader).map(transformer::apply);
    }

    @Override
    public Stream<O> stream(CsvReader reader) throws IOException {
        return delegate.stream(reader).map(transformer::apply);
    }

    @Override
    public Stream<O> stream(Reader reader, int skip) throws IOException {
        return delegate.stream(reader, skip).map(transformer::apply);
    }
//IFJAVA8_END
    
}
