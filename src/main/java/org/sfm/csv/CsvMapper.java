package org.sfm.csv;

import java.io.IOException;
import java.io.InputStream;

import org.sfm.map.MappingException;
import org.sfm.utils.RowHandler;

public interface CsvMapper<T> {
	<H extends RowHandler<T>> H forEach(InputStream is, H handle) throws IOException, MappingException;
}
