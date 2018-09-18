package org.simpleflatmapper.converter;

import java.lang.reflect.Type;

public class ContextualConverterFactoryAdapter<I, O> implements ContextualConverterFactory<I, O> {
    private final ConverterFactory<I, O> converterFactory;

    public ContextualConverterFactoryAdapter(ConverterFactory<I, O> converterFactory) {
        this.converterFactory = converterFactory;
    }

    @Override
    public ContextualConverter<? super I, ? extends O> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
        return new ContextualConverterAdapter<I, O>(converterFactory.newConverter(targetedTypes, params));
    }

    @Override
    public ConvertingScore score(ConvertingTypes targetedTypes) {
        return converterFactory.score(targetedTypes);
    }

    @Override
    public Type getFromType() {
        return converterFactory.getFromType();
    }
}
