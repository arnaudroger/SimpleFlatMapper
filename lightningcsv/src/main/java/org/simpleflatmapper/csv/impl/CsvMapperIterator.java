package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.CsvReader;
import org.simpleflatmapper.csv.parser.CellConsumer;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.CheckedConsumer;

import java.io.IOException;
import java.util.Iterator;

public class CsvMapperIterator<T> implements Iterator<T> {

	
	private T currentValue;
	private boolean isFetched;
	
	private final CsvReader reader;
	private final CellConsumer cellConsumer;
	
	public CsvMapperIterator(CsvReader reader, CsvMapperImpl<T> csvMapperImpl) {
		cellConsumer = csvMapperImpl.newCellConsumer(new CheckedConsumer<T>() {
			@Override
			public void accept(T t) throws Exception {
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
			reader.parseRow(cellConsumer);
			isFetched = true;
		} catch (IOException e) {
            ErrorHelper.rethrow(e);
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
