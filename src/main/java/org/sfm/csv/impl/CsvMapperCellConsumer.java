package org.sfm.csv.impl;

import org.sfm.csv.parser.CellConsumer;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.utils.RowHandler;

import java.util.Collection;

public final class CsvMapperCellConsumer<T> implements CellConsumer {


    private final CsvCellHandler<T> mapperSetters;



    protected final RowHandlerErrorHandler rowHandlerErrorHandlers;

    protected final BreakDetector breakDetector;

    protected final RowHandler<? super T> handler;

    protected final CsvMapperCellConsumer[] children;

    protected int cellIndex = 0;

    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public CsvMapperCellConsumer(
            CsvCellHandler<T> csvCellHandler,
            RowHandlerErrorHandler rowHandlerErrorHandlers,
            RowHandler<? super T> handler,
            BreakDetector breakDetector, Collection<CsvMapperCellConsumer<?>> children) {
        super();
        this.mapperSetters = csvCellHandler;
        this.rowHandlerErrorHandlers = rowHandlerErrorHandlers;
        this.handler = handler;
        this.breakDetector = breakDetector;
        this.children = children.toArray(new CsvMapperCellConsumer[0]);
    }

    @Override
    public final void endOfRow() {
        composeInstance();
        resetConsumer();
    }

    public final T getCurrentInstance() {
        return mapperSetters.getCurrentInstance();
    }


    protected final boolean hasData() {
        return cellIndex > 0;
    }


    protected final void callHandler() {
        if (handler == null) return;
        try {
            handler.handle(getCurrentInstance());
        } catch (Exception e) {
            rowHandlerErrorHandlers.handlerError(e, getCurrentInstance());
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
            mapperSetters.newCellForDelayedSetter(chars, offset, length, cellIndex);
        } else if (isNotNull()) {
            mapperSetters.newCellForSetter(chars, offset, length, cellIndex);
        }
        this.cellIndex = cellIndex + 1;
    }

    private boolean isNotNull() {
        if (breakDetector == null) {
            mapperSetters.createInstanceIfNull();
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
            mapperSetters.resetCurrentInstance();
        }
        cellIndex = 0;
    }

    private void composeInstance() {
        if (hasData()) {
            if (breakDetector == null || breakDetector.isNotNull()) {
                for (CsvMapperCellConsumer<?> child : children) {
                    child.composeInstance();
                }

                mapperSetters.createInstanceIfNull();

                mapperSetters.applyDelayedSetters();

                if (breakDetector == null) {
                    callHandler();
                }
            }
        }
    }


    protected void afterEnd() {
        if (breakDetector != null && mapperSetters.hasInstance()) {
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
                if (mapperSetters.hasInstance()) {
                    callHandler();
                    mapperSetters.resetCurrentInstance();
                }

                updateChildrenStatus(cellIndex);

                if (breakDetector.isNotNull()) {
                    mapperSetters.createInstance();
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
