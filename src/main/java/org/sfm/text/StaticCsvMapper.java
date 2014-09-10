package org.sfm.text;

import java.io.InputStream;

import org.sfm.utils.RowHandler;

public final class StaticCsvMapper<T> implements CsvMapper<T> {
	@Override
	public <H extends RowHandler<T>> H forEach(final InputStream is, final H handle) throws Exception {
		return handle;
	}
}
