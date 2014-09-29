package org.sfm.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
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
	public <H extends RowHandler<T>> H forEach(final InputStream is, final H handler, final Charset charset) throws IOException, MappingException {
		new CsvParser().parse(is, newCellHandler(handler, new DecoderContext(charset)));
		return handler;
	}
	
	@Override
	public <H extends RowHandler<T>> H forEach(final InputStream is, final H handler) throws IOException, MappingException {
		return forEach(is, handler, Charset.forName("UTF-8"));
	}
	
	@Override
	public <H extends RowHandler<T>> H forEach(final Reader reader, final H handler) throws IOException, MappingException {
		new CsvParser().parse(reader, newCellHandler(handler, null));
		return handler;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected CsvMapperCellHandler<T> newCellHandler(final RowHandler<T> handler, DecoderContext decoderContext) {
		
		CellSetter<T>[] outSetters = new CellSetter[setters.length];
		DelayedCellSetter<T, ?>[] outDelayedCellSetters = new DelayedCellSetter[delayedCellSetters.length];
		
		
		Map<CsvMapper<?>, CsvMapperCellHandler<?>> cellHandlers = new HashMap<CsvMapper<?>, CsvMapperCellHandler<?>>();

		for(int i = 0; i < delayedCellSetters.length; i++) {
			DelayedCellSetterFactory<T, ?> delayedCellSetterFactory = delayedCellSetters[i];
			if (delayedCellSetterFactory != null) {
				if (delayedCellSetterFactory instanceof DelegateMarkerDelayedCellSetter) {
					DelegateMarkerDelayedCellSetter<T, ?> marker = (DelegateMarkerDelayedCellSetter<T, ?>) delayedCellSetterFactory;
					
					CsvMapperCellHandler<?> bhandler = cellHandlers.get(marker.getMapper());
					
					DelegateDelayedCellSetterFactory<T, ?> delegateCellSetter;
					
					if(bhandler == null) {
						delegateCellSetter = new DelegateDelayedCellSetterFactory(marker, i, decoderContext);
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
				
				CsvMapperCellHandler<?> bhandler = cellHandlers.get(marker.getMapper());
				
				DelegateCellSetter<T> delegateCellSetter;
				
				if(bhandler == null) {
					delegateCellSetter = new DelegateCellSetter<T>(marker, i + delayedCellSetters.length, decoderContext);
					cellHandlers.put(marker.getMapper(), delegateCellSetter.getBytesCellHandler());
				} else {
					delegateCellSetter = new DelegateCellSetter<T>(marker, bhandler, i + delayedCellSetters.length);
				}
				outSetters[i] = delegateCellSetter; 
			} else {
				outSetters[i] = setters[i];
			}
		}
		
		
		
		
		
		return new CsvMapperCellHandler<T>(instantiator, 
				outDelayedCellSetters, 
				outSetters, 				
				fieldErrorHandler, 
				rowHandlerErrorHandlers, handler, decoderContext);
	}


}
