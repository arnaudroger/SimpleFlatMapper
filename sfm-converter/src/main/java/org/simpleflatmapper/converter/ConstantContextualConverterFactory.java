package org.simpleflatmapper.converter;

public class ConstantContextualConverterFactory<I, O> extends AbstractContextualConverterFactory<I, O> {
    private final ContextualConverter<? super I, ? extends O> converter;

    public ConstantContextualConverterFactory(ConvertingTypes convertingTypes, ContextualConverter<? super I, ? extends O> converter) {
        super(convertingTypes);
        this.converter = converter;
    }

    @Override
    public ContextualConverter<? super I, ? extends O> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
        return converter;
    }

}