package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.MappingContext;


public final class FieldErrorHandlerMapper<S, T, K> implements FieldMapper<S, T> {

	public final FieldMapper<S, T> delegate;
	public final FieldMapperErrorHandler<? super K> errorHandler;
	public final K key;
	
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
			errorHandler.errorMappingField(key, source, target, e, mappingContext);
		}
	}

    @Override
    public String toString() {
        return "FieldErrorHandlerMapper{delegate=" + delegate + '}';
	}
	
	
	public  static <S, T, K extends FieldKey<K>> FieldMapper<S, T> of(K key, FieldMapper<S, T> delegate,
															   FieldMapperErrorHandler<? super K> errorHandler) {
		return new FieldErrorHandlerMapper<S, T, K>(key, delegate, errorHandler);
	}
}
