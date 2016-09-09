package org.simpleflatmapper.csv;

import org.simpleflatmapper.csv.parser.CellConsumer;
import org.simpleflatmapper.csv.parser.CsvCharConsumer;
import org.simpleflatmapper.csv.parser.CsvStringArrayIterator;
import org.simpleflatmapper.csv.parser.StringArrayConsumer;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.RowHandler;

import java.io.IOException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END



public final class CsvReader implements Iterable<String[]> {

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

	private final CsvCharConsumer consumer;

	public CsvReader(CsvCharConsumer charConsumer) {
		this.consumer = charConsumer;
	}

	/**
	 * parse cvs
     * @param cellConsumer the consumer that the parser will callback
	 * @param <CC> the cell consumer type
     * @throws java.io.IOException if an io error occurs
     * @return the cell consumer
	 */
	public <CC extends CellConsumer> CC parseAll(CC cellConsumer)
			throws IOException {
		do {
			consumer.consumeAllBuffer(cellConsumer);
		} while (consumer.refillBuffer());
		consumer.finish(cellConsumer);

		return cellConsumer;
	}

	/**
	 * parse cvs
	 * @param cellConsumer the cell consumer
     * @return true if there was data consumed
	 * @throws IOException if io error occurs
	 */
	public boolean parseRow(CellConsumer cellConsumer)
			throws IOException {

		do {
			if (consumer.consumeToNextRow(cellConsumer)) {
				return true;
			}
		} while (consumer.refillBuffer());

		consumer.finish(cellConsumer);
		return false;
	}


	public void skipRows(int n) throws IOException {
		parseRows(DUMMY_CONSUMER, n);
	}

	public <CC extends CellConsumer> CC  parseRows(CC cellConsumer, int limit) throws IOException {
		for(int i = 0; i < limit; i++) {
			parseRow(cellConsumer);
		}
		return cellConsumer;
	}

	public <RH extends RowHandler<String[]>> RH read(RH handler) throws IOException {
		parseAll(StringArrayConsumer.newInstance(handler));
		return handler;
	}

	public <RH extends RowHandler<String[]>> RH read(RH handler, int limit) throws IOException {
		parseRows(StringArrayConsumer.newInstance(handler), limit);
		return handler;
	}

	@Override
	public Iterator<String[]> iterator() {
		return new CsvStringArrayIterator(this);
	}

	//IFJAVA8_START
	public Stream<String[]> stream() {
		return StreamSupport.stream(new CsvStringArraySpliterator(this), false);
	}

	private static class CsvStringArraySpliterator implements Spliterator<String[]> {
		private final CsvReader reader;

		public CsvStringArraySpliterator(CsvReader csvReader) {
			this.reader = csvReader;
		}

		@Override
		public boolean tryAdvance(Consumer<? super String[]> action) {
			try {
				return reader.parseRow(StringArrayConsumer.newInstance((strings) -> action.accept(strings)));
			} catch (IOException e) {
               return ErrorHelper.rethrow(e);
			}
		}

		@Override
		public void forEachRemaining(Consumer<? super String[]> action) {
			try {
				reader.parseAll(StringArrayConsumer.newInstance((strings) -> action.accept(strings)));
			} catch (IOException e) {
                ErrorHelper.rethrow(e);
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

}
