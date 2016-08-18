package org.simpleflatmapper.converter;

import java.lang.reflect.Type;

public interface ConverterFactory<I, O> {

    Converter<? super I, ? extends O> newConverter(ConvertingTypes targetedTypes, Object... params);

    ConvertingScore score(ConvertingTypes targetedTypes);

    Type getFromType();
}
