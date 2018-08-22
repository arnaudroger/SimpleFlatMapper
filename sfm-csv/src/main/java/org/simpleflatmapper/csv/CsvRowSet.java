package org.simpleflatmapper.csv;

import org.simpleflatmapper.lightningcsv.CsvReader;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.ErrorHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CsvRowSet implements Enumerable<CsvRow> {

    private final CsvReader csvReader;
    private CsvRow currentRow;
    private int limit;
    private CsvColumnKey[] keys;
    
    CellConsumer cellConsumer = new CellConsumer() {
        @Override
        public void newCell(char[] chars, int offset, int length) {
            currentRow.addValue(chars, offset, length);
        }

        @Override
        public boolean endOfRow() {
            return true;
        }

        @Override
        public void end() {

        }
    };

    public CsvRowSet(CsvReader csvReader, int limit) {
        this.csvReader = csvReader;
        this.limit = limit;
    }

    public CsvRowSet(CsvReader csvReader, int limit, CsvColumnKey[] keys) {
        this.csvReader = csvReader;
        this.currentRow = new CsvRow(keys, maxIndex(keys));
        this.limit = limit;
        this.keys = keys;
    }
    
    public CsvColumnKey[] getKeys() throws IOException {
        if (keys == null) {
            this.keys = fetchKeys();
            currentRow = new CsvRow(keys, maxIndex(keys));
        }
        return keys;
    }

    private int maxIndex(CsvColumnKey[] keys) {
        int i = 0;
        for(CsvColumnKey k : keys) {
            if (k != null) {
                i = Math.max(i, k.getIndex());
            }
        }
        return i;
    }

    private CsvColumnKey[] fetchKeys() throws IOException {
        KeysCellConsumer keysCellConsumer = new KeysCellConsumer();
        csvReader.parseRow(keysCellConsumer);
        return keysCellConsumer.getKeys();
    }


    @Override
    public boolean next() {
        if (limit == 0) return false;
        
        currentRow.reset();
        try {
            if (limit != -1) limit--;
            csvReader.parseRow(cellConsumer);
            return currentRow.hasData();
        } catch (IOException e) {
            return ErrorHelper.rethrow(e);
        }
    }

    @Override
    public CsvRow currentValue() {
        return currentRow;
    }

    private static class KeysCellConsumer implements CellConsumer {
        private static final CsvColumnKey[] EMPTY_KEYS = new CsvColumnKey[0];

        private final List<CsvColumnKey> keyList;

        public KeysCellConsumer() {
            this.keyList = new ArrayList<CsvColumnKey>(10);
        }

        @Override
        public void newCell(char[] chars, int offset, int length) {
            keyList.add(new CsvColumnKey(new String(chars, offset, length), keyList.size()));
        }

        @Override
        public boolean endOfRow() {
            return true;
        }

        @Override
        public void end() {
        }

        public CsvColumnKey[] getKeys() {
            return keyList.toArray(EMPTY_KEYS);
        }
    }
}
