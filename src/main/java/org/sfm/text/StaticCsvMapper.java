package org.sfm.text;

import java.io.InputStream;

import org.sfm.utils.Handler;

public class StaticCsvMapper<T> implements CsvMapper<T> {
	@Override
	public <H extends Handler<T>> H forEach(InputStream is, H handle) throws Exception {
		return handle;
	}
}
