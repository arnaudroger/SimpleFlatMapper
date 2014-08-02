package org.sfm.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public final class CsvParser {
	
	static enum State {
		IN_QUOTE, QUOTE, NONE
	}

	private final int bufferSize;
	
	public CsvParser(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	public CsvParser() {
		this(1<<16);
	}
	
	public <CH extends BytesCellHandler> CH parse(InputStream is, CH handler) throws IOException {
		new InputStreamCsvParser(bufferSize).parse(is, handler);
		return handler;
	}
	
	public <CH extends CharsCellHandler> CH parse(Reader r, CH handler) throws IOException {
		new ReaderCsvParser(bufferSize).parse(r, handler);
		return handler;
	}
}
