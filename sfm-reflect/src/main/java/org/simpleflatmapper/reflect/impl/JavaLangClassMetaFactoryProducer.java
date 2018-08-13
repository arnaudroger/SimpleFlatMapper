package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ConstructorPropertyMeta;
import org.simpleflatmapper.reflect.meta.ObjectClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.UnaryFactory;

import java.util.Collections;

public class JavaLangClassMetaFactoryProducer implements ReflectionService.ClassMetaFactoryProducer {
    @Override
    public void produce(Consumer<? super UnaryFactory<ReflectionService, ClassMeta<?>>> consumer) {
        predefined(String.class, consumer);
        predefined(Boolean.class, consumer);
        predefined(Byte.class, consumer);
        predefined(Character.class, consumer);
        predefined(Short.class, consumer);
        predefined(Integer.class, consumer);
        predefined(Long.class, consumer);
        predefined(Float.class, consumer);
        predefined(Double.class, consumer);
    }

    private <T> void predefined(final Class<T> target,
                                Consumer<? super UnaryFactory<ReflectionService, ClassMeta<?>>> consumer) {
        consumer.accept(new UnaryFactory<ReflectionService, ClassMeta<?>>() {
            @Override
            public ClassMeta<?> newInstance(ReflectionService reflectionService) {
                return new ObjectClassMeta<T>(
                        target,
                        Collections.<InstantiatorDefinition>emptyList(),
                        Collections.<ConstructorPropertyMeta<T, ?>>emptyList(),
                        Collections.<String, String>emptyMap(),
                        Collections.<PropertyMeta<T, ?>>emptyList(),
                        reflectionService, false);
            }
        });
    }
}
