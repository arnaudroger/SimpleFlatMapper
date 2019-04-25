package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.AliasProviderProducer;
import org.simpleflatmapper.util.Consumer;

public class JpaAliasProviderFactory implements AliasProviderProducer {
    private boolean isActive() {
        try {
            Class.forName("javax.persistence.Column");
            return true;
        } catch (Throwable e) {
        }
        return false;
    }

    @Override
    public void produce(Consumer<? super AliasProvider> consumer) {
        if (isActive()) {
            consumer.accept(new JpaAliasProvider());
        }
        consumer.accept(new SfmAliasProvider());
    }
}
