package org.sfm.csv;

import org.sfm.csv.CellValueReader;

import java.lang.reflect.Type;


public interface CellValueReaderFactory {
    <P> CellValueReader<P> getReader(Type propertyType, int index);
}
