package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public interface BooleanFieldMapperGetter<S> {
    boolean getBoolean(S s, MappingContext<?> mappingContext) throws Exception;
}
