package org.sfm.csv.parser;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by e19224 on 08/12/2014.
 */
public interface CsvCharConsumer {
    void parseAll(CellConsumer cellConsumer);

    boolean nextRow(CellConsumer cellConsumer);

    void finish(CellConsumer cellConsumer);

    boolean fillBuffer(Reader reader) throws IOException;
}
