package org.simpleflatmapper.converter.test;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterFactory;
import org.simpleflatmapper.converter.ConverterFactoryProducer;
import org.simpleflatmapper.converter.ConvertingScore;
import org.simpleflatmapper.converter.ConvertingTypes;
import org.simpleflatmapper.util.Consumer;

import java.lang.reflect.Type;

public class ValidConverterFactoryProducer implements ConverterFactoryProducer {
    @Override
    public void produce(Consumer<ConverterFactory> consumer) {
        consumer.accept(new ConverterFactory() {
            @Override
            public Converter newConverter(ConvertingTypes targetedTypes, Object... params) {
                return null;
            }

            @Override
            public ConvertingScore score(ConvertingTypes targetedTypes) {
                return null;
            }

            @Override
            public Type getFromType() {
                return null;
            }
        });
    }
}
