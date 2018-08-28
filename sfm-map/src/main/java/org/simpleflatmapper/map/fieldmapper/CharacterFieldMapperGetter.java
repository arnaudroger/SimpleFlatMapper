package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public interface CharacterFieldMapperGetter<S> {
    char getCharacter(S s, MappingContext<?> mappingContext) throws Exception;
}
