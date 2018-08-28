package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public interface IntFieldMapperGetter<S> {
    int getInt(S s, MappingContext<?> mappingContext) throws Exception;
}
