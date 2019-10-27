package org.simpleflatmapper.lightningcsv;

import java.io.Closeable;
import java.io.IOException;

public class ClosableCsvWriter extends CsvWriter implements Closeable {

    private final Closeable resource;
    public <T extends Appendable & Closeable> ClosableCsvWriter(CellWriter cellWriter, T appendable) {
        super(cellWriter, appendable);
        this.resource = appendable;
    }

    @Override
    public void close() throws IOException {
        resource.close();
    }
}
