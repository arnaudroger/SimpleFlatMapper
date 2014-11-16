package org.sfm.csv.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.sfm.csv.CsvParser;
import org.sfm.csv.parser.CharsCellHandler;
import org.sfm.csv.parser.CsvReader;
import org.sfm.utils.RowHandler;

public class CsvIterator<T> implements Iterator<T> {

	
	private T currentValue;
	private boolean isFetched;
	
	private final CsvReader reader;
	private CharsCellHandler handler;
	
	public CsvIterator(Reader reader, CsvMapperImpl<T> csvMapperImpl, int rowStart) {
		handler = csvMapperImpl.newCellHandler(new RowHandler<T>() {
			@Override
			public void handle(T t) throws Exception {
				currentValue = t;
			}
		}, rowStart, -1, false);
		this.reader = new CsvReader(CsvParser.DEFAULT, handler, reader);
	}
	public CsvIterator(Reader reader, DynamicCsvMapper<T> csvMapperImpl, int rowStart) {
		handler = csvMapperImpl.newPullCellHandler(new RowHandler<T>() {
			@Override
			public void handle(T t) throws Exception {
				currentValue = t;
			}
		}, rowStart);
		this.reader = new CsvReader(CsvParser.DEFAULT, handler, reader);
	}
	
	@Override
	public boolean hasNext() {
		fetch();
		return currentValue != null;
	}

	private void fetch() {
		if (isFetched) return;
		try {
			currentValue = null;
			reader.parse();
			isFetched = true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T next() {
		fetch();
		isFetched = false;
		return currentValue;
	}

	@Override
	public void remove() {
        throw new UnsupportedOperationException("remove");
    }
	
}
