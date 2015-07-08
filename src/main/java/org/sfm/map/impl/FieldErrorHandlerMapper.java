package org.sfm.map.impl;

import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MappingContext;


public final class FieldErrorHandlerMapper<S, T, K> implements FieldMapper<S, T> {

	private final FieldMapper<S, T> delegate;
	private final FieldMapperErrorHandler<K> errorHandler;
	private final K key;
	
	public FieldErrorHandlerMapper(K key, FieldMapper<S, T> delegate,
			FieldMapperErrorHandler<K> errorHandler) {
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
        return "FieldErrorHandlerMapper{" +
                "delegate=" + delegate +
                '}';
    }
}
