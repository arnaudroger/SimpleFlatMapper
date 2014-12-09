package org.sfm.csv.parser;

import org.sfm.csv.CsvParser;

import java.io.IOException;
import java.io.Reader;

public final class CsvReader {

	private final static CellConsumer DUMMY_CONSUMER = new CellConsumer() {

		@Override
		public void newCell(char[] chars, int offset, int length) {
		}

		@Override
		public void endOfRow() {
		}

		@Override
		public void end() {
		}
	};

	private final Reader reader;
	private final CsvCharConsumer consumer;

	public CsvReader(CharBuffer buffer, Reader reader) {
		this.reader = reader;
		this.consumer = new StandardCsvCharConsumer(buffer);
	}

	public CsvReader(Reader reader, CsvCharConsumer charConsumer) {
		this.reader = reader;
		this.consumer = charConsumer;
	}

	public CsvReader(final int bufferSize, final Reader reader) {
		this(new CharBuffer(bufferSize), reader);
	}

	/**
	 * parse cvs
	 * 
	 * @throws IOException
	 */
	public <CC extends CellConsumer> CC parseAll(CC cellConsumer)
			throws IOException {
		do {
			consumer.parseAll(cellConsumer);
		} while (consumer.fillBuffer(reader));
		consumer.finish(cellConsumer);

		return cellConsumer;
	}

	/**
	 * parse cvs
	 *
	 * @throws IOException
	 */
	public boolean parseRow(CellConsumer cellConsumer)
			throws IOException {

		do {
			if (consumer.nextRow(cellConsumer)) {
				return true;
			}
		} while (consumer.fillBuffer(reader));

		consumer.finish(cellConsumer);
		return false;
	}


	public void skipRows(int n) throws IOException {
		parseRows(DUMMY_CONSUMER, n);
	}

	public void parseRows(CellConsumer cellConsumer, int limit) throws IOException {
		for(int i = 0; i < limit; i++) {
			parseRow(cellConsumer);
		}
	}
}
