package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public class BoxedCharacterContextualGetter<T> implements CharacterContextualGetter<T>, ContextualGetter<T, Character> {


    private final ContextualGetter<? super T, ? extends Character> delegate;

    public BoxedCharacterContextualGetter(ContextualGetter<? super T, ? extends Character> delegate) {
        this.delegate = delegate;
    }

    @Override
    public char getCharacter(T target, Context mappingContext) throws Exception {
        final Character value = get(target, mappingContext);
        if (value != null) {
            return value.charValue();
        }
        return 0;
    }

    @Override
    public Character get(T target, Context context) throws Exception {
        return delegate.get(target, context);
    }
}
