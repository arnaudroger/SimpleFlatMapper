package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.CsvMapper;
import org.sfm.csv.CsvParser;
import org.sfm.csv.CsvReader;
import org.sfm.csv.parser.CellConsumer;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.RowHandler;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
//IFJAVA8_START
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END



public final class CsvMapperImpl<T> implements CsvMapper<T> {
    private static final DelayedCellSetter[] EMPTY_DELAYED_CELL_SETTERS = new DelayedCellSetter[0];

    private final DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories;
    private final CellSetter<T>[] setters;


    private final CsvColumnKey[] joinKeys;
	private final RowHandlerErrorHandler rowHandlerErrorHandlers;
    private final CsvCellHandlerFactory<T> csvCellHandlerFactory;

    private final boolean hasSetterSubProperties;
    private final boolean hasSubProperties;

	public CsvMapperImpl(CsvCellHandlerFactory<T> csvCellHandlerFactory,
                         DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories,
                         CellSetter<T>[] setters,
                         CsvColumnKey[] joinKeys,
                         RowHandlerErrorHandler rowHandlerErrorHandlers) {
		super();
		this.csvCellHandlerFactory = csvCellHandlerFactory;
		this.delayedCellSetterFactories = delayedCellSetterFactories;
		this.setters = setters;
        this.joinKeys = joinKeys;
		this.rowHandlerErrorHandlers = rowHandlerErrorHandlers;
        this.hasSetterSubProperties = hasSetterSubProperties(setters);
        this.hasSubProperties = hasSetterSubProperties || hasDelayedMarker(delayedCellSetterFactories);
	}

    private boolean hasDelayedMarker(DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories) {
        for(DelayedCellSetterFactory<T, ?> setter : delayedCellSetterFactories) {
            if (setter instanceof DelegateMarkerDelayedCellSetterFactory) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSetterSubProperties(CellSetter<T>[] setters) {
        for(CellSetter<T> setter : setters) {
            if (setter instanceof DelegateMarkerSetter) {
                return true;
            }
        }
        return false;
    }

    @Override
	public final <H extends RowHandler<? super T>> H forEach(final Reader reader, final H handler) throws IOException, MappingException {
		return forEach(CsvParser.reader(reader), handler);
	}

	@Override
	public <H extends RowHandler<? super T>> H forEach(CsvReader reader, H handle) throws IOException, MappingException {
		reader.parseAll(newCellConsumer(handle));
		return handle;
	}

	@Override
	public final <H extends RowHandler<? super T>> H forEach(final Reader reader, final H handler, final int skip) throws IOException, MappingException {
		return forEach(CsvParser.skip(skip).reader(reader), handler);
	}

	@Override
	public final <H extends RowHandler<? super T>> H forEach(final Reader reader, final H handler, final int skip, final int limit) throws IOException, MappingException {
		return forEach(CsvParser.skip(skip).reader(reader), handler, limit);
	}

	@Override
	public final <H extends RowHandler<? super T>> H forEach(CsvReader reader, H handle, int limit) throws IOException, MappingException {
		reader.parseRows(newCellConsumer(handle), limit);
		return handle;
	}

	@Override
    @Deprecated
    @SuppressWarnings("deprecation")
	public Iterator<T> iterate(Reader reader) throws IOException {
		return iterate(CsvParser.reader(reader));
	}

	@Override
    @Deprecated
	public Iterator<T> iterate(CsvReader csvReader) {
		return new CsvMapperIterator<T>(csvReader, this);
	}

	@Override
    @Deprecated
    @SuppressWarnings("deprecation")
	public Iterator<T> iterate(Reader reader, int skip) throws IOException {
		return iterate(CsvParser.skip(skip).reader(reader));
	}

	@Override
    @SuppressWarnings("deprecation")
	public Iterator<T> iterator(Reader reader) throws IOException {
		return iterate(reader);
	}

	@SuppressWarnings("deprecation")
    @Override
	public Iterator<T> iterator(CsvReader csvReader) {
		return iterate(csvReader);
	}

	@Override
    @SuppressWarnings("deprecation")
    public Iterator<T> iterator(Reader reader, int skip) throws IOException {
		return iterate(reader, skip);
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
                return ErrorHelper.rethrow(e);
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
                ErrorHelper.rethrow(e);
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

    protected CsvMapperCellConsumer newCellConsumer(final RowHandler<? super T> handler) {
        return newCellConsumer(handler, null);
    }

	protected CsvMapperCellConsumer<T> newCellConsumer(final RowHandler<? super T> handler, BreakDetector parentBreakDetector) {
        CsvMapperCellConsumer<?>[] cellHandlers = null;

        if (hasSubProperties) {
            cellHandlers = new CsvMapperCellConsumer<?>[delayedCellSetterFactories.length + setters.length];
        }
        final BreakDetector breakDetector = newBreakDetector(parentBreakDetector, delayedCellSetterFactories.length - 1);

        DelayedCellSetter<T, ?>[] outDelayedCellSetters = getDelayedCellSetters(cellHandlers, breakDetector);
        CellSetter<T>[] outSetters = getCellSetters(cellHandlers, breakDetector);

        CsvCellHandler<T> mapperSetters = csvCellHandlerFactory.newInstace(outDelayedCellSetters, outSetters);

        return new CsvMapperCellConsumer<T>(mapperSetters,
                rowHandlerErrorHandlers,
                handler,
                breakDetector, toList(cellHandlers));
	}

    @SuppressWarnings("unchecked")
    private DelayedCellSetter<T, ?>[] getDelayedCellSetters(CsvMapperCellConsumer<?>[] cellHandlers, BreakDetector breakDetector) {
        if (delayedCellSetterFactories.length == 0) {
            return EMPTY_DELAYED_CELL_SETTERS;
        } else {
            return buildDelayedCellSetters(cellHandlers, breakDetector);
        }
    }

    private DelayedCellSetter<T, ?>[] buildDelayedCellSetters(CsvMapperCellConsumer<?>[] cellHandlers, BreakDetector breakDetector) {
        DelayedCellSetter<T, ?>[] outDelayedCellSetters = new DelayedCellSetter[delayedCellSetterFactories.length];
        for(int i = delayedCellSetterFactories.length - 1; i >= 0 ; i--) {
            DelayedCellSetterFactory<T, ?> delayedCellSetterFactory = delayedCellSetterFactories[i];
            if (delayedCellSetterFactory != null) {
                outDelayedCellSetters[i] = delayedCellSetterFactory.newCellSetter(breakDetector, cellHandlers);
            }
        }
        return outDelayedCellSetters;
    }

    private Collection<CsvMapperCellConsumer<?>> toList(CsvMapperCellConsumer<?>[] cellHandlers) {
        if (cellHandlers == null) return Collections.emptyList();

        List<CsvMapperCellConsumer<?>> consumers = new ArrayList<CsvMapperCellConsumer<?>>();
        for(CsvMapperCellConsumer<?> consumer : cellHandlers) {
            if (consumer != null) {
                consumers.add(consumer);
            }
        }
        return consumers;
    }

    @SuppressWarnings("unchecked")
    private CellSetter<T>[] getCellSetters(CsvMapperCellConsumer<?>[] cellHandlers, BreakDetector breakDetector) {
        if (hasSetterSubProperties) {
            return rebuildCellSetters(cellHandlers, breakDetector);
        } else {
            return setters;
        }
    }

    private CellSetter<T>[] rebuildCellSetters(CsvMapperCellConsumer<?>[] cellHandlers, BreakDetector breakDetector) {
        CellSetter<T>[] outSetters = new CellSetter[setters.length];
        for(int i = setters.length - 1; i >= 0 ; i--) {
            if (setters[i] instanceof DelegateMarkerSetter) {
                DelegateCellSetter<T, ?> delegateCellSetter = getDelegateCellSetter((DelegateMarkerSetter)setters[i], cellHandlers, breakDetector, i);
                outSetters[i] = delegateCellSetter;
            } else {
                outSetters[i] = setters[i];
            }
        }
        return outSetters;
    }

    @SuppressWarnings("unchecked")
    private <P> DelegateCellSetter<T, P> getDelegateCellSetter(DelegateMarkerSetter marker, CsvMapperCellConsumer<?>[] cellHandlers, BreakDetector breakDetector, int i) {
        final int parent = marker.getParent();
        final int cellIndex = i + delayedCellSetterFactories.length;
        if(parent == cellIndex) {
            final DelegateCellSetter<T, P> tpDelegateCellSetter = new DelegateCellSetter<T, P>(marker, cellIndex, breakDetector);
            cellHandlers[cellIndex] = tpDelegateCellSetter.getCellConsumer();
            return tpDelegateCellSetter;
        } else {
            final CsvMapperCellConsumer<?> cellHandler = cellHandlers[parent];
            if (cellHandler==null) {
                throw new NullPointerException("No cell handler on parent " + parent);
            }
            return new DelegateCellSetter<T, P>(marker, (CsvMapperCellConsumer<P>) cellHandler, cellIndex);
        }
    }

    private BreakDetector newBreakDetector(BreakDetector parentBreakDetector, int delayedSetterEnd) {
        if (parentBreakDetector != null || joinKeys.length > 0) {
            return new BreakDetector(joinKeys, parentBreakDetector, delayedSetterEnd);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "CsvMapperImpl{" +
                "targetSettersFactory=" + csvCellHandlerFactory +
                ", delayedCellSetters=" + Arrays.toString(delayedCellSetterFactories) +
                ", setters=" + Arrays.toString(setters) +
                '}';
    }
}
