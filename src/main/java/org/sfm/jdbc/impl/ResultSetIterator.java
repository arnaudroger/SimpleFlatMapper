package org.sfm.jdbc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.sfm.map.Mapper;

public class ResultSetIterator<T> implements Iterator<T> {

	private final ResultSet rs;
	private final Mapper<ResultSet, T> mapper;
	
	private boolean isFetched;
	private boolean hasValue;
	
	public ResultSetIterator(ResultSet rs, Mapper<ResultSet, T> mapper) {
		this.rs = rs;
		this.mapper = mapper;
	}

	@Override
	public boolean hasNext() {
		fetch();
		return hasValue;
	}

	private void fetch() {
		if (!isFetched) {
			try {
				hasValue = rs.next();
				isFetched = true;
			} catch(SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public T next() {
		fetch();
		if (hasValue) {
			T t = mapper.map(rs);
			isFetched = false;
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
