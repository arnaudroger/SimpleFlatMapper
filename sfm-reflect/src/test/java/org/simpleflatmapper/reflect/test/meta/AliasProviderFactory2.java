package org.simpleflatmapper.reflect.test.meta;

import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.AliasProviderProducer;
import org.simpleflatmapper.reflect.meta.Table;
import org.simpleflatmapper.util.Consumer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AliasProviderFactory2 implements AliasProviderProducer {
    @Override
    public void produce(Consumer<? super AliasProvider> consumer) {
        consumer.accept(new AliasProvider2());

    }

    public static class AliasProvider2 implements AliasProvider {
        @Override
        public String getAliasForMethod(Method method) {
            return null;
        }

        @Override
        public String getAliasForField(Field field) {
            return null;
        }

        @Override
        public Table getTable(Class<?> target) {
            return null;
        }
    }
}
