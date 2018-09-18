package org.simpleflatmapper.converter;

import java.lang.reflect.Type;

public interface ContextualConverterFactory<I, O> {

    ContextualConverter<? super I, ? extends O> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params);

    ConvertingScore score(ConvertingTypes targetedTypes);

    Type getFromType();
}
