package org.simpleflatmapper.converter;

public interface ConverterFactory<I, O> {

    Converter<? super I, ? extends O> newConverter(ConvertingTypes targetedTypes, Object... params);

    int score(ConvertingTypes targetedTypes);

}
