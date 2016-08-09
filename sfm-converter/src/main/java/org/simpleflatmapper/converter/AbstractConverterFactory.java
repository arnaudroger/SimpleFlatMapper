package org.simpleflatmapper.converter;


public abstract class AbstractConverterFactory<I, O> implements ConverterFactory<I, O> {
    protected final ConvertingTypes convertingTypes;

    protected AbstractConverterFactory(Class<I> from, Class<O> to) {
        this(new ConvertingTypes(from, to));
    }

    protected AbstractConverterFactory(ConvertingTypes convertingTypes) {
        this.convertingTypes = convertingTypes;
    }

    @Override
    public int score(ConvertingTypes targetedTypes) {
        return this.convertingTypes.score(targetedTypes);
    }

}
