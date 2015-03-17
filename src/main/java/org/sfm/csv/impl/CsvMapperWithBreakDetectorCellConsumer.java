package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.RowHandler;

import java.util.Collection;

public final class CsvMapperWithBreakDetectorCellConsumer<T> implements CsvMapperCellConsumer {

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

	private final int totalLength;
    private final CsvMapperWithBreakDetectorCellConsumer[] children;


    private T currentInstance;
	private int cellIndex = 0;

    private int rowNumber = 0;

    private final BreakDetector breakDetector;

    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public CsvMapperWithBreakDetectorCellConsumer(
            Instantiator<DelayedCellSetter<T, ?>[], T> instantiator,
            DelayedCellSetter<T, ?>[] delayedCellSetters,
            CellSetter<T>[] setters,
            CsvColumnKey[] columns,
            FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler,
            RowHandlerErrorHandler rowHandlerErrorHandlers,
            RowHandler<? super T> handler,
            ParsingContext parsingContext, BreakDetector breakDetector, Collection<CsvMapperCellConsumer<?>> children) {
		super();
		this.instantiator = instantiator;
		this.delayedCellSetters = delayedCellSetters;
		this.setters = setters;
		this.columns = columns;
		this.fieldErrorHandler = fieldErrorHandler;
		this.rowHandlerErrorHandlers = rowHandlerErrorHandlers;
		this.handler = handler;
        this.breakDetector = breakDetector;
		this.totalLength = delayedCellSetters.length + setters.length;
		this.parsingContext = parsingContext;
        this.children = children.toArray(new CsvMapperWithBreakDetectorCellConsumer[0]);
	}


    @Override
    public void newCell(char[] chars, int offset, int length) {
        newCell(chars, offset, length, cellIndex);
        updateStatus(cellIndex - 1);
    }

    private void updateStatus(int cellIndex) {
        if (breakDetector.updateStatus(delayedCellSetters, cellIndex)) {
            if (breakDetector.broken()) {
                if (currentInstance != null) {
                    callHandler();
                    currentInstance = null;
                }

                for(CsvMapperWithBreakDetectorCellConsumer consumer : children) {
                    consumer.updateStatus(cellIndex);
                }

                if (breakDetector.isNotNull()) {
                    // force flush
                    //preFlushChildren();
                    currentInstance = createInstance();
                }
                return;
            }
        }

        for(CsvMapperWithBreakDetectorCellConsumer consumer : children) {
            consumer.updateStatus(cellIndex);
        }

    }

    @Override
    public void endOfRow() {

        composeInstance();

        resetConsumer();


    }

    private void resetConsumer() {
        for (CsvMapperWithBreakDetectorCellConsumer<?> child : children) {
            child.resetConsumer();
        }
        cellIndex = 0;
        breakDetector.reset();
    }

    private void composeInstance() {

        if (hasData() && breakDetector.isNotNull()) {
            for (CsvMapperWithBreakDetectorCellConsumer<?> child : children) {
                child.composeInstance();
            }

            if (currentInstance == null) {
                currentInstance = createInstance();
            }
            applyDelayedSetters();
        }
    }

    private boolean hasData() {
        return cellIndex > 0;
    }


    private void callHandler() {
        if (handler == null) return;
        try {
            handler.handle(currentInstance);
        } catch (Exception e) {
            rowHandlerErrorHandlers.handlerError(e, currentInstance);
        }
    }

    private void applyDelayedSetters() {
        for(int i = 0; i < delayedCellSetters.length; i++) {
            DelayedCellSetter<T, ?> delayedSetter = delayedCellSetters[i];
            if (delayedSetter != null && delayedSetter.isSettable()) {
                try {
                    delayedSetter.set(currentInstance);
                } catch (Exception e) {
                    fieldErrorHandler.errorMappingField(getColumn(i), this, currentInstance, e);
                }
            }
        }
    }

    private T createInstance() {
        try {
            return instantiator.newInstance(delayedCellSetters);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    private void newCellForDelayedSetter(char[] chars, int offset, int length, int cellIndex) {
        try {
            DelayedCellSetter<T, ?> delayedCellSetter = delayedCellSetters[cellIndex];
            if (delayedCellSetter != null) {
                delayedCellSetter.set(chars, offset, length, parsingContext);
            }
        } catch (Exception e) {
            fieldErrorHandler.errorMappingField(columns[cellIndex], this, currentInstance, e);
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
                fieldErrorHandler.errorMappingField(columns[cellIndex], this, currentInstance, e);
            }
        }
    }

    @Override
    public void end() {
        endOfRow();
        if (currentInstance != null) {
            callHandler();
        }
    }

    public void newCell(char[] chars, int offset, int length, int cellIndex) {
        if (cellIndex < delayedCellSetters.length) {
            newCellForDelayedSetter(chars, offset, length, cellIndex);
        } else if (breakDetector.isNotNull()) {
            newCellForSetter(chars, offset, length, cellIndex);
        }
        this.cellIndex = cellIndex + 1;
    }

    @Override
    public BreakDetector getBreakDetector() {
        return breakDetector;
    }

    @Override
    public T getCurrentInstance() {
        return currentInstance;
    }

    private CsvColumnKey getColumn(int cellIndex) {
        for(CsvColumnKey key : columns) {
            if (key.getIndex() == cellIndex) {
                return key;
            }
        }
        return null;
    }

}
