package org.simpleflatmapper.map.property;

import org.simpleflatmapper.converter.ContextualConverterFactory;

public class ContextualConverterFactoryProperty<I, O> {

    public final ContextualConverterFactory<I, O> factory;

    public ContextualConverterFactoryProperty(ContextualConverterFactory<I, O> factory) {
        this.factory = factory;
    }

    public static <I, O> ContextualConverterFactoryProperty of(ContextualConverterFactory<I, O> converter) {
        return new ContextualConverterFactoryProperty<I, O>(converter);
    }
}
