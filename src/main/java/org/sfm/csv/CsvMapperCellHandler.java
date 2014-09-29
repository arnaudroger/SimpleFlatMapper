package org.sfm.csv;

import org.sfm.csv.parser.BytesCellHandler;
import org.sfm.csv.parser.CharsCellHandler;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.InstantiationMappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

public final class CsvMapperCellHandler<T> implements BytesCellHandler, CharsCellHandler {

	private final DelayedCellSetter<T, ?>[] delayedCellSetters;
	private final CellSetter<T>[] setters;
	@SuppressWarnings("rawtypes")
	private final Instantiator<DelayedCellSetter[], T> instantiator;
	private final FieldMapperErrorHandler<Integer> fieldErrorHandler;
	private final RowHandlerErrorHandler rowHandlerErrorHandlers;
	private final RowHandler<T> handler;
	private final int flushIndex;
	
	T currentInstance;
	int cellIndex = 0;
	
	private final int totalLength;

	public CsvMapperCellHandler(
			@SuppressWarnings("rawtypes") Instantiator<DelayedCellSetter[], T> instantiator,
			DelayedCellSetter<T, ?>[] delayedCellSetters,
			CellSetter<T>[] setters,
			FieldMapperErrorHandler<Integer> fieldErrorHandler,
			RowHandlerErrorHandler rowHandlerErrorHandlers,
			RowHandler<T> handler) {
		super();
		this.instantiator = instantiator;
		this.delayedCellSetters = delayedCellSetters;
		this.setters = setters;
		this.fieldErrorHandler = fieldErrorHandler;
		this.rowHandlerErrorHandlers = rowHandlerErrorHandlers;
		this.handler = handler;
		this.flushIndex = lastNonNullSetter(delayedCellSetters, setters);
		this.totalLength = delayedCellSetters.length + setters.length;
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
	public void endOfRow() {
		endOfRow(cellIndex);
		cellIndex = 0;
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
			throw new InstantiationMappingException(e.getMessage(), e);
		}
	}

	private void newCellForDelayedSetter(byte[] bytes, int offset, int length, int cellIndex) {
		try {
			delayedCellSetters[cellIndex].set(bytes, offset, length);
		} catch (Exception e) {
			fieldErrorHandler.errorMappingField(cellIndex, this, currentInstance, e);
		}
	}

	private void newCellForDelayedSetter(char[] chars, int offset, int length, int cellIndex) {
		try {
			delayedCellSetters[cellIndex].set(chars, offset, length);
		} catch (Exception e) {
			fieldErrorHandler.errorMappingField(cellIndex, this, currentInstance, e);
		}
	}

	private void newCellForSetter(byte[] bytes, int offset, int length, int cellIndex) {
		if (cellIndex < totalLength) {
			try {
				setters[cellIndex - delayedCellSetters.length].set(currentInstance, bytes, offset, length);
			} catch (Exception e) {
				fieldErrorHandler.errorMappingField(cellIndex, this, currentInstance, e);
			}
		}
	}

	private void newCellForSetter(char[] chars, int offset, int length, int cellIndex) {
		if (cellIndex < totalLength) {
			try {
				setters[cellIndex - delayedCellSetters.length].set(currentInstance, chars, offset, length);
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
		if (cellIndex == -1) {
			return;
		}
		newCell(chars, offset, length, cellIndex);
		cellIndex++;
	}
	
	@Override
	public void newCell(byte[] bytes, int offset, int length) {
		if (cellIndex == -1) {
			return;
		}
		newCell(bytes, offset, length, cellIndex);
		cellIndex ++;
	}

	public void newCell(byte[] bytes, int offset, int length, int cellIndex) {
		if (cellIndex < delayedCellSetters.length) {
			newCellForDelayedSetter(bytes, offset, length, cellIndex);
		} else {
			if (currentInstance == null) {
				currentInstance = createInstance();
			}
			newCellForSetter(bytes, offset, length, cellIndex);
		}
		
		if (cellIndex == flushIndex) {
			flush(cellIndex);
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
		
		cellIndex++;
	}
}