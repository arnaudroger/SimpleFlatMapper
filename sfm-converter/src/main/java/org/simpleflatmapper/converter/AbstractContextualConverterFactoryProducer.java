package org.simpleflatmapper.converter;


import org.simpleflatmapper.util.Consumer;

public abstract class AbstractContextualConverterFactoryProducer implements ContextualConverterFactoryProducer {

    protected  <I, O> void constantConverter(
            Consumer<? super ContextualConverterFactory<?, ?>> consumer,
            Class<I> from,
            Class<O> to,
            ContextualConverter<I, O> converter) {
        consumer.accept(new ConstantContextualConverterFactory<I, O>(new ConvertingTypes(from, to), converter));
    }

    protected <I, O> void factoryConverter(
            Consumer<? super ContextualConverterFactory<?, ?>> consumer,
            ContextualConverterFactory<?, ?> converterFactory) {
        consumer.accept(converterFactory);
    }

}
