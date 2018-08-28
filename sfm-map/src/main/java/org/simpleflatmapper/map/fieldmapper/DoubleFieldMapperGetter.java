package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public interface DoubleFieldMapperGetter<S> {
    double getDouble(S s, MappingContext<?> mappingContext) throws Exception;
}
