package org.sfm.csv.impl;

import java.io.IOException;
import java.util.Iterator;

import org.sfm.csv.parser.CellConsumer;
import org.sfm.csv.parser.CsvReader;
import org.sfm.map.impl.AbstractMapperImpl;
import org.sfm.utils.RowHandler;

public class CsvMapperIterator<T> implements Iterator<T> {

	
	private T currentValue;
	private boolean isFetched;
	
	private final CsvReader reader;
	private final CellConsumer cellConsumer;
	
	public CsvMapperIterator(CsvReader reader, CsvMapperImpl<T> csvMapperImpl) {
		cellConsumer = csvMapperImpl.newCellConsumer(new RowHandler<T>() {
			@Override
			public void handle(T t) throws Exception {
				currentValue = t;
			}
		});
		this.reader = reader;
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
			reader.parseLine(cellConsumer);
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
