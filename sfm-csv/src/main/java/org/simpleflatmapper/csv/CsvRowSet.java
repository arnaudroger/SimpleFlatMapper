package org.simpleflatmapper.csv;

import org.simpleflatmapper.lightningcsv.CsvReader;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.ErrorHelper;

import java.io.IOException;

public class CsvRowSet implements Enumerable<CsvRow> {
    
    private final CsvReader csvReader;
    private final CsvRow currentRow;
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

    public CsvRowSet(CsvReader csvReader) {
        this.csvReader = csvReader;
        this.currentRow = new CsvRow();
    }

    
    @Override
    public boolean next() {
        currentRow.reset();
        try {
            csvReader.parseRow(cellConsumer);
            return currentRow.getNbColumns() > 0;
        } catch (IOException e) {
            return ErrorHelper.rethrow(e);
        }
    }

    @Override
    public CsvRow currentValue() {
        return currentRow;
    }
}
