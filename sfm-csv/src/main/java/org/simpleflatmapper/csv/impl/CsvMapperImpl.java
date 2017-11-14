package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.csv.CsvReader;
import org.simpleflatmapper.csv.mapper.*;
import org.simpleflatmapper.csv.parser.CellConsumer;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.CheckedConsumer;

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
	private final ConsumerErrorHandler consumerErrorHandlers;
    private final CsvMapperCellHandlerFactory<T> csvMapperCellHandlerFactory;

    private final boolean hasSetterSubProperties;
    private final boolean hasSubProperties;
    private final int maxMandatoryCellIndex;

    public CsvMapperImpl(CsvMapperCellHandlerFactory<T> csvMapperCellHandlerFactory,
                         DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories,
                         CellSetter<T>[] setters,
                         CsvColumnKey[] joinKeys,
                         ConsumerErrorHandler consumerErrorHandlers, 
                         int maxMandatoryCellIndex) {
		super();
		this.csvMapperCellHandlerFactory = csvMapperCellHandlerFactory;
		this.delayedCellSetterFactories = delayedCellSetterFactories;
		this.setters = setters;
        this.joinKeys = joinKeys;
		this.consumerErrorHandlers = consumerErrorHandlers;
        this.hasSetterSubProperties = hasSetterSubProperties(setters);
        this.hasSubProperties = hasSetterSubProperties || hasDelayedMarker(delayedCellSetterFactories);
        this.maxMandatoryCellIndex = maxMandatoryCellIndex;
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
	public final <H extends CheckedConsumer<? super T>> H forEach(final Reader reader, final H handler) throws IOException, MappingException {
		return forEach(CsvParser.reader(reader), handler);
	}

	@Override
	public <H extends CheckedConsumer<? super T>> H forEach(CsvReader reader, H handle) throws IOException, MappingException {
		reader.parseAll(newCellConsumer(handle));
		return handle;
	}

	@Override
	public final <H extends CheckedConsumer<? super T>> H forEach(final Reader reader, final H handler, final int skip) throws IOException, MappingException {
		return forEach(CsvParser.skip(skip).reader(reader), handler);
	}

	@Override
	public final <H extends CheckedConsumer<? super T>> H forEach(final Reader reader, final H handler, final int skip, final int limit) throws IOException, MappingException {
		return forEach(CsvParser.skip(skip).reader(reader), handler, limit);
	}

	@Override
	public final <H extends CheckedConsumer<? super T>> H forEach(CsvReader reader, H handle, int limit) throws IOException, MappingException {
		reader.parseRows(newCellConsumer(handle), limit);
		return handle;
	}

	@Override
	public Iterator<T> iterator(Reader reader) throws IOException {
		return iterator(CsvParser.reader(reader));
	}

	@Override
	public Iterator<T> iterator(CsvReader csvReader) {
		return new CsvMapperIterator<T>(csvReader, this);
	}

	@Override
	public Iterator<T> iterator(Reader reader, int skip) throws IOException {
		return iterator(CsvParser.skip(skip).reader(reader));
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
			this.cellConsumer = newCellConsumer(new CheckedConsumer<T>() {
				@Override
				public void accept(T t) throws Exception {
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
				csvReader.parseAll(newCellConsumer(new CheckedConsumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
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

    protected CsvMapperCellConsumer newCellConsumer(final CheckedConsumer<? super T> handler) {
        return newCellConsumer(handler, null, false);
    }

	protected CsvMapperCellConsumer<T> newCellConsumer(final CheckedConsumer<? super T> handler, BreakDetector parentBreakDetector, boolean appendCollection) {
        CsvMapperCellConsumer<?>[] cellHandlers = null;

        if (hasSubProperties) {
            cellHandlers = new CsvMapperCellConsumer<?>[delayedCellSetterFactories.length + setters.length];
        }

        BreakDetector breakDetector = null;

        // check is need to skip breakdetector if no key and append to collection
        if ((joinKeys != null && joinKeys.length > 0) || !appendCollection) {
            breakDetector = newBreakDetector(parentBreakDetector, delayedCellSetterFactories.length - 1);
        }

        DelayedCellSetter<T, ?>[] outDelayedCellSetters = getDelayedCellSetters(cellHandlers, breakDetector);
        CellSetter<T>[] outSetters = getCellSetters(cellHandlers, breakDetector);

        CsvMapperCellHandler<T> mapperSetters = csvMapperCellHandlerFactory.newInstance(outDelayedCellSetters, outSetters);

        return new CsvMapperCellConsumer<T>(mapperSetters,
                consumerErrorHandlers,
                handler,
                breakDetector, toList(cellHandlers), maxMandatoryCellIndex);
	}

    @SuppressWarnings("unchecked")
    private DelayedCellSetter<T, ?>[] getDelayedCellSetters(CsvMapperCellConsumer<?>[] cellHandlers, BreakDetector breakDetector) {
        if (delayedCellSetterFactories.length == 0) {
            return EMPTY_DELAYED_CELL_SETTERS;
        } else {
            return buildDelayedCellSetters(cellHandlers, breakDetector);
        }
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
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

    public CsvMapperCellHandlerFactory<T>  csvMapperCellHandlerFactory() {
        return csvMapperCellHandlerFactory;
    }


    @Override
    public String toString() {
        return "CsvMapperImpl{" +
                "targetSettersFactory=" + csvMapperCellHandlerFactory +
                ", delayedCellSetters=" + Arrays.toString(delayedCellSetterFactories) +
                ", setters=" + Arrays.toString(setters) +
                '}';
    }
}
