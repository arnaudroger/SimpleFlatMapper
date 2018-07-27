package org.simpleflatmapper.lightningcsv;

import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.util.CheckedConsumer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

public class CloseableCsvReader implements Closeable, Iterable<String[]> {

    private final CsvReader delegate;
    private final Closeable resource;

    public CloseableCsvReader(CsvReader delegate, Closeable resource) {
        this.delegate = delegate;
        this.resource = resource;
    }

    public <CC extends CellConsumer> CC parseAll(CC cellConsumer) throws IOException {
        return delegate.parseAll(cellConsumer);
    }

    public boolean parseRow(CellConsumer cellConsumer) throws IOException {
        return delegate.parseRow(cellConsumer);
    }

    public void skipRows(int n) throws IOException {
        delegate.skipRows(n);
    }

    public <CC extends CellConsumer> CC parseRows(CC cellConsumer, int limit) throws IOException {
        return delegate.parseRows(cellConsumer, limit);
    }

    public <RH extends CheckedConsumer<String[]>> RH read(RH handler) throws IOException {
        return delegate.read(handler);
    }

    public <RH extends CheckedConsumer<String[]>> RH read(RH handler, int limit) throws IOException {
        return delegate.read(handler, limit);
    }

    @Override
    public Iterator<String[]> iterator() {
        return delegate.iterator();
    }


    //IFJAVA8_START
    public Stream<String[]> stream() {
        return delegate.stream();
    }
    //IFJAVA8_END

    @Override
    public void close() throws IOException {
        resource.close();
    }
}
