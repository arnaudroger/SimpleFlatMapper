package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public class BoxedCharacterFieldMapperGetter<T> implements CharacterFieldMapperGetter<T>, FieldMapperGetter<T, Character> {


    private final FieldMapperGetter<? super T, ? extends Character> delegate;

    public BoxedCharacterFieldMapperGetter(FieldMapperGetter<? super T, ? extends Character> delegate) {
        this.delegate = delegate;
    }

    @Override
    public char getCharacter(T target, MappingContext<?> mappingContext) throws Exception {
        final Character value = get(target, mappingContext);
        if (value != null) {
            return value.charValue();
        }
        return 0;
    }

    @Override
    public Character get(T target, MappingContext<?> context) throws Exception {
        return delegate.get(target, context);
    }
}
