package org.sfm.csv;

import org.sfm.csv.parser.BytesCellHandler;
import org.sfm.csv.parser.CharsCellHandler;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.InstantiationMappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

final class CsvMapperCellHandler<T> implements BytesCellHandler, CharsCellHandler {

	private final DelayedCellSetter<T, ?>[] delayedCellSetters;
	private final CellSetter<T>[] setters;
	@SuppressWarnings("rawtypes")
	private final Instantiator<DelayedCellSetter[], T> instantiator;
	private final FieldMapperErrorHandler<Integer> fieldErrorHandler;
	private final RowHandlerErrorHandler rowHandlerErrorHandlers;
	private final RowHandler<T> handler;
	private final int flushIndex;
	private final int lastIndex;
	
	T value;
	int cellIndex = 0;
	boolean hasValue;

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
		this.lastIndex = flushIndex;
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
		flush();
		cellIndex = 0;
	}

	public void flush() {
		if (value == null) {
			if (hasValue) {
				try {
					value = instantiator.newInstance(delayedCellSetters);
				} catch (Exception e) {
					throw new InstantiationMappingException(e.getMessage(), e);
				}
			}
		}
		if (value != null) {
			for(int i = 0; i < delayedCellSetters.length; i++) {
				DelayedCellSetter<T, ?> delayedSetter = delayedCellSetters[i];
				if (delayedSetter != null && delayedSetter.isSettable()) {
					try {
						delayedSetter.set(value);
					} catch (Exception e) {
						rowHandlerErrorHandlers.handlerError(e, value);
					}
				}
			}
			try {
				handler.handle(value);
			} catch (Exception e) {
				rowHandlerErrorHandlers.handlerError(e, value);
			}
			value = null;
			hasValue = false;
		}
	}

	@Override
	public void newCell(byte[] bytes, int offset, int length) {
		newCell(bytes, offset, length, cellIndex++);
	}

	public void newCell(byte[] bytes, int offset, int length, int cellIndex) {
		hasValue = true;
		if (cellIndex > lastIndex) {
			return;
		}
		
		if (cellIndex < delayedCellSetters.length) {
			try {
				delayedCellSetters[cellIndex].set(bytes, offset, length);
			} catch (Exception e) {
				fieldErrorHandler.errorMappingField(cellIndex, this, value, e);
			}
		} else {
			if (value == null) {
				try {
					value = instantiator.newInstance(delayedCellSetters);
				} catch (Exception e) {
					throw new InstantiationMappingException(e.getMessage(), e);
				}
			}
			if (cellIndex < setters.length + delayedCellSetters.length) {
				try {
					setters[cellIndex - delayedCellSetters.length].set(value, bytes, offset, length);
				} catch (Exception e) {
					fieldErrorHandler.errorMappingField(cellIndex, this, value, e);
				}
			}
		}
		if (cellIndex == flushIndex) {
			flush();
		}
		cellIndex++;
	}

	@Override
	public void end() {
		endOfRow();
	}

	@Override
	public void newCell(char[] chars, int offset, int length) {
		newCell(chars, offset, length, cellIndex++);
	}
	public void newCell(char[] chars, int offset, int length, int cellIndex) {
		hasValue = true;
		if (cellIndex > lastIndex) {
			return;
		}
		
		if (cellIndex < delayedCellSetters.length) {
			try {
				delayedCellSetters[cellIndex].set(chars, offset, length);
			} catch (Exception e) {
				fieldErrorHandler.errorMappingField(cellIndex, this, value, e);
			}
		} else {
			if (value == null) {
				try {
					value = instantiator.newInstance(delayedCellSetters);
				} catch (Exception e) {
					throw new InstantiationMappingException(e.getMessage(), e);
				}
			}
			if (cellIndex < setters.length + delayedCellSetters.length) {
				try {
					setters[cellIndex - delayedCellSetters.length].set(value, chars, offset, length);
				} catch (Exception e) {
					fieldErrorHandler.errorMappingField(cellIndex, this, value, e);
				}
			}
		}
		if (cellIndex == flushIndex) {
			flush();
		}
		cellIndex++;
	}
}