package org.simpleflatmapper.converter;


import org.simpleflatmapper.util.Consumer;

public interface ConverterFactoryProducer {
    void produce(Consumer<ConverterFactory> consumer);
}
