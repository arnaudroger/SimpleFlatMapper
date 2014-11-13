package org.sfm.csv.parser;

import java.io.IOException;
import java.io.Reader;

public final class CsvReader {



	private final Reader reader;
	private final CharBuffer buffer;
	private final CharConsumer consumer;

	public CsvReader(final int bufferSize, final CharsCellHandler handler, final Reader reader) {
		this.buffer = new CharBuffer(bufferSize);
		this.consumer = new CharConsumer(handler);
		this.reader = reader;
	}

	/**
	 * parse cvs
	 * 
	 * @return
	 * @throws IOException
	 */
	public void parse()
			throws IOException {
		buffer.parse(reader, consumer);
	}
}
