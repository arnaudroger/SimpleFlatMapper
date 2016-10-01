package org.simpleflatmapper.converter;


import org.simpleflatmapper.util.ProducerServiceLoader;

public interface ConverterFactoryProducer
        extends ProducerServiceLoader.Producer<ConverterFactory<?, ?>> {
}
