package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public interface FieldMapperGetter<S, T> {
    T get(S s, MappingContext<?> context) throws Exception;
}
