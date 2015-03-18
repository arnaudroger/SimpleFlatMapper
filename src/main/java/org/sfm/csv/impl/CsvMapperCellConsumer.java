package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.parser.CellConsumer;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.RowHandler;

import java.util.Collection;

public final class CsvMapperCellConsumer<T> implements CellConsumer {

    /**
     * mapping information
     */
    protected final Instantiator<DelayedCellSetter<T, ?>[], T> instantiator;
    protected final DelayedCellSetter<T, ?>[] delayedCellSetters;
    protected final CellSetter<T>[] setters;
    protected final CsvColumnKey[] columns;


    /**
     * error handling
     */
    protected final FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler;
    protected final RowHandlerErrorHandler rowHandlerErrorHandlers;


    protected final BreakDetector breakDetector;

    protected final RowHandler<? super T> handler;

    /**
     * parsing information
     */
    protected final ParsingContext parsingContext;
    protected final int totalLength;


    protected final CsvMapperCellConsumer[] children;
    protected T currentInstance;

    protected int cellIndex = 0;

    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public CsvMapperCellConsumer(
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
        this.children = children.toArray(new CsvMapperCellConsumer[0]);
    }

    @Override
    public final void endOfRow() {
        composeInstance();
        resetConsumer();
    }

    public final T getCurrentInstance() {
        return currentInstance;
    }

    protected final CsvColumnKey getColumn(int cellIndex) {
        for (CsvColumnKey key : columns) {
            if (key.getIndex() == cellIndex) {
                return key;
            }
        }
        return null;
    }

    protected final boolean hasData() {
        return cellIndex > 0;
    }


    protected final void callHandler() {
        if (handler == null) return;
        try {
            handler.handle(currentInstance);
        } catch (Exception e) {
            rowHandlerErrorHandlers.handlerError(e, currentInstance);
        }
    }

    private void applyDelayedSetters() {
        for (int i = 0; i < delayedCellSetters.length; i++) {
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

    protected final T createInstance() {
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

    @Override
    public final void end() {
        endOfRow();
        afterEnd();
    }


    @Override
    public final void newCell(char[] chars, int offset, int length) {
        int index = cellIndex;
        newCell(chars, offset, length, index);
        afterNewCell(index);
    }

    public final void newCell(char[] chars, int offset, int length, int cellIndex) {
        if (cellIndex < delayedCellSetters.length) {
            newCellForDelayedSetter(chars, offset, length, cellIndex);
        } else if (isNotNull()) {
            newCellForSetter(chars, offset, length, cellIndex);
        }
        this.cellIndex = cellIndex + 1;
    }

    private boolean isNotNull() {
        if (breakDetector == null) {
            if (currentInstance == null) {
                currentInstance = createInstance();
            }
            return true;
        } else {
            return breakDetector.isNotNull();
        }
    }

    public final BreakDetector getBreakDetector() {
        return breakDetector;
    }


    private void resetConsumer() {
        for (CsvMapperCellConsumer<?> child : children) {
            child.resetConsumer();
        }
        if (breakDetector != null) {
            breakDetector.reset();
        } else {
            currentInstance = null;
        }
        cellIndex = 0;
    }

    private void composeInstance() {
        if (hasData()) {
            if (breakDetector == null || breakDetector.isNotNull()) {
                for (CsvMapperCellConsumer<?> child : children) {
                    child.composeInstance();
                }

                if (currentInstance == null) {
                    currentInstance = createInstance();
                }
                applyDelayedSetters();
                if (breakDetector == null) {
                    callHandler();
                }
            }
        }
    }


    protected void afterEnd() {
        if (breakDetector != null && currentInstance != null) {
            callHandler();
        }
    }

    protected void afterNewCell(int index) {
        if (breakDetector == null) return;
        updateBreakStatus(index);
    }


    private void updateBreakStatus(int cellIndex) {

        if (breakDetector.updateStatus(delayedCellSetters, cellIndex)) {
            if (breakDetector.broken()) {
                if (currentInstance != null) {
                    callHandler();
                    currentInstance = null;
                }

                updateChildrenStatus(cellIndex);

                if (breakDetector.isNotNull()) {
                    // force flush
                    //preFlushChildren();
                    currentInstance = createInstance();
                }
                return;
            }
        }

        updateChildrenStatus(cellIndex);
    }

    private void updateChildrenStatus(int cellIndex) {
        for (CsvMapperCellConsumer consumer : children) {
            consumer.updateBreakStatus(cellIndex);
        }
    }
}
