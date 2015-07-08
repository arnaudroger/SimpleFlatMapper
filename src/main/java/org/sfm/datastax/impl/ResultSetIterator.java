package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ResultSetIterator<T> implements Iterator<T> {

	private final ResultSet rs;
	private final Mapper<Row, T> mapper;
    private final MappingContext<? super Row> mappingContext;
	
	public ResultSetIterator(ResultSet rs, Mapper<Row, T> mapper, MappingContext<? super Row> mappingContext) {
		this.rs = rs;
		this.mapper = mapper;
        this.mappingContext = mappingContext;
    }

	@Override
	public boolean hasNext() {
		return !rs.isExhausted();
	}

	@Override
	public T next() {
		Row row = rs.one();
		if (row != null) {
			T t = mapper.map(row, mappingContext);
			return t;
		} else {
			throw new NoSuchElementException("No more rows");
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
