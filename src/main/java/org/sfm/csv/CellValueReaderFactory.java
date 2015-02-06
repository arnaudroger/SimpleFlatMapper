package org.sfm.csv;

import java.lang.reflect.Type;


public interface CellValueReaderFactory {
    <P> CellValueReader getReader(Type propertyType, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder parsingContextFactoryBuilder);
}
