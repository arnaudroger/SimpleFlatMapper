package org.simpleflatmapper.csv;

import java.lang.reflect.Type;

public interface CellValueReaderFactory {
    <P> CellValueReader<P> getReader(Type propertyType, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder parsingContextFactoryBuilder);
}
