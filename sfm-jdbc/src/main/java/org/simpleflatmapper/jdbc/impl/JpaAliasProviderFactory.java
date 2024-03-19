package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.AliasProviderProducer;
import org.simpleflatmapper.util.Consumer;

public class JpaAliasProviderFactory implements AliasProviderProducer {

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable e) {
        }
        return false;
    }

    @Override
    public void produce(Consumer<? super AliasProvider> consumer) {
        if (isClassPresent("javax.persistence.Column")) {
            consumer.accept(new JpaAliasProvider());
        }
        if (isClassPresent("jakarta.persistence.Column")) {
            consumer.accept(new JakartaAliasProvider());
        }
        consumer.accept(new SfmAliasProvider());
    }
}
