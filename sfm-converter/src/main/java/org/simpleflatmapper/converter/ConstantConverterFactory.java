package org.simpleflatmapper.converter;

public class ConstantConverterFactory extends AbstractConverterFactory{
    private final Converter<?, ?> converter;

    public ConstantConverterFactory(ConvertingTypes convertingTypes, Converter<?, ?> converter) {
        super(convertingTypes);
        this.converter = converter;
    }

    @Override
    public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
        return converter;
    }

}