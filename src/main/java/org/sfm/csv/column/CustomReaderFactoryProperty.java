package org.sfm.csv.column;


import org.sfm.csv.CellValueReaderFactory;
import org.sfm.map.column.ColumnProperty;

public class CustomReaderFactoryProperty implements ColumnProperty {
    private final CellValueReaderFactory readerFactory;

    public CustomReaderFactoryProperty(CellValueReaderFactory readerFactory) {
        if (readerFactory == null) throw new NullPointerException();
        this.readerFactory = readerFactory;
    }

    public CellValueReaderFactory getReaderFactory() {
        return readerFactory;
    }

    @Override
    public String toString() {
        return "CellValueReaderFactory{CellValueReaderFactory}";
    }
}
