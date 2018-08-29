package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public interface CharacterContextualGetter<S> {
    char getCharacter(S s, Context mappingContext) throws Exception;
}
