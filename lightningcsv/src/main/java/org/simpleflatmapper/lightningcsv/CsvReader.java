package org.simpleflatmapper.lightningcsv;

import org.simpleflatmapper.lightningcsv.impl.CellConsumerCapture;
import org.simpleflatmapper.lightningcsv.impl.CellConsumerFixLengthToCheckConsumer;
import org.simpleflatmapper.lightningcsv.parser.AbstractCharConsumer;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.lightningcsv.parser.CharBuffer;
import org.simpleflatmapper.lightningcsv.parser.NullCellConsumer;
import org.simpleflatmapper.lightningcsv.parser.StringArrayCellConsumer;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Function;

import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
//IFJAVA8_START
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END



public final class CsvReader implements Iterable<String[]> {

	private final AbstractCharConsumer consumer;

	private final Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper;

	public CsvReader(AbstractCharConsumer charConsumer) {
		this(charConsumer, null);
	}

	public CsvReader(AbstractCharConsumer charConsumer, Function<? super CellConsumer, ? extends CellConsumer> cellConsumerWrapper) {
		this.consumer = charConsumer;
		this.cellConsumerWrapper = cellConsumerWrapper;
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
		_parseAll(wrapConsumer(cellConsumer), false);

		return cellConsumer;
	}

	private <CC extends CellConsumer> void _parseAll(CC cellConsumer, boolean keepRow) throws IOException {
		do {
			consumer.consumeAllBuffer(cellConsumer);
		} while(consumer.shiftAndRead(keepRow));

		consumer.finish(cellConsumer);
	}


	/**
	 * parse cvs
	 * @param cellConsumer the cell consumer
     * @return true if there was data consumed
	 * @throws IOException if io error occurs
	 */
	public boolean parseRow(CellConsumer cellConsumer)
			throws IOException {
		return rawParseRow(wrapConsumer(cellConsumer), false);
	}

	public boolean rawParseRow(CellConsumer cellConsumer, boolean keepRow) throws IOException {
		do {
			if (consumer.consumeToNextRow(cellConsumer)) {
				return true;
			}
		} while(consumer.shiftAndRead(keepRow));

		consumer.finish(cellConsumer);
		return false;
	}


	public void skipRows(int n) throws IOException {
		_parseRows(NullCellConsumer.INSTANCE, n);
	}

	public <CC extends CellConsumer> CC  parseRows(CC cellConsumer, int limit) throws IOException {
		_parseRows(wrapConsumer(cellConsumer), limit);
		return cellConsumer;
	}

	private <CC extends CellConsumer> void _parseRows(CC cellConsumer, int limit) throws IOException {
		for(int i = 0; i < limit; i++) {
			rawParseRow(cellConsumer, false);
		}
	}

	public <RH extends CheckedConsumer<String[]>> RH read(RH consumer) throws IOException {
		parseAll(toCellConsumer(consumer));
		return consumer;
	}

	public <RH extends CheckedConsumer<String[]>> RH read(RH consumer, int limit) throws IOException {
		parseRows(toCellConsumer(consumer), limit);
		return consumer;
	}

	private CellConsumer toCellConsumer(CheckedConsumer<String[]> consumer) {
		return StringArrayCellConsumer.newInstance(consumer);
	}

	public CellConsumer wrapConsumer(CellConsumer cellConsumer) {
		if (cellConsumerWrapper == null) return cellConsumer;
		return cellConsumerWrapper.apply(cellConsumer);
	}

	public CharBuffer charBuffer() {
		return consumer.charBuffer();
	}

	@Override
	public Iterator<String[]> iterator() {
		return new CsvStringArrayIterator(this);
	}
	
	public Iterator<Row> rowIterator() throws IOException {
		return new CsvRowArrayIterator(this);
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
				return reader.parseRow(reader.toCellConsumer(action::accept));
			} catch (IOException e) {
               return ErrorHelper.rethrow(e);
			}
		}

		@Override
		public void forEachRemaining(Consumer<? super String[]> action) {
			try {
				reader.parseAll(reader.toCellConsumer(action::accept));
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

	public Stream<Row> rowStream() {
		return StreamSupport.stream(new CsvRowSpliterator(this), false);
	}

	private static class CsvRowSpliterator implements Spliterator<Row> {
		private final CsvReader reader;
		
		private Row.Headers headers;

		public CsvRowSpliterator(CsvReader csvReader) {
			this.reader = csvReader;
		}

		@Override
		public boolean tryAdvance(Consumer<? super Row> action) {
			try {
				if (headers == null) {
					headers = headers(reader);
				}
				return reader.parseRow(new CellConsumerFixLengthToCheckConsumer(headers.size(), values -> action.accept(new Row(headers, values))));
			} catch (IOException e) {
				return ErrorHelper.rethrow(e);
			}
		}

		@Override
		public void forEachRemaining(Consumer<? super Row> action) {
			try {
				if (headers == null) {
					headers = headers(reader);
				}
				reader.parseAll(new CellConsumerFixLengthToCheckConsumer(headers.size(), values -> action.accept(new Row(headers, values))));
			} catch (IOException e) {
				ErrorHelper.rethrow(e);
			}
		}

		@Override
		public Spliterator<Row> trySplit() {
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

    private static class CsvStringArrayIterator implements Iterator<String[]> {

        private final CsvReader reader;
        private final CellConsumer cellConsumer;

        private boolean isFetched;
        private String[] value;
        @SuppressWarnings("unchecked")
        public CsvStringArrayIterator(CsvReader csvReader) {
            cellConsumer = csvReader.toCellConsumer(new CheckedConsumer<String[]>() {
                @Override
                public void accept(String[] strings)  {
                    value = strings;
                }
            });
            reader = csvReader;
        }

        @Override
        public boolean hasNext() {
            fetch();
            return value != null;
        }

        private void fetch() {
            if (!isFetched) {
                try {
                    value = null;
                    reader.parseRow(cellConsumer);
                } catch (IOException e) {
                    ErrorHelper.rethrow(e);
                }
                isFetched = true;
            }
        }

        @Override
        public String[] next() {
            fetch();
            if (value == null) throw new NoSuchElementException();
            isFetched = false;
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

	private static class CsvRowArrayIterator implements Iterator<Row> {

		private final CsvReader reader;
		private final CellConsumerFixLengthToCheckConsumer cellConsumer;
		private Row.Headers headers;

		private Row value;
		private boolean isFetched;
		

		public CsvRowArrayIterator(CsvReader csvReader) throws IOException {
			reader = csvReader;
			headers = headers(csvReader);
			cellConsumer = new CellConsumerFixLengthToCheckConsumer(headers.size(), new org.simpleflatmapper.util.Consumer<String[]>() {
				@Override
				public void accept(String[] strings) {
					value = new Row(headers, strings);
				}
			});
		}

		@Override
		public boolean hasNext() {
			fetch();
			return value != null;
		}

		private void fetch() {
			if (!isFetched) {
				try {
					value = null;
					reader.parseRow(cellConsumer);
				} catch (IOException e) {
					ErrorHelper.rethrow(e);
				}
				isFetched = true;
			}
		}

		@Override
		public Row next() {
			fetch();
			if (value == null) throw new NoSuchElementException();
			isFetched = false;
			return value;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static Row.Headers headers(CsvReader reader) throws IOException {
		CellConsumerCapture cellConsumer = new CellConsumerCapture();
		reader.parseRow(cellConsumer);
		if (!cellConsumer.hasData()) {
			throw new EOFException("No headers");
		}
		return Row.headers(cellConsumer.values());
	} 
	
}
