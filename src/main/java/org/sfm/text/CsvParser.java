package org.sfm.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public final class CsvParser {
	
	static enum State {
		IN_QUOTE, QUOTE, NONE
	}

	private final int bufferSize;
	
	public CsvParser(final int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	public CsvParser() {
		this(1<<16);
	}
	
	public <CH extends BytesCellHandler> CH parse(final InputStream is, final CH handler) throws IOException {
		new InputStreamCsvParser(bufferSize).parse(is, handler);
		return handler;
	}
	
	public <CH extends CharsCellHandler> CH parse(final Reader r, final CH handler) throws IOException {
		new ReaderCsvParser(bufferSize).parse(r, handler);
		return handler;
	}
}
