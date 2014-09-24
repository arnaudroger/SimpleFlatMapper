package org.sfm.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.sfm.map.MappingException;
import org.sfm.utils.RowHandler;

public interface CsvMapper<T> {
	<H extends RowHandler<T>> H forEach(InputStream is, H handle) throws IOException, MappingException;
	<H extends RowHandler<T>> H forEach(Reader reader, H handle) throws IOException, MappingException;
}
