package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public interface FloatFieldMapperGetter<S> {
    float getFloat(S s, MappingContext<?> mappingContext) throws Exception;
}
