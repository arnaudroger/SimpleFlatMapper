package org.sfm.csv.parser;

import org.sfm.csv.CsvParser;

import java.io.IOException;
import java.io.Reader;

public final class CsvReader {



	private final Reader reader;
	private final CharBuffer buffer;
	private final CharConsumer consumer;

	public CsvReader(final int bufferSize, final Reader reader) {
		this.buffer = new CharBuffer(bufferSize);
		this.consumer = new CharConsumer();
		this.reader = reader;
	}

	/**
	 * parse cvs
	 * 
	 * @throws IOException
	 */
	public void parseAll(CellConsumer cellConsumer)
			throws IOException {
		do {
			consumer.parseFull(buffer, cellConsumer);
		} while (buffer.fillBuffer(reader));
		consumer.finish(buffer, cellConsumer);
	}

	/**
	 * parse cvs
	 *
	 * @throws IOException
	 */
	public boolean parseLine(CellConsumer cellConsumer)
			throws IOException {

		do {
			if (consumer.nextLine(buffer, cellConsumer)) {
				return true;
			}
		} while (buffer.fillBuffer(reader));

		consumer.finish(buffer, cellConsumer);
		return false;
	}


	public void skipLines(int n) throws IOException {
		parseLines(CsvParser.DUMMY_CONSUMER, n);
	}

	public void parseLines(CellConsumer cellConsumer, int limit) throws IOException {
		for(int i = 0; i < limit; i++) {
			parseLine(cellConsumer);
		}
	}
}
