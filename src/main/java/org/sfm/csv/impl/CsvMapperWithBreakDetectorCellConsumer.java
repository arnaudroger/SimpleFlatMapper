package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
            ParsingContext parsingContext, BreakDetector breakDetector, Collection<CsvMapperCellConsumer> children) {
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
    public void end() {
        endOfRow();
        callHandler();
    }

    @Override
    public void newCell(char[] chars, int offset, int length) {
        newCell(chars, offset, length, cellIndex);
        updateStatus(cellIndex);
        cellIndex++;
    }

    private void updateStatus(int cellIndex) {
        if (breakDetector.updateStatus(delayedCellSetters, cellIndex)) {
            createInstanceIfNeeded();
        }
        for(CsvMapperWithBreakDetectorCellConsumer consumer : children) {
            consumer.updateStatus(cellIndex);
        }
    }

    @Override
	public void endOfRow() {
        for(CsvMapperCellConsumer child : children) {
            child.endOfRow();
        }

        breakDetector.reset();
        cellIndex = 0;
	}
	
    public void newCell(char[] chars, int offset, int length, int cellIndex) {

        if (cellIndex < delayedCellSetters.length) {
            newCellForDelayedSetter(chars, offset, length, cellIndex);
        } else {
            newCellForSetter(chars, offset, length, cellIndex);
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

    private void callHandler() {
        if (currentInstance != null) {
            try {
                flushChildren();
                handler.handle(currentInstance);
                currentInstance = null;
            } catch (Exception e) {
                rowHandlerErrorHandlers.handlerError(e, currentInstance);
            }
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
        if (breakDetector.broken()
                && breakDetector.isNotNull()) {
            createInstance();
        }
    }

    private void createInstance() {
        try {
            callHandler();
            currentInstance = instantiator.newInstance(delayedCellSetters);
            applyDelayedSetters();
        } catch (Exception e) {
            throw new MappingException(e.getMessage(), e);
        }
    }

    public void flush() {
        callHandler();
    }

    private void flushChildren() {
        for(CsvMapperWithBreakDetectorCellConsumer child: children) {
            child.flush();
        }
    }

}
