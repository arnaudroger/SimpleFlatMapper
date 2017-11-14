package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.csv.parser.CellConsumer;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.util.CheckedConsumer;

import java.util.Collection;

public final class CsvMapperCellConsumer<T> implements CellConsumer {

    private final CsvMapperCellHandler<T> mapperSetters;

    protected final ConsumerErrorHandler consumerErrorHandlers;

    protected final BreakDetector breakDetector;

    protected final CheckedConsumer<? super T> handler;

    protected final CsvMapperCellConsumer[] children;
    
    private final int maxMandatoryCellIndex;

    protected int cellIndex = 0;

    private boolean calledHandler = false;
    private boolean producedObject = false;

    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public CsvMapperCellConsumer(
            CsvMapperCellHandler<T> csvMapperCellHandler,
            ConsumerErrorHandler consumerErrorHandlers,
            CheckedConsumer<? super T> handler,
            BreakDetector breakDetector, 
            Collection<CsvMapperCellConsumer<?>> children, 
            int maxMandatoryCellIndex) {
        super();
        this.mapperSetters = csvMapperCellHandler;
        this.consumerErrorHandlers = consumerErrorHandlers;
        this.handler = handler;
        this.breakDetector = breakDetector;
        this.children = children.toArray(new CsvMapperCellConsumer[0]);
        this.maxMandatoryCellIndex = maxMandatoryCellIndex;
    }

    @Override
    public final boolean endOfRow() {
        composeInstance();
        resetConsumer();
        boolean calledHandler = this.calledHandler;
        this.calledHandler = false;
        return calledHandler;
    }

    public T getOrCreateCurrentInstance() {
        T t = getCurrentInstance();
        if (t == null) {
            composeInstance();
        }
        t = getCurrentInstance();
        return t;
    }

    public final T getCurrentInstance() {
        return mapperSetters.getCurrentInstance();
    }


    protected final boolean hasData() {
        return cellIndex > maxMandatoryCellIndex;
    }


    protected final void callHandler() {
        calledHandler = true;
        if (handler == null) return;
        try {
            handler.accept(getCurrentInstance());
        } catch (Exception e) {
            consumerErrorHandlers.handlerError(e, getCurrentInstance());
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
            mapperSetters.delayedCellValue(chars, offset, length, cellIndex);
        } else if (isNotNull()) {
            mapperSetters.cellValue(chars, offset, length, cellIndex);
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
        if (breakDetector == null || breakDetector.updateStatus(mapperSetters, cellIndex)) {
            if (breakDetector == null || breakDetector.broken()) {
                if (mapperSetters.hasInstance()) {
                    callHandler();
                    mapperSetters.resetCurrentInstance();
                }

                updateChildrenStatus(cellIndex);

                if (breakDetector == null || breakDetector.isNotNull()) {
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
