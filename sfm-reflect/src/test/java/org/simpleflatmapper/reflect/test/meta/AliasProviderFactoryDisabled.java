package org.simpleflatmapper.reflect.test.meta;

import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.AliasProviderProducer;
import org.simpleflatmapper.util.Consumer;

public class AliasProviderFactoryDisabled implements AliasProviderProducer {
    @Override
    public void produce(Consumer<? super AliasProvider> consumer) {
        // does not produce anything
    }
}
