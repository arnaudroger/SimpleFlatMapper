package org.sfm.csv.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//IFJAVA8_START
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END

import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.CsvMapper;
import org.sfm.csv.CsvParser;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

public final class CsvMapperImpl<T> implements CsvMapper<T> {
	private final DelayedCellSetterFactory<T, ?>[] delayedCellSetters;
	private final CellSetter<T>[] setters;
	private final CsvColumnKey[] keys;
	
	private final Instantiator<DelayedCellSetter<T, ?>[], T> instantiator;
	private final FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler;
	private final RowHandlerErrorHandler rowHandlerErrorHandlers;
	private final ParsingContextFactory parsingContextFactory;
	
	public CsvMapperImpl(Instantiator<DelayedCellSetter<T, ?>[], T> instantiator,
			DelayedCellSetterFactory<T, ?>[] delayedCellSetters,
			CellSetter<T>[] setters,
			CsvColumnKey[] keys,
			ParsingContextFactory parsingContextFactory,
			FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler,
			RowHandlerErrorHandler rowHandlerErrorHandlers) {
		super();
		this.instantiator = instantiator;
		this.delayedCellSetters = delayedCellSetters;
		this.setters = setters;
		this.keys = keys;
		this.fieldErrorHandler = fieldErrorHandler;
		this.rowHandlerErrorHandlers = rowHandlerErrorHandlers;
		this.parsingContextFactory = parsingContextFactory;
	}

	@Override
	public <H extends RowHandler<T>> H forEach(final Reader reader, final H handler) throws IOException, MappingException {
		return forEach(reader, handler, 0);
	}
	
	@Override
	public <H extends RowHandler<T>> H forEach(final Reader reader, final H handler, final int rowStart) throws IOException, MappingException {
		return forEach(reader, handler, rowStart, -1);
	}
	
	@Override
	public <H extends RowHandler<T>> H forEach(final Reader reader, final H handler, final int rowStart, final int limit) throws IOException, MappingException {
		new CsvParser().parse(reader, newCellHandler(handler, rowStart, limit, true));
		return handler;
	}
	
	@Override
	public Iterator<T> iterate(Reader reader) {
		return iterate(reader, -1);
	}
	
	@Override
	public Iterator<T> iterate(Reader reader, int rowStart) {
		return new CsvIterator<T>(reader, this, rowStart);
	}
	
	//IFJAVA8_START
	@Override
	public Stream<T> stream(Reader reader) {
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterate(reader), Spliterator.DISTINCT | Spliterator.ORDERED);
		return StreamSupport.stream(spliterator, false);
	}
	//IFJAVA8_END

	protected CsvMapperCellHandler<T> newCellHandler(final RowHandler<T> handler) {
		return newCellHandler(handler, -1, -1, true);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected CsvMapperCellHandler<T> newCellHandler(final RowHandler<T> handler, int rowStart, int limit, boolean pushMode) {
		
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
						delegateCellSetter = new DelegateDelayedCellSetterFactory(marker, i);
						cellHandlers.put(marker.getMapper(), delegateCellSetter.getCellHandler());
					} else {
						delegateCellSetter = new DelegateDelayedCellSetterFactory(marker, bhandler, i);
					}
					outDelayedCellSetters[i] =  delegateCellSetter.newCellSetter(); 
				} else {
					outDelayedCellSetters[i] = delayedCellSetterFactory.newCellSetter();
				}
			}
		}

		
		CellSetter<T>[] outSetters = new CellSetter[setters.length];
		for(int i = 0; i < setters.length; i++) {
			if (setters[i] instanceof DelegateMarkerSetter) {
				DelegateMarkerSetter<?> marker = (DelegateMarkerSetter<?>) setters[i];
				
				CsvMapperCellHandler<?> bhandler = cellHandlers.get(marker.getMapper());
				
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
		
		
		
		
		return new CsvMapperCellHandler<T>(instantiator, 
				outDelayedCellSetters, 
				outSetters, keys,		
				fieldErrorHandler, 
				rowHandlerErrorHandlers, 
				handler, 
				parsingContextFactory.newContext(), 
				rowStart, limit, pushMode);
	}


}
