package org.sfm.csv.parser;

import java.io.IOException;
import java.io.Reader;

public abstract class CsvCharConsumer {
    public abstract void consumeAllBuffer(CellConsumer cellConsumer);

    public abstract boolean consumeToNextRow(CellConsumer cellConsumer);

    public abstract void finish(CellConsumer cellConsumer);

    public abstract boolean refillBuffer() throws IOException;

    public abstract char quoteChar();
}
