package org.sfm.csv;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.sfm.csv.cell.DelayedSetterImpl;
import org.sfm.csv.parser.CsvParser;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.RethrowFieldMapperErrorHandler;
import org.sfm.map.RethrowRowHandlerErrorHandler;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

public final class CsvMapperImpl<T> implements CsvMapper<T> {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static final DelayedSetter NULL_DELAYED_SETTER = new DelayedSetterImpl(null, null);
	
	private final DelayedCellSetter<T, ?>[] delayedCellSetters;
	private final CellSetter<T>[] setters;
	@SuppressWarnings("rawtypes")
	private final Instantiator<DelayedSetter[], T> instantiator;
	private final FieldMapperErrorHandler<Integer> fieldErrorHandler;
	private final RowHandlerErrorHandler rowHandlerErrorHandlers;
	
	public CsvMapperImpl(@SuppressWarnings("rawtypes") Instantiator<DelayedSetter[], T> instantiator,
			DelayedCellSetter<T, ?>[] delayedCellSetters,
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
	public CsvMapperImpl(Instantiator<DelayedSetter[], T> instantiator,
			DelayedCellSetter<T, ?>[] delayedCellSetters,
			CellSetter<T>[] setters) {
		this(instantiator, delayedCellSetters, setters, new RethrowFieldMapperErrorHandler<Integer>(), new RethrowRowHandlerErrorHandler());
	}

	@Override
	public <H extends RowHandler<T>> H forEach(final InputStream is, final H handler) throws Exception {
		new CsvParser().parse(is, newCellHandler(handler));
		return handler;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected CsvMapperBytesCellHandler<T> newCellHandler(final RowHandler<T> handler) {
		
		CellSetter<T>[] outSetters = new CellSetter[setters.length];
		DelayedCellSetter<T, ?>[] outDelayedCellSetters = new DelayedCellSetter[delayedCellSetters.length];
		
		
		Map<CsvMapper<?>, CsvMapperBytesCellHandler<?>> cellHandlers = new HashMap<CsvMapper<?>, CsvMapperBytesCellHandler<?>>();

		for(int i = 0; i < delayedCellSetters.length; i++) {
			if (delayedCellSetters[i] instanceof DelegateMarkerDelayedCellSetter) {
				DelegateMarkerDelayedCellSetter<T, ?> marker = (DelegateMarkerDelayedCellSetter<T, ?>) delayedCellSetters[i];
				
				CsvMapperBytesCellHandler<?> bhandler = cellHandlers.get(marker.getMapper());
				
				DelegateDelayedCellSetter<T, ?> delegateCellSetter;
				
				if(bhandler == null) {
					delegateCellSetter = new DelegateDelayedCellSetter(marker, i);
					cellHandlers.put(marker.getMapper(), delegateCellSetter.getBytesCellHandler());
				} else {
					delegateCellSetter = new DelegateDelayedCellSetter(marker, bhandler, i);
				}
				outDelayedCellSetters[i] =  delegateCellSetter; 
			} else {
				outDelayedCellSetters[i] = delayedCellSetters[i];
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
