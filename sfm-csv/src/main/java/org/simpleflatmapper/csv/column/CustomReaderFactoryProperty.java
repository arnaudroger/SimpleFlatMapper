package org.simpleflatmapper.csv.column;


import org.simpleflatmapper.csv.CellValueReaderFactory;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class CustomReaderFactoryProperty {
    private final CellValueReaderFactory readerFactory;

    public CustomReaderFactoryProperty(CellValueReaderFactory readerFactory) {
        this.readerFactory = requireNonNull("readerFactory", readerFactory);
    }

    public CellValueReaderFactory getReaderFactory() {
        return readerFactory;
    }

    @Override
    public String toString() {
        return "CellValueReaderFactory{CellValueReaderFactory}";
    }
}
