package org.sfm.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

//IFJAVA8_START
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END

import org.sfm.csv.parser.CellConsumer;
import org.sfm.csv.parser.CsvReader;
import org.sfm.csv.parser.CsvStringArrayIterator;
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

	public static int _8K = 1 << 13;

	public static final int DEFAULT = _8K;

	public static <CC extends CellConsumer> CC parse(final Reader r, final CC cellConsumer) throws IOException {
		newCsvReader(r).parseAll(cellConsumer);
		return cellConsumer;
	}

	public static <CC extends CellConsumer> CC parse(final Reader r, final CC cellConsumer, int skip) throws IOException {
		CsvReader reader = newCsvReader(r);

		reader.skipRows(skip);

		reader.parseAll(cellConsumer);

		return cellConsumer;
	}

	public static <CC extends CellConsumer> CC parse(final Reader r, final CC cellConsumer, int skip, int limit) throws IOException {
		CsvReader reader = newCsvReader(r);

		reader.skipRows(skip);

		reader.parseRows(cellConsumer, limit);

		return cellConsumer;
	}

	public static <RH extends RowHandler<String[]>> RH readRows(final Reader r, final RH handler) throws IOException {
		parse(r, new StringArrayConsumer<RH>(handler));
		return handler;
	}

	public static <RH extends RowHandler<String[]>> RH readRows(final Reader r, final RH handler, int skip) throws IOException {
		parse(r, new StringArrayConsumer<RH>(handler), skip);
		return handler;
	}

	public static <RH extends RowHandler<String[]>> RH readRows(final Reader r, final RH handler, int skip, int limit) throws IOException {
		parse(r, new StringArrayConsumer<RH>(handler), skip, limit);
		return handler;
	}

	public static CsvParserBuilder separator(char c) {
		return new CsvParserBuilder().separator(c);
	}

	public static CsvParserBuilder bufferSize(int size) {
		return new CsvParserBuilder().bufferSize(size);
	}

	public static CsvParserBuilder quote(char c) {
		return new CsvParserBuilder().quote(c);
	}

	public static CsvReader reader(Reader reader) {
		return new CsvParserBuilder().reader(reader);
	}

	public static Iterator<String[]> iterateRows(Reader r) {
		return new CsvStringArrayIterator(newCsvReader(r));
	}

	public static Iterator<String[]> iterateRows(Reader r, int skip) throws IOException {
		CsvReader csvReader = newCsvReader(r);
		csvReader.skipRows(skip);
		return new CsvStringArrayIterator(csvReader);
	}

	//IFJAVA8_START
	public static Stream<String[]> stream(Reader r) {
		CsvReader csvReader = newCsvReader(r);
		return stream(csvReader);
	}

	public static Stream<String[]> stream(CsvReader csvReader) {
		Spliterator<String[]> spliterator = new CsvStringArraySpliterator(csvReader);
		return StreamSupport.stream(spliterator, false);
	}

	public static Stream<String[]> stream(Reader r, int skip) throws IOException {
		CsvReader csvReader = newCsvReader(r);
		csvReader.skipRows(skip);
		Spliterator<String[]> spliterator = new CsvStringArraySpliterator(csvReader);
		return StreamSupport.stream(spliterator, false);
	}

	private static class CsvStringArraySpliterator implements Spliterator<String[]> {
		private final CsvReader reader;

		public CsvStringArraySpliterator(CsvReader csvReader) {
			this.reader = csvReader;
		}

		@Override
		public boolean tryAdvance(Consumer<? super String[]> action) {
			try {
				return reader.parseRow(new StringArrayConsumer<RowHandler<String[]>>((strings) -> action.accept(strings)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void forEachRemaining(Consumer<? super String[]> action) {
			try {
				reader.parseAll(new StringArrayConsumer<RowHandler<String[]>>((strings) -> action.accept(strings)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Spliterator<String[]> trySplit() {
			return null;
		}

		@Override
		public long estimateSize() {
			return Long.MAX_VALUE;
		}

		@Override
		public int characteristics() {
			return Spliterator.ORDERED | Spliterator.NONNULL;
		}
	}

	//IFJAVA8_END


	public static CsvReader newCsvReader(Reader reader) {
		return reader(reader);
	}

	public static CsvReader newCsvReader(int bufferSize, Reader reader) {
		return bufferSize(bufferSize).reader(reader);
	}
}
