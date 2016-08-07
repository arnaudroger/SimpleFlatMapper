package org.simpleflatmapper.converter;

public interface ConverterFactory {

    Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params);

    int score(ConvertingTypes targetedTypes);
}
