package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public interface ShortFieldMapperGetter<S> {
    short getShort(S s, MappingContext<?> mappingContext) throws Exception;
}
