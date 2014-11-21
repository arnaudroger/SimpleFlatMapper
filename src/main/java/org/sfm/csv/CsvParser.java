package org.sfm.csv;

import java.io.IOException;
import java.io.Reader;

import org.sfm.csv.parser.CellConsumer;
import org.sfm.csv.parser.CsvReader;
import org.sfm.csv.parser.StringArrayConsumer;
import org.sfm.utils.RowHandler;

public final class CsvParser {

	public final static CellConsumer DUMMY_CONSUMER = new CellConsumer() {

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

	public static int _4K = 1 << 12;
	public static int _8K = 1 << 13;
	public static int _16K = 1 << 14;
	public static int _32K = 1 << 15;
	public static int _64K = 1 << 16;
	
	public static final int DEFAULT = _8K;
	
	private final int bufferSize;
	
	public CsvParser(final int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	public CsvParser() {
		this(DEFAULT);
	}

	public <CC extends CellConsumer> CC parse(final Reader r, final CC cellConsumer) throws IOException {
		newCsvReader(bufferSize, r).parseAll(cellConsumer);
		return cellConsumer;
	}

	public <CC extends CellConsumer> CC parse(final Reader r, final CC cellConsumer, int skip) throws IOException {
		CsvReader reader = newCsvReader(bufferSize, r);

		reader.skipLines(skip);

		reader.parseAll(cellConsumer);

		return cellConsumer;
	}

	public <CC extends CellConsumer> CC parse(final Reader r, final CC cellConsumer, int skip, int limit) throws IOException {
		CsvReader reader = newCsvReader(bufferSize, r);

		reader.skipLines(skip);

		reader.parseLines(cellConsumer, limit);

		return cellConsumer;
	}

	public <RH extends RowHandler<String[]>> RH readRows(final Reader r, final RH handler) throws IOException {
		parse(r, new StringArrayConsumer(handler));
		return handler;
	}

	public <RH extends RowHandler<String[]>> RH readRows(final Reader r, final RH handler, int skip) throws IOException {
		parse(r, new StringArrayConsumer(handler), skip);
		return handler;
	}

	public <RH extends RowHandler<String[]>> RH readRows(final Reader r, final RH handler, int skip, int limit) throws IOException {
		parse(r, new StringArrayConsumer(handler), skip, limit);
		return handler;
	}

	public static CsvReader newCsvReader(Reader reader) {
		return newCsvReader(DEFAULT, reader);
	}

	public static CsvReader newCsvReader(int bufferSize, Reader reader) {
		return new CsvReader(bufferSize, reader);
	}
}
