package org.sfm.csv;

import java.io.InputStream;

import org.sfm.utils.RowHandler;

public interface CsvMapper<T> {
	<H extends RowHandler<T>> H forEach(InputStream is, H handle) throws Exception;
}
