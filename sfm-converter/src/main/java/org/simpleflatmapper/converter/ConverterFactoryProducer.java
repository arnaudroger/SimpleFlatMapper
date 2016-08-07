package org.simpleflatmapper.converter;

import java.util.function.Consumer;

public interface ConverterFactoryProducer {
    void produce(Consumer<ConverterFactory> consumer);
}
