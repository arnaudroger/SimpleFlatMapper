package org.sfm.csv;

import java.io.InputStream;

import org.sfm.csv.parser.BytesCellHandler;
import org.sfm.csv.parser.CsvParser;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.InstantiationMappingException;
import org.sfm.map.RethrowFieldMapperErrorHandler;
import org.sfm.map.RethrowRowHandlerErrorHandler;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

public final class CsvMapperImpl<T> implements CsvMapper<T> {
	
	private final CellSetter<T>[] setters;
	private final Instantiator<BytesCellHandler, T> instantiator;
	private final FieldMapperErrorHandler<Integer> fieldErrorHandler = new RethrowFieldMapperErrorHandler<Integer>();
	private final RowHandlerErrorHandler rowHandlerErrorHandlers = new RethrowRowHandlerErrorHandler();
	
	public CsvMapperImpl(Instantiator<BytesCellHandler, T> instantiator,
			CellSetter<T>[] setters) {
		super();
		this.instantiator = instantiator;
		this.setters = setters;
	}

	@Override
	public <H extends RowHandler<T>> H forEach(final InputStream is, final H handler) throws Exception {
		new CsvParser().parse(is, newCellHandler(handler));
		return handler;
	}

	private BytesCellHandler newCellHandler(final RowHandler<T> handler) {
		return new BytesCellHandler() {
			T value;
			int cellIndex = 0;
			@Override
			public void endOfRow() {
				if (value != null) {
					try {
						handler.handle(value);
					} catch (Exception e) {
						rowHandlerErrorHandlers.handlerError(e, value);
					}
					value = null;
				}
				cellIndex = 0;
			}
			
			@Override
			public void newCell(byte[] bytes, int offset, int length) {
				if (value == null) {
					try {
						value = instantiator.newInstance(this);
					} catch (Exception e) {
						throw new InstantiationMappingException(e.getMessage(), e);
					}
				}
				if (cellIndex < setters.length) {
					try {
						setters[cellIndex].set(value, bytes, offset, length);
					} catch (Exception e) {
						fieldErrorHandler.errorMappingField(cellIndex, this, value, e);
					}
				}
				cellIndex++;
			}

			@Override
			public void end() {
				endOfRow();
			}
		};
	}
}
