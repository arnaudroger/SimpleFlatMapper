package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;

import java.util.Spliterator;
import java.util.function.Consumer;

public class ResultSetSpliterator<T> implements Spliterator<T> {
    private final ResultSet resultSet;
    private final Mapper<Row, T> mapper;
    private final MappingContext<? super Row> mappingContext;

    public ResultSetSpliterator(ResultSet resultSet, Mapper<Row, T> mapper, MappingContext<? super Row> mappingContext) {
        this.resultSet = resultSet;
        this.mapper = mapper;
        this.mappingContext = mappingContext;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        ResultSet lResultSet = this.resultSet;
        if (!lResultSet.isExhausted()) {
            action.accept(mapper.map(lResultSet.one(), mappingContext));
            return true;
        }
        return false;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        ResultSet lResultSet = this.resultSet;
        Mapper<Row, T> lMapper = this.mapper;
        MappingContext<? super Row> lMappingContext = this.mappingContext;
        Row r;
        while((r = lResultSet.one()) != null) {
            action.accept(lMapper.map(r, lMappingContext));
        }
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return Spliterator.ORDERED | Spliterator.NONNULL;
    }
}
