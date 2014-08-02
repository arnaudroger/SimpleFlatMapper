package org.sfm.text;

import java.io.InputStream;

import org.sfm.utils.Handler;

public interface CsvMapper<T> {
	<H extends Handler<T>> H forEach(InputStream is, H handle) throws Exception;
}
