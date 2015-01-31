package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.CsvMapper;
import org.sfm.csv.CsvParser;
import org.sfm.csv.CsvReader;
import org.sfm.csv.parser.CellConsumer;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//IFJAVA8_START
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END



public final class CsvMapperImpl<T> implements CsvMapper<T> {
	private final DelayedCellSetterFactory<T, ?>[] delayedCellSetters;
	private final CellSetter<T>[] setters;
	private final CsvColumnKey[] keys;
	
	private final Instantiator<DelayedCellSetter<T, ?>[], T> instantiator;
	private final FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler;
	private final RowHandlerErrorHandler rowHandlerErrorHandlers;
	private final ParsingContextFactory parsingContextFactory;
	
	public CsvMapperImpl(Instantiator<DelayedCellSetter<T, ?>[], T> instantiator,
			DelayedCellSetterFactory<T, ?>[] delayedCellSetters,
			CellSetter<T>[] setters,
			CsvColumnKey[] keys,
			ParsingContextFactory parsingContextFactory,
			FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler,
			RowHandlerErrorHandler rowHandlerErrorHandlers) {
		super();
		this.instantiator = instantiator;
		this.delayedCellSetters = delayedCellSetters;
		this.setters = setters;
		this.keys = keys;
		this.fieldErrorHandler = fieldErrorHandler;
		this.rowHandlerErrorHandlers = rowHandlerErrorHandlers;
		this.parsingContextFactory = parsingContextFactory;
	}

	@Override
	public final <H extends RowHandler<T>> H forEach(final Reader reader, final H handler) throws IOException, MappingException {
		return forEach(CsvParser.reader(reader), handler);
	}

	@Override
	public <H extends RowHandler<T>> H forEach(CsvReader reader, H handle) throws IOException, MappingException {
		reader.parseAll(newCellConsumer(handle));
		return handle;
	}

	@Override
	public final <H extends RowHandler<T>> H forEach(final Reader reader, final H handler, final int skip) throws IOException, MappingException {
		return forEach(CsvParser.skip(skip).reader(reader), handler);
	}

	@Override
	public final <H extends RowHandler<T>> H forEach(final Reader reader, final H handler, final int skip, final int limit) throws IOException, MappingException {
		return forEach(CsvParser.skip(skip).reader(reader), handler, limit);
	}

	@Override
	public <H extends RowHandler<T>> H forEach(CsvReader reader, H handle, int limit) throws IOException, MappingException {
		reader.parseRows(newCellConsumer(handle), limit);
		return handle;
	}

	@Override
	public Iterator<T> iterate(Reader reader) throws IOException {
		return iterate(CsvParser.reader(reader));
	}

	@Override
	public Iterator<T> iterate(CsvReader csvReader) {
		return new CsvMapperIterator<T>(csvReader, this);
	}

	@Override
	public Iterator<T> iterate(Reader reader, int skip) throws IOException {
		return iterate(CsvParser.skip(skip).reader(reader));
	}

	//IFJAVA8_START
	@Override
	public Stream<T> stream(Reader reader) throws IOException {
		return stream(CsvParser.reader(reader));
	}

	@Override
	public Stream<T> stream(CsvReader csvReader) {
		return StreamSupport.stream(new CsvSpliterator(csvReader), false);
	}

	@Override
	public Stream<T> stream(Reader reader, int skip) throws IOException {
		return stream(CsvParser.skip(skip).reader(reader));
	}

	public class CsvSpliterator implements Spliterator<T> {
		private final CsvReader csvReader;
		private final CellConsumer cellConsumer;
		private T current;

		public CsvSpliterator(CsvReader csvReader) {
			this.csvReader = csvReader;
			this.cellConsumer = newCellConsumer(new RowHandler<T>() {
				@Override
				public void handle(T t) throws Exception {
					current = t;
				}
			});
		}

		@Override
		public boolean tryAdvance(Consumer<? super T> action) {
			current = null;
			try {
				csvReader.parseRow(cellConsumer);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (current != null) {
				action.accept(current);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void forEachRemaining(Consumer<? super T> action) {
			try {
				csvReader.parseAll(newCellConsumer(new RowHandler<T>() {
                    @Override
                    public void handle(T t) throws Exception {
						action.accept(t);
                    }
                }));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Spliterator<T> trySplit() {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected CsvMapperCellConsumer<T> newCellConsumer(final RowHandler<T> handler) {

		DelayedCellSetter<T, ?>[] outDelayedCellSetters = new DelayedCellSetter[delayedCellSetters.length];
		Map<CsvMapper<?>, CsvMapperCellConsumer<?>> cellHandlers = new HashMap<CsvMapper<?>, CsvMapperCellConsumer<?>>();


		for(int i = 0; i < delayedCellSetters.length; i++) {
			DelayedCellSetterFactory<T, ?> delayedCellSetterFactory = delayedCellSetters[i];
			if (delayedCellSetterFactory != null) {
				if (delayedCellSetterFactory instanceof DelegateMarkerDelayedCellSetter) {
					DelegateMarkerDelayedCellSetter<T, ?> marker = (DelegateMarkerDelayedCellSetter<T, ?>) delayedCellSetterFactory;

					CsvMapperCellConsumer<?> cellHandler = cellHandlers.get(marker.getMapper());

					DelegateDelayedCellSetterFactory<T, ?> delegateCellSetter;

					if(cellHandler == null) {
						delegateCellSetter = new DelegateDelayedCellSetterFactory(marker, i);
						cellHandlers.put(marker.getMapper(), delegateCellSetter.getCellHandler());
					} else {
						delegateCellSetter = new DelegateDelayedCellSetterFactory(marker, cellHandler, i);
					}
					outDelayedCellSetters[i] =  delegateCellSetter.newCellSetter();
				} else {
					outDelayedCellSetters[i] = delayedCellSetterFactory.newCellSetter();
				}
			}
		}


		CellSetter<T>[] outSetters = new CellSetter[setters.length];
		for(int i = 0; i < setters.length; i++) {
			if (setters[i] instanceof DelegateMarkerSetter) {
				DelegateMarkerSetter<?> marker = (DelegateMarkerSetter<?>) setters[i];

				CsvMapperCellConsumer<?> cellHandler = cellHandlers.get(marker.getMapper());

				DelegateCellSetter<T> delegateCellSetter;

				if(cellHandler == null) {
					delegateCellSetter = new DelegateCellSetter<T>(marker, i + delayedCellSetters.length);
					cellHandlers.put(marker.getMapper(), delegateCellSetter.getBytesCellHandler());
				} else {
					delegateCellSetter = new DelegateCellSetter<T>(marker, cellHandler, i + delayedCellSetters.length);
				}
				outSetters[i] = delegateCellSetter;
			} else {
				outSetters[i] = setters[i];
			}
		}

		return new CsvMapperCellConsumer<T>(instantiator,
				outDelayedCellSetters,
				outSetters, keys,
				fieldErrorHandler,
				rowHandlerErrorHandlers,
				handler,
				parsingContextFactory.newContext());
	}



}
