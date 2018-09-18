package org.simpleflatmapper.converter;


import org.simpleflatmapper.util.ProducerServiceLoader;

public interface ContextualConverterFactoryProducer
        extends ProducerServiceLoader.Producer<ContextualConverterFactory<?, ?>> {
}
