package org.simpleflatmapper.converter;

public abstract class AbstractConverterFactory implements ConverterFactory{
    protected final ConvertingTypes convertingTypes;

    protected AbstractConverterFactory(Class<?> from, Class<?> to) {
        this(new ConvertingTypes(from, to));
    }

    protected AbstractConverterFactory(ConvertingTypes convertingTypes) {
        this.convertingTypes = convertingTypes;
    }

    @Override
    public int score(ConvertingTypes targetedTypes) {
        return this.convertingTypes.score(targetedTypes);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "convertingTypes=" + convertingTypes +
                '}';
    }
}
