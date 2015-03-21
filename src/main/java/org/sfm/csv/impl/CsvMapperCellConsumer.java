package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.parser.CellConsumer;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.utils.ForEachIndexedCallBack;
import org.sfm.utils.RowHandler;

import java.util.Collection;

public final class CsvMapperCellConsumer<T> implements CellConsumer {


    private final AbstractTargetSetters<T> mapperSetters;

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


    protected final CsvMapperCellConsumer[] children;
    protected T currentInstance;

    protected int cellIndex = 0;
    private final ForEachIndexedCallBack<DelayedCellSetter<T, ?>> delayedCellSetterForEachIndexedCallBack = new ForEachIndexedCallBack<DelayedCellSetter<T, ?>>() {
        @Override
        public void handle(DelayedCellSetter<T, ?> delayedCellSetter, int index) {
            try {
                delayedCellSetter.set(currentInstance);
            } catch (Exception e) {
                fieldErrorHandler.errorMappingField(mapperSetters.getColumn(index), this, currentInstance, e);
            }
        }
    };

    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public CsvMapperCellConsumer(
            AbstractTargetSetters<T> targetSetters,
            FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler,
            RowHandlerErrorHandler rowHandlerErrorHandlers,
            RowHandler<? super T> handler,
            ParsingContext parsingContext, BreakDetector breakDetector, Collection<CsvMapperCellConsumer<?>> children) {
        super();
        this.mapperSetters = targetSetters;
        this.fieldErrorHandler = fieldErrorHandler;
        this.rowHandlerErrorHandlers = rowHandlerErrorHandlers;
        this.handler = handler;
        this.breakDetector = breakDetector;
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


    private void newCellForDelayedSetter(char[] chars, int offset, int length, int cellIndex) {
        try {
            DelayedCellSetter<T, ?> delayedCellSetter = mapperSetters.getDelayedCellSetter(cellIndex);
            if (delayedCellSetter != null) {
                delayedCellSetter.set(chars, offset, length, parsingContext);
            }
        } catch (Exception e) {
            fieldErrorHandler.errorMappingField(mapperSetters.getColumn(cellIndex), this, currentInstance, e);
        }
    }

    private void newCellForSetter(char[] chars, int offset, int length, int cellIndex) {
            try {
                CellSetter<T> cellSetter = mapperSetters.getCellSetter(cellIndex);
                if (cellSetter != null) {
                    cellSetter.set(currentInstance, chars, offset, length, parsingContext);
                }
            } catch (Exception e) {
                fieldErrorHandler.errorMappingField(mapperSetters.getColumn(cellIndex), this, currentInstance, e);
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
        if (mapperSetters.isDelayedSetter(cellIndex)) {
            newCellForDelayedSetter(chars, offset, length, cellIndex);
        } else if (isNotNull()) {
            newCellForSetter(chars, offset, length, cellIndex);
        }
        this.cellIndex = cellIndex + 1;
    }

    private boolean isNotNull() {
        if (breakDetector == null) {
            if (currentInstance == null) {
                currentInstance = mapperSetters.createInstance();
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
                    currentInstance = mapperSetters.createInstance();
                }


                mapperSetters.forEachSettableDelayedSetters(delayedCellSetterForEachIndexedCallBack);

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

        if (breakDetector.updateStatus(mapperSetters, cellIndex)) {
            if (breakDetector.broken()) {
                if (currentInstance != null) {
                    callHandler();
                    currentInstance = null;
                }

                updateChildrenStatus(cellIndex);

                if (breakDetector.isNotNull()) {
                    // force flush
                    //preFlushChildren();
                    currentInstance = mapperSetters.createInstance();
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
