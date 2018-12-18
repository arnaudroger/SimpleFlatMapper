package org.simpleflatmapper.csv;

import org.simpleflatmapper.lightningcsv.CsvReader;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.lightningcsv.parser.CharBuffer;
import org.simpleflatmapper.util.Asserts;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.ErrorHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CsvRowSet implements Enumerable<CsvRow> {

    private final CsvReader csvReader;
    private final CharBuffer charBuffer;
    private CsvRow currentRow;
    private CellConsumer cellConsumer;
    private int limit;
    private CsvColumnKey[] keys;
    private boolean finished;
    

    public CsvRowSet(CsvReader csvReader, int limit) {
        this.csvReader = csvReader;
        this.charBuffer = csvReader.charBuffer();
        this.limit = limit;
    }

    public CsvRowSet(CsvReader csvReader, int limit, CsvColumnKey[] keys) {
        this.csvReader = csvReader;
        this.charBuffer = csvReader.charBuffer();
        this.currentRow = new CsvRow(keys, maxIndex(keys), charBuffer);
        this.cellConsumer = csvReader.wrapConsumer(currentRow);
        this.limit = limit;
        this.keys = keys;
    }

    public CsvColumnKey[] getKeys() throws IOException {
        if (keys == null) {
            this.keys = fetchKeys();
            currentRow = new CsvRow(keys, maxIndex(keys), charBuffer);
            cellConsumer = csvReader.wrapConsumer(currentRow);
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
        if (finished || limit == 0) return false;
        
        currentRow.reset();
        try {
            if (limit != -1) limit--;
            do {
                finished = !csvReader.rawParseRow(cellConsumer, true);
                if (currentRow.hasData()) {
                    return true;
                }
                if (finished) return false;
            } while (true);
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
            int end = lastEmptyKey();
            
            CsvColumnKey[] keys = new CsvColumnKey[end];
            
            for(int i = 0; i < end; i++) {
                keys[i] = keyList.get(i);
            }
            
            return keys;
        }

        private int lastEmptyKey() {
            for(int i = keyList.size() - 1; i >= 0; i--) {
                if (!isEmpty(keyList.get(i))) return i + 1;
            }
            return 0;
        }

        private boolean isEmpty(CsvColumnKey csvColumnKey) {
            String name = csvColumnKey.getName();
            for(int i = 0; i < name.length(); i++) {
                if (name.charAt(i) != ' ') return false;
            }
            return true;
        }
    }

  
}
