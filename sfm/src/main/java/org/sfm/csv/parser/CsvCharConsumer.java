package org.sfm.csv.parser;

import java.io.IOException;
import java.io.Reader;

public interface CsvCharConsumer {
    void parseAll(CellConsumer cellConsumer);

    boolean nextRow(CellConsumer cellConsumer);

    void finish(CellConsumer cellConsumer);

    boolean fillBuffer(Reader reader) throws IOException;

    char quoteChar();
}
