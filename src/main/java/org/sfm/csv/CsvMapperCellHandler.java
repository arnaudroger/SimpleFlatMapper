package org.sfm.csv;

import org.sfm.csv.parser.CharsCellHandler;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

public final class CsvMapperCellHandler<T> implements CharsCellHandler {

	private final DelayedCellSetter<T, ?>[] delayedCellSetters;
	private final CellSetter<T>[] setters;
	@SuppressWarnings("rawtypes")
	private final Instantiator<DelayedCellSetter[], T> instantiator;
	private final FieldMapperErrorHandler<Integer> fieldErrorHandler;
	private final RowHandlerErrorHandler rowHandlerErrorHandlers;
	private final RowHandler<T> handler;
	private final int flushIndex;
	private final int totalLength;
	private final ParsingContext parsingContext;

	private final int rowStart;
	private final int limit;
	
	
	
	private T currentInstance;
	private int cellIndex = 0;
	private int currentRow = 0;

	public CsvMapperCellHandler(
			@SuppressWarnings("rawtypes") Instantiator<DelayedCellSetter[], T> instantiator,
			DelayedCellSetter<T, ?>[] delayedCellSetters,
			CellSetter<T>[] setters,
			FieldMapperErrorHandler<Integer> fieldErrorHandler,
			RowHandlerErrorHandler rowHandlerErrorHandlers,
			RowHandler<T> handler, 
			ParsingContext parsingContext,
			int rowStart, int limit) {
		super();
		this.instantiator = instantiator;
		this.delayedCellSetters = delayedCellSetters;
		this.setters = setters;
		this.fieldErrorHandler = fieldErrorHandler;
		this.rowHandlerErrorHandlers = rowHandlerErrorHandlers;
		this.handler = handler;
		this.flushIndex = lastNonNullSetter(delayedCellSetters, setters);
		this.totalLength = delayedCellSetters.length + setters.length;
		this.rowStart = rowStart;
		this.limit = limit;
		this.parsingContext = parsingContext;
	}
	
	private int lastNonNullSetter(
			DelayedCellSetter<T, ?>[] dcs,
			CellSetter<T>[] cs) {
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

	@Override
	public boolean endOfRow() {
		endOfRow(cellIndex);
		cellIndex = 0;
		return continueProcessing();
	}
	
	private boolean continueProcessing() {
		currentRow++;
		boolean continueProcessing =  limit == -1 || (currentRow - rowStart) < limit;
		return continueProcessing;
	} 
	
	public void endOfRow(int cellIndex) {
		flush(cellIndex);
	}
	public void flush(int cellIndex) {
		if (cellIndex > 0) {
			T instance = currentInstance;
			if (instance == null) {
				instance = createInstance();
				applyDelayedSetters(instance);
				callHandler(instance);
			} else {
				applyDelayedSetters(instance);
				callHandler(instance);
				currentInstance = null;
			}
			this.cellIndex = -1;
		}
	}


	private void callHandler(T instance) {
		try {
			handler.handle(instance);
		} catch (Exception e) {
			rowHandlerErrorHandlers.handlerError(e, instance);
		}
	}

	private void applyDelayedSetters(T instance) {
		for(int i = 0; i < delayedCellSetters.length; i++) {
			DelayedCellSetter<T, ?> delayedSetter = delayedCellSetters[i];
			if (delayedSetter != null && delayedSetter.isSettable()) {
				try {
					delayedSetter.set(instance);
				} catch (Exception e) {
					rowHandlerErrorHandlers.handlerError(e, instance);
				}
			}
		}
	}

	private T createInstance() {
		try {
			return instantiator.newInstance(delayedCellSetters);
		} catch (Exception e) {
			throw new MappingException(e.getMessage(), e);
		}
	}

	private void newCellForDelayedSetter(char[] chars, int offset, int length, int cellIndex) {
		try {
			DelayedCellSetter<T, ?> delayedCellSetter = delayedCellSetters[cellIndex];
			if (delayedCellSetter != null) {
				delayedCellSetter.set(chars, offset, length, parsingContext);
			}
		} catch (Exception e) {
			fieldErrorHandler.errorMappingField(cellIndex, this, currentInstance, e);
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
				fieldErrorHandler.errorMappingField(cellIndex, this, currentInstance, e);
			}
		}
	}
	
	@Override
	public void end() {
		endOfRow();
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		if (rowStart == -1 || currentRow >= rowStart) {
			if (cellIndex == -1) {
				return;
			}
			newCell(chars, offset, length, cellIndex);
			if (cellIndex != -1) {
				cellIndex++;
			}
		}
	}
	
	public void newCell(char[] chars, int offset, int length, int cellIndex) {
		
		if (cellIndex < delayedCellSetters.length) {
			newCellForDelayedSetter(chars, offset, length, cellIndex);
		} else {
			if (currentInstance == null) {
				currentInstance = createInstance();
			}
			newCellForSetter(chars, offset, length, cellIndex);
		}
		
		if (cellIndex == flushIndex) {
			flush(cellIndex);
		}
	}
}