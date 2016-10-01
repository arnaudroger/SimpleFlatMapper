package org.simpleflatmapper.converter;


import org.simpleflatmapper.util.Consumer;

public abstract class AbstractConverterFactoryProducer implements ConverterFactoryProducer {

    protected  <I, O> void constantConverter(
            Consumer<? super ConverterFactory<?, ?>> consumer,
            Class<I> from,
            Class<O> to,
            Converter<I, O> converter) {
        consumer.accept(new ConstantConverterFactory<I, O>(new ConvertingTypes(from, to), converter));
    }

    protected <I, O> void factoryConverter(
            Consumer<? super ConverterFactory<?, ?>> consumer,
            ConverterFactory<?, ?> converterFactory) {
        consumer.accept(converterFactory);
    }

}
