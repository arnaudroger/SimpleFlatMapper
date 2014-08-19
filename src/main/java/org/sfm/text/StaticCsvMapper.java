package org.sfm.text;

import java.io.InputStream;

import org.sfm.utils.Handler;

public final class StaticCsvMapper<T> implements CsvMapper<T> {
	@Override
	public <H extends Handler<T>> H forEach(final InputStream is, final H handle) throws Exception {
		return handle;
	}
}
