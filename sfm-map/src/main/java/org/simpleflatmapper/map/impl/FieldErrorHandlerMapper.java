package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.MappingContext;


public final class FieldErrorHandlerMapper<S, T, K> implements FieldMapper<S, T> {

	private final FieldMapper<S, T> delegate;
	private final FieldMapperErrorHandler<? super K> errorHandler;
	private final K key;
	
	public FieldErrorHandlerMapper(K key, FieldMapper<S, T> delegate,
			FieldMapperErrorHandler<? super K> errorHandler) {
		super();
		this.key = key;
		this.delegate = delegate;
		this.errorHandler = errorHandler;
	}

	@Override
	public void mapTo(S source, T target, MappingContext<? super S> mappingContext)  {
		try {
			delegate.mapTo(source, target, mappingContext);
		} catch(Exception e) {
			errorHandler.errorMappingField(key, source, target, e);
		}
	}

    @Override
    public String toString() {
        return "FieldErrorHandlerMapper{delegate=" + delegate + '}';
	}
}
