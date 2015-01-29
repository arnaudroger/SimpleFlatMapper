package org.sfm.csv.parser;

import org.sfm.csv.CsvReader;
import org.sfm.utils.RowHandler;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CsvStringArrayIterator implements Iterator<String[]> {

    private final CsvReader reader;
    private final CellConsumer cellConsumer;

    private boolean isFetched;
    private String[] value;

    @SuppressWarnings("unchecked")
    public CsvStringArrayIterator(CsvReader csvReader) {
        cellConsumer = new StringArrayConsumer(new RowHandler<String[]>() {
            @Override
            public void handle(String[] strings) throws Exception {
                value = strings;
            }
        });
        reader = csvReader;
    }

    @Override
    public boolean hasNext() {
        fetch();
        return value != null;
    }

    private void fetch() {
        if (!isFetched) {
            try {
                value = null;
                reader.parseRow(cellConsumer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            isFetched = true;
        }
    }

    @Override
    public String[] next() {
        fetch();
        if (value == null) throw new NoSuchElementException();
        isFetched = false;
        return value;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
