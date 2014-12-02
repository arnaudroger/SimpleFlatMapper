package org.sfm.csv.parser;

import org.sfm.csv.CsvParser;

import java.io.IOException;
import java.io.Reader;

public final class CsvReader {



	private final Reader reader;
	private final CsvCharConsumer consumer;

	public CsvReader(CharBuffer buffer, Reader reader) {
		this.reader = reader;
		this.consumer = new CsvCharConsumer(buffer);
	}

	public CsvReader(final int bufferSize, final Reader reader) {
		this(new CharBuffer(bufferSize), reader);
	}

	/**
	 * parse cvs
	 * 
	 * @throws IOException
	 */
	public void parseAll(CellConsumer cellConsumer)
			throws IOException {
		do {
			consumer.parseFull(cellConsumer);
		} while (consumer.fillBuffer(reader));
		consumer.finish(cellConsumer);
	}

	/**
	 * parse cvs
	 *
	 * @throws IOException
	 */
	public boolean parseLine(CellConsumer cellConsumer)
			throws IOException {

		do {
			if (consumer.nextLine(cellConsumer)) {
				return true;
			}
		} while (consumer.fillBuffer(reader));

		consumer.finish(cellConsumer);
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
