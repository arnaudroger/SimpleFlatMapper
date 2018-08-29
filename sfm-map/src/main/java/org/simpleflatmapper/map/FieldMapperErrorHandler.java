package org.simpleflatmapper.map;

import org.simpleflatmapper.converter.Context;

public interface FieldMapperErrorHandler<K> {
	void errorMappingField(K key, Object source, Object target, Exception error, Context mappingContext) throws MappingException;
}
