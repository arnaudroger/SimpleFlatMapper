package org.simpleflatmapper.map;

import org.simpleflatmapper.converter.Context;

import java.util.ArrayList;

public class ResultFieldMapperErrorHandler<K> implements FieldMapperErrorHandler<K> {
	@SuppressWarnings("unchecked")
	public void errorMappingField(K key, Object source, Object target, Exception error, Context mappingContext) throws MappingException {
		ArrayList<Result.FieldError<K>> list = mappingContext.context(0);
		list.add(new Result.FieldError<K>(key, error));
	}
}
