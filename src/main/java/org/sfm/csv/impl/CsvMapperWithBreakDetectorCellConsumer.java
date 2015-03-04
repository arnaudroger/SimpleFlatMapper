package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.parser.CellConsumer;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

public final class CsvMapperWithBreakDetectorCellConsumer<T> implements IndexedCellConsumer {

	/**
	 * mapping information
	 */
	private final Instantiator<DelayedCellSetter<T, ?>[], T> instantiator;
	private final DelayedCellSetter<T, ?>[] delayedCellSetters;
	private final CellSetter<T>[] setters;
	private final CsvColumnKey[] columns;

	/**
	 * error handling
	 */
	private final FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler;
	private final RowHandlerErrorHandler rowHandlerErrorHandlers;



	private final RowHandler<? super T> handler;

	/**
	 * parsing information
	 */
	private final ParsingContext parsingContext;

	private final int flushIndex;
	private final int totalLength;


	private T currentInstance;
	private int cellIndex = 0;
    private boolean hasData;


    private final BreakDetector breakDetector;

    public CsvMapperWithBreakDetectorCellConsumer(
            Instantiator<DelayedCellSetter<T, ?>[], T> instantiator,
            DelayedCellSetter<T, ?>[] delayedCellSetters,
            CellSetter<T>[] setters,
            CsvColumnKey[] columns,
            FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler,
            RowHandlerErrorHandler rowHandlerErrorHandlers,
            RowHandler<? super T> handler,
            ParsingContext parsingContext, BreakDetector breakDetector) {
		super();
		this.instantiator = instantiator;
		this.delayedCellSetters = delayedCellSetters;
		this.setters = setters;
		this.columns = columns;
		this.fieldErrorHandler = fieldErrorHandler;
		this.rowHandlerErrorHandlers = rowHandlerErrorHandlers;
		this.handler = handler;
        this.breakDetector = breakDetector;
        this.flushIndex = lastNonNullSetter(delayedCellSetters, setters);
		this.totalLength = delayedCellSetters.length + setters.length;
		this.parsingContext = parsingContext;
	}

    @Override
    public void end() {
        endOfRow();
        if (currentInstance != null) {
            callHandler();
        }
    }

    @Override
    public void newCell(char[] chars, int offset, int length) {
        if (cellIndex == -1) {
            return;
        }
        newCell(chars, offset, length, cellIndex);
        if (cellIndex != -1) {
            cellIndex++;
        }
    }
	
	@Override
	public void endOfRow() {
		flush();
		cellIndex = 0;
	}
	
    public void newCell(char[] chars, int offset, int length, int cellIndex) {
        hasData = true;
        if (cellIndex < delayedCellSetters.length) {
            newCellForDelayedSetter(chars, offset, length, cellIndex);
        } else {
            createInstanceIfNeeded();
            newCellForSetter(chars, offset, length, cellIndex);
        }

        if (cellIndex == flushIndex) {
            flush();
        }
    }

	private void newCellForDelayedSetter(char[] chars, int offset, int length, int cellIndex) {
		try {
			DelayedCellSetter<T, ?> delayedCellSetter = delayedCellSetters[cellIndex];
			if (delayedCellSetter != null) {
				delayedCellSetter.set(chars, offset, length, parsingContext);
			}
		} catch (Exception e) {
			fieldErrorHandler.errorMappingField(getColumn(cellIndex), this, currentInstance, e);
		}
	}

	private void newCellForSetter(char[] chars, int offset, int length, int cellIndex) {
		if (cellIndex < totalLength) {
			try {
				CellSetter<T> cellSetter = setters[cellIndex - delayedCellSetters.length];
				if (cellSetter != null) {
					cellSetter.set(currentInstance, chars, offset, length, parsingContext);
				}
			} catch (Exception e) {
				fieldErrorHandler.errorMappingField(getColumn(cellIndex), this, currentInstance, e);
			}
		}
	}

    private void flush() {
        if (hasData) {
            createInstanceIfNeeded();
            this.cellIndex = -1;
            hasData = false;
        }
    }

    private void callHandler() {
        try {
            handler.handle(currentInstance);
            currentInstance = null;
        } catch (Exception e) {
            rowHandlerErrorHandlers.handlerError(e, currentInstance);
        }
    }

    private void applyDelayedSetters() {
        for(int i = 0; i < delayedCellSetters.length; i++) {
            DelayedCellSetter<T, ?> delayedSetter = delayedCellSetters[i];
            if (delayedSetter != null && delayedSetter.isSettable()) {
                applyDelaySetter(i, delayedSetter);
            }
        }
    }

    private void applyDelaySetter(int i, DelayedCellSetter<T, ?> delayedSetter) {
        try {
            delayedSetter.set(currentInstance);
        } catch (Exception e) {
            fieldErrorHandler.errorMappingField(getColumn(cellIndex), this, currentInstance, e);
        }
    }

    private CsvColumnKey getColumn(int cellIndex) {
        for(CsvColumnKey key : columns) {
            if (key.getIndex() == cellIndex) {
                return key;
            }
        }
        return null;
    }

    private void createInstanceIfNeeded() {
        if (breakDetector.isBroken(delayedCellSetters)) {
            createInstance();
        }
    }



    private void createInstance() {
        try {
            if(currentInstance!= null) {
                callHandler();
            }
            currentInstance = instantiator.newInstance(delayedCellSetters);
            applyDelayedSetters();
        } catch (Exception e) {
            throw new MappingException(e.getMessage(), e);
        }
    }

    private static int lastNonNullSetter(
            DelayedCellSetter<?, ?>[] dcs,
            CellSetter<?>[] cs) {
        int lastNonNull = -1;

        for(int i = 0; i < dcs.length; i++) {
            if (dcs[i] != null) {
                lastNonNull = i;
            }
        }

        for(int i = 0; i < cs.length; i++) {
            if (cs[i] != null) {
                lastNonNull = i + dcs.length;
            }
        }

        return lastNonNull;
    }

}
