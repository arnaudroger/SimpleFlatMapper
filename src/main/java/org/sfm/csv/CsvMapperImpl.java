package org.sfm.csv;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.sfm.csv.parser.CsvParser;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.RethrowFieldMapperErrorHandler;
import org.sfm.map.RethrowRowHandlerErrorHandler;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

public final class CsvMapperImpl<T> implements CsvMapper<T> {
	private final DelayedCellSetterFactory<T, ?>[] delayedCellSetters;
	private final CellSetter<T>[] setters;
	@SuppressWarnings("rawtypes")
	private final Instantiator<DelayedCellSetter[], T> instantiator;
	private final FieldMapperErrorHandler<Integer> fieldErrorHandler;
	private final RowHandlerErrorHandler rowHandlerErrorHandlers;
	
	public CsvMapperImpl(@SuppressWarnings("rawtypes") Instantiator<DelayedCellSetter[], T> instantiator,
			DelayedCellSetterFactory<T, ?>[] delayedCellSetters,
			CellSetter<T>[] setters,
			FieldMapperErrorHandler<Integer> fieldErrorHandler,
			RowHandlerErrorHandler rowHandlerErrorHandlers) {
		super();
		this.instantiator = instantiator;
		this.delayedCellSetters = delayedCellSetters;
		this.setters = setters;
		this.fieldErrorHandler = fieldErrorHandler;
		this.rowHandlerErrorHandlers = rowHandlerErrorHandlers;
	}

	@SuppressWarnings("rawtypes")
	public CsvMapperImpl(Instantiator<DelayedCellSetter[], T> instantiator,
			DelayedCellSetterFactory<T, ?>[] delayedCellSetters,
			CellSetter<T>[] setters) {
		this(instantiator, delayedCellSetters, setters, new RethrowFieldMapperErrorHandler<Integer>(), new RethrowRowHandlerErrorHandler());
	}

	@Override
	public <H extends RowHandler<T>> H forEach(final InputStream is, final H handler) throws IOException, MappingException {
		new CsvParser().parse(is, newCellHandler(handler));
		return handler;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected CsvMapperBytesCellHandler<T> newCellHandler(final RowHandler<T> handler) {
		
		CellSetter<T>[] outSetters = new CellSetter[setters.length];
		DelayedCellSetter<T, ?>[] outDelayedCellSetters = new DelayedCellSetter[delayedCellSetters.length];
		
		
		Map<CsvMapper<?>, CsvMapperBytesCellHandler<?>> cellHandlers = new HashMap<CsvMapper<?>, CsvMapperBytesCellHandler<?>>();

		for(int i = 0; i < delayedCellSetters.length; i++) {
			DelayedCellSetterFactory<T, ?> delayedCellSetterFactory = delayedCellSetters[i];
			if (delayedCellSetterFactory != null) {
				if (delayedCellSetterFactory instanceof DelegateMarkerDelayedCellSetter) {
					DelegateMarkerDelayedCellSetter<T, ?> marker = (DelegateMarkerDelayedCellSetter<T, ?>) delayedCellSetterFactory;
					
					CsvMapperBytesCellHandler<?> bhandler = cellHandlers.get(marker.getMapper());
					
					DelegateDelayedCellSetterFactory<T, ?> delegateCellSetter;
					
					if(bhandler == null) {
						delegateCellSetter = new DelegateDelayedCellSetterFactory(marker, i);
						cellHandlers.put(marker.getMapper(), delegateCellSetter.getBytesCellHandler());
					} else {
						delegateCellSetter = new DelegateDelayedCellSetterFactory(marker, bhandler, i);
					}
					outDelayedCellSetters[i] =  delegateCellSetter.newCellSetter(); 
				} else {
					outDelayedCellSetters[i] = delayedCellSetterFactory.newCellSetter();
				}
			}
		}

		
		for(int i = 0; i < setters.length; i++) {
			if (setters[i] instanceof DelegateMarkerSetter) {
				DelegateMarkerSetter<?> marker = (DelegateMarkerSetter<?>) setters[i];
				
				CsvMapperBytesCellHandler<?> bhandler = cellHandlers.get(marker.getMapper());
				
				DelegateCellSetter<T> delegateCellSetter;
				
				if(bhandler == null) {
					delegateCellSetter = new DelegateCellSetter<T>(marker, i + delayedCellSetters.length);
					cellHandlers.put(marker.getMapper(), delegateCellSetter.getBytesCellHandler());
				} else {
					delegateCellSetter = new DelegateCellSetter<T>(marker, bhandler, i + delayedCellSetters.length);
				}
				outSetters[i] = delegateCellSetter; 
			} else {
				outSetters[i] = setters[i];
			}
		}
		
		
		
		
		return new CsvMapperBytesCellHandler<T>(instantiator, 
				outDelayedCellSetters, 
				outSetters, 				
				fieldErrorHandler, 
				rowHandlerErrorHandlers, handler);
	}


}
