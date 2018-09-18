package org.simpleflatmapper.converter.test;

import org.simpleflatmapper.converter.AbstractContextualConverterFactoryProducer;
import org.simpleflatmapper.converter.AbstractConverterFactoryProducer;
import org.simpleflatmapper.converter.ContextualConverterFactory;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterFactory;
import org.simpleflatmapper.util.Consumer;

public class FooConverterFactoryProducer extends AbstractConverterFactoryProducer {
    @Override
    public void produce(Consumer<? super ConverterFactory<?, ?>> consumer) {
        constantConverter(consumer, Bar.class, Foo.class, new Converter<Bar, Foo>() {
            @Override
            public Foo convert(Bar in) throws Exception {
                return new Foo(in);
            }
        });
    }
}
