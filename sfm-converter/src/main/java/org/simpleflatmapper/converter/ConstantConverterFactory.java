package org.simpleflatmapper.converter;

public class ConstantConverterFactory<I, O> extends AbstractConverterFactory<I, O> {
    private final Converter<? super I, ? extends O> converter;

    public ConstantConverterFactory(ConvertingTypes convertingTypes, Converter<? super I, ? extends O> converter) {
        super(convertingTypes);
        this.converter = converter;
    }

    @Override
    public Converter<? super I, ? extends O> newConverter(ConvertingTypes targetedTypes, Object... params) {
        return converter;
    }

}