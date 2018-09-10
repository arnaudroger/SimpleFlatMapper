package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.csv.CsvRowSet;
import org.simpleflatmapper.lightningcsv.CsvReader;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Enumerable;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

public class CsvMapperImpl<T> implements CsvMapper<T> {
    private final SetRowMapper<CsvRow, CsvRowSet, T, IOException> setRowMapper;
    private final CsvColumnKey[] keys;

    public CsvMapperImpl(SetRowMapper<CsvRow, CsvRowSet, T, IOException> setRowMapper, CsvColumnKey[] keys) {
        this.setRowMapper = setRowMapper;
        this.keys = keys;
    }

    @Override
    public <H extends CheckedConsumer<? super T>> H forEach(Reader reader, H handle) throws IOException, MappingException {
        forEach(toCsvRowSet(reader, 0, -1), handle);
        return handle;
    }

    @Override
    public <H extends CheckedConsumer<? super T>> H forEach(CsvReader reader, H handle) throws IOException, MappingException {
        forEach(toCsvRowSet(reader, 0 , -1), handle);
        return handle;
    }

    @Override
    public <H extends CheckedConsumer<? super T>> H forEach(Reader reader, H handle, int skip) throws IOException, MappingException {
        forEach(toCsvRowSet(reader, skip, -1), handle);
        return handle;
    }

    @Override
    public <H extends CheckedConsumer<? super T>> H forEach(Reader reader, H handle, int skip, int limit) throws IOException, MappingException {
        forEach(toCsvRowSet(reader, skip, limit), handle);
        return handle;
    }

    @Override
    public <H extends CheckedConsumer<? super T>> H forEach(CsvReader reader, H handle, int limit) throws IOException, MappingException {
        forEach(toCsvRowSet(reader, 0, limit), handle);
        return handle;
    }

    @Override
    public Iterator<T> iterator(Reader reader) throws IOException {
        return iterator(toCsvRowSet(reader, 0 , -1));
    }

    @Override
    public Iterator<T> iterator(CsvReader reader) throws IOException {
        return iterator(toCsvRowSet(reader, 0 , -1));
    }

    @Override
    public Iterator<T> iterator(Reader reader, int skip) throws IOException {
        return iterator(toCsvRowSet(reader, skip , -1));
    }

    //IFJAVA8_START
    @Override
    public Stream<T> stream(Reader reader) throws IOException {
        return stream(toCsvRowSet(reader, 0 , -1));
    }

    @Override
    public Stream<T> stream(CsvReader reader) throws IOException {
        return stream(toCsvRowSet(reader, 0 , -1));
    }

    @Override
    public Stream<T> stream(Reader reader, int skip) throws IOException {
        return stream(toCsvRowSet(reader, skip , -1));
    }
    //IFJAVA8_END



    @Override
    public <H extends CheckedConsumer<? super T>> H forEach(CsvRowSet source, H handler) throws IOException, MappingException {
        setRowMapper.forEach(source, handler);
        return handler;
    }

    @Override
    public Iterator<T> iterator(CsvRowSet source) throws IOException, MappingException {
        return setRowMapper.iterator(source);
    }

    //IFJAVA8_START
    @Override
    public Stream<T> stream(CsvRowSet source) throws IOException, MappingException {
        return setRowMapper.stream(source);
    }
    //IFJAVA8_END

    @Override
    public Enumerable<T> enumerate(CsvRowSet source) throws IOException, MappingException {
        return setRowMapper.enumerate(source);
    }

    @Override
    public T map(CsvRow source) throws MappingException {
        return setRowMapper.map(source);
    }

    @Override
    public T map(CsvRow source, MappingContext<? super CsvRow> context) throws MappingException {
        return setRowMapper.map(source, context);
    }

    private CsvRowSet toCsvRowSet(Reader reader, int skip, int limit) throws IOException {
        return toCsvRowSet(CsvParser.reader(reader), skip, limit);
    }

    private CsvRowSet toCsvRowSet(CsvReader reader, int skip, int limit) throws IOException {
        reader.skipRows(skip);
        return new CsvRowSet(reader, limit, keys);
    }

}
