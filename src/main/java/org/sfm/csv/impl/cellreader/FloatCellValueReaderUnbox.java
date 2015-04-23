package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class FloatCellValueReaderUnbox implements FloatCellValueReader {
    private final CellValueReader<Float> reader;

    public FloatCellValueReaderUnbox(CellValueReader<Float> customReader) {
        this.reader = customReader;
    }

    @Override
    public float readFloat(CharSequence value, ParsingContext parsingContext) {
        return read(value, parsingContext);
    }

    @Override
    public Float read(CharSequence value, ParsingContext parsingContext) {
        return reader.read(value, parsingContext);
    }

    @Override
    public String toString() {
        return "FloatCellValueReaderUnbox{" +
                "reader=" + reader +
                '}';
    }
}
