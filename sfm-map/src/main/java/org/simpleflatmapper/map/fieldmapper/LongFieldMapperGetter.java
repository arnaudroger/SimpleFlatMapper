package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public interface LongFieldMapperGetter<S> {
    long getLong(S s, MappingContext<?> mappingContext) throws Exception;
}
