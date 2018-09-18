package org.simpleflatmapper.converter;


import java.lang.reflect.Type;

public abstract class AbstractContextualConverterFactory<I, O> implements ContextualConverterFactory<I, O> {
    protected final ConvertingTypes convertingTypes;

    protected AbstractContextualConverterFactory(Class<? super I> from, Class<? extends O> to) {
        this(new ConvertingTypes(from, to));
    }

    protected AbstractContextualConverterFactory(ConvertingTypes convertingTypes) {
        this.convertingTypes = convertingTypes;
    }

    @Override
    public ConvertingScore score(ConvertingTypes targetedTypes) {
        return this.convertingTypes.score(targetedTypes);
    }

    @Override
    public Type getFromType() {
        return this.convertingTypes.getFrom();
    }
}
