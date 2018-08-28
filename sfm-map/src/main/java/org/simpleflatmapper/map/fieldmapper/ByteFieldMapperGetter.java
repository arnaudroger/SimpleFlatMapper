package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public interface ByteFieldMapperGetter<S> {
    byte getByte(S s, MappingContext<?> mappingContext) throws Exception;
}
