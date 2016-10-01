package org.simpleflatmapper.datastax.impl.mapping;

import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.AliasProviderProducer;
import org.simpleflatmapper.util.Consumer;

public class DatastaxAliasProviderFactory implements AliasProviderProducer {
    private boolean isActive() {
        try {
            Class.forName("com.datastax.driver.mapping.annotations.Table");
            return true;
        } catch (Throwable e) {}
        return false;
    }

    @Override
    public void produce(Consumer<? super AliasProvider> consumer) {
        if (isActive()) {
            consumer.accept(new DatastaxAliasProvider());
        }
    }
}
